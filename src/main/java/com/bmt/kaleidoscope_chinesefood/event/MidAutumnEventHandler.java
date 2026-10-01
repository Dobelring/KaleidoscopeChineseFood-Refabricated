package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.mojang.serialization.Codec;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * 中秋登录礼（官方 1.1.13 新增）：活动期（各年中秋起 3 天）内每天首次登录送一个月饼，
 * 并授予 mid_autumn_festival 进度、发金色祝福消息。
 * 官方 NeoForge 版把领取标记存 PlayerPersisted（跨死亡/重登），Fabric 无等价物，
 * 这里改用主世界 SavedData 按玩家 UUID 记录。26.x 的存储是 SavedDataType(Identifier,
 * Supplier, Codec, DataFixTypes) 记录 + Codec 序列化；DataFixTypes 必须给真实枚举值
 * （读存档路径会对它调 update，存档版本恒等时 DFU 空跑、不碰我们的数据）。
 */
public class MidAutumnEventHandler {
    private static final Map<Integer, LocalDate> MID_AUTUMN_DATES = Map.of(
            2026, LocalDate.of(2026, 9, 25),
            2027, LocalDate.of(2027, 9, 15),
            2028, LocalDate.of(2028, 10, 3),
            2029, LocalDate.of(2029, 9, 22),
            2030, LocalDate.of(2030, 9, 12),
            2031, LocalDate.of(2031, 10, 1),
            2032, LocalDate.of(2032, 9, 19)
    );
    private static final int FESTIVAL_DAYS = 3;
    private static final int GRANT_DELAY_TICKS = 60;
    private static final SavedDataType<MidAutumnClaimData> CLAIM_DATA = new SavedDataType<>(
            KaleidoscopeChineseFood.id("mid_autumn"),
            MidAutumnClaimData::new,
            MidAutumnClaimData.CODEC,
            DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES
    );
    private static final Set<UUID> PENDING_PLAYERS = ConcurrentHashMap.newKeySet();

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerLoggedIn(handler.player));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> PENDING_PLAYERS.remove(handler.player.getUUID()));
        ServerTickEvents.END_SERVER_TICK.register(MidAutumnEventHandler::onServerTick);
    }

    private static void onPlayerLoggedIn(ServerPlayer player) {
        LocalDate today = LocalDate.now();
        if (isInFestival(today)) {
            String todayKey = today.format(DateTimeFormatter.BASIC_ISO_DATE);
            if (!todayKey.equals(getClaimedKey(player))) {
                PENDING_PLAYERS.add(player.getUUID());
            }
        }
    }

    private static void onServerTick(MinecraftServer server) {
        if (PENDING_PLAYERS.isEmpty()) {
            return;
        }
        // 迭代副本：DISCONNECT 事件可能在同一 tick 内修改集合
        for (UUID uuid : new ArrayList<>(PENDING_PLAYERS)) {
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player == null) {
                PENDING_PLAYERS.remove(uuid);
                continue;
            }
            if (player.tickCount < GRANT_DELAY_TICKS) {
                continue;
            }
            PENDING_PLAYERS.remove(uuid);
            LocalDate today = LocalDate.now();
            if (isInFestival(today)) {
                String todayKey = today.format(DateTimeFormatter.BASIC_ISO_DATE);
                if (!todayKey.equals(getClaimedKey(player))) {
                    setClaimedKey(player, todayKey);
                    ItemStack mooncake = new ItemStack(ModItems.MOONCAKE);
                    if (!player.getInventory().add(mooncake)) {
                        player.drop(mooncake, false);
                    }
                    grantAdvancement(player);
                    player.sendSystemMessage(Component.translatable("message.kaleidoscope_chinesefood.mid_autumn_gift").withStyle(ChatFormatting.GOLD));
                }
            }
        }
    }

    private static boolean isInFestival(LocalDate today) {
        LocalDate midAutumn = MID_AUTUMN_DATES.get(today.getYear());
        return midAutumn != null && !today.isBefore(midAutumn) && today.isBefore(midAutumn.plusDays(FESTIVAL_DAYS));
    }

    private static void grantAdvancement(ServerPlayer player) {
        AdvancementHolder advancement = player.level().getServer().getAdvancements().get(KaleidoscopeChineseFood.id("mid_autumn_festival"));
        if (advancement != null) {
            PlayerAdvancements advancements = player.getAdvancements();
            AdvancementProgress progress = advancements.getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    advancements.award(advancement, criterion);
                }
            }
        }
    }

    private static String getClaimedKey(ServerPlayer player) {
        return claimData(player.level().getServer()).claimed.get(player.getUUID());
    }

    private static void setClaimedKey(ServerPlayer player, String todayKey) {
        MidAutumnClaimData data = claimData(player.level().getServer());
        data.claimed.put(player.getUUID(), todayKey);
        data.setDirty();
    }

    private static MidAutumnClaimData claimData(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(CLAIM_DATA);
    }

    public static class MidAutumnClaimData extends SavedData {
        public static final Codec<MidAutumnClaimData> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING)
                .xmap(MidAutumnClaimData::fromRaw, MidAutumnClaimData::toRaw);

        final Map<UUID, String> claimed;

        public MidAutumnClaimData() {
            this(new HashMap<>());
        }

        public MidAutumnClaimData(Map<UUID, String> claimed) {
            this.claimed = claimed;
        }

        private static MidAutumnClaimData fromRaw(Map<String, String> raw) {
            Map<UUID, String> map = new HashMap<>();
            for (Map.Entry<String, String> entry : raw.entrySet()) {
                try {
                    map.put(UUID.fromString(entry.getKey()), entry.getValue());
                } catch (IllegalArgumentException ignored) {
                    // UUID 键损坏时跳过该条
                }
            }
            return new MidAutumnClaimData(map);
        }

        private static Map<String, String> toRaw(MidAutumnClaimData data) {
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<UUID, String> entry : data.claimed.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue());
            }
            return map;
        }
    }
}
