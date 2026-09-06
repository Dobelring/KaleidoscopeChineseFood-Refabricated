package com.bmt.kaleidoscope_chinesefood.integration;

import com.github.ysbbbbbb.kaleidoscopedoll.block.DollBlock;
import com.github.ysbbbbbb.kaleidoscopedoll.event.ModRegisterEvent;
import com.github.ysbbbbbb.kaleidoscopedoll.item.DollEntityItem;
import com.github.ysbbbbbb.kaleidoscopedoll.item.DollItem;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 森罗物语：玩偶（kaleidoscope_doll）联动，与原版 1.1.10 语义一致：
 * 仅当玩偶模组在场、且下界/名酒同族模组都不在场时，把 6 个贡献者玩偶与 6 个实体玩偶
 * 以本模组命名空间注册（复用玩偶模组的 DollBlock/DollItem/DollEntityItem 类），
 * 登记 ModRegisterEvent.SPECIAL_TOOLTIPS 使玩偶模组显示贡献者署名，
 * 并加入玩偶模组的"作者玩偶/实体玩偶"创造栏。
 */
public class KaleidoscopeDollIntegration {
    private static final Map<ResourceLocation, Block> DOLL_BLOCKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Item> DOLL_ITEMS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Item> ENTITY_DOLL_ITEMS = new LinkedHashMap<>();
    private static final Map<String, String> DOLL_DEFINITIONS = Map.of(
            "doll_0", "contributor_0",
            "doll_1", "contributor_1",
            "doll_2", "contributor_2",
            "doll_3", "contributor_3",
            "doll_4", "contributor_4",
            "doll_5", "contributor_5"
    );

    private KaleidoscopeDollIntegration() {
    }

    public static void register() {
        FabricLoader loader = FabricLoader.getInstance();
        // 与原版一致：doll 在场且 nether/liquor 都不在场才注册（避免同族模组互相挤占贡献者玩偶）
        if (!loader.isModLoaded("kaleidoscope_doll")
                || loader.isModLoaded("kaleidoscope_nether")
                || loader.isModLoaded("kaleidoscope_world_liquor")) {
            return;
        }

        DOLL_DEFINITIONS.forEach((dollId, tooltipKey) -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", dollId);
            DollBlock block = new DollBlock();
            DOLL_BLOCKS.put(id, block);
            Registry.register(BuiltInRegistries.BLOCK, id, block);
            ModRegisterEvent.SPECIAL_TOOLTIPS.put(id, tooltipKey);
            DollItem item = new DollItem(block, tooltipKey);
            DOLL_ITEMS.put(id, item);
            Registry.register(BuiltInRegistries.ITEM, id, item);

            ResourceLocation entityDollId = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", "entity_" + dollId);
            Item entityDollItem = Registry.register(BuiltInRegistries.ITEM, entityDollId, new DollEntityItem());
            ENTITY_DOLL_ITEMS.put(id, entityDollItem);
        });

        // 玩偶模组的两个创造栏内容是注册时固定的 lambda（只扫它自己的 DOLL_ITEMS/DOLL_BLOCKS），
        // 联动物品不会被自动收录，需要用 ItemGroupEvents 手动加入（等价原版 BuildCreativeModeTabContentsEvent）
        ItemGroupEvents.modifyEntriesEvent(playerDollTabKey()).register(entries ->
                DOLL_ITEMS.values().forEach(entries::accept));
        ItemGroupEvents.modifyEntriesEvent(entityDollTabKey()).register(entries ->
                ENTITY_DOLL_ITEMS.forEach((blockId, entityDollItem) -> {
                    Block block = DOLL_BLOCKS.get(blockId);
                    if (block != null) {
                        entries.accept(DollEntityItem.createItemWithBlockState(block.defaultBlockState()));
                    }
                }));
    }

    /** 联动是否生效（客户端用：注册实体玩偶的动态渲染器） */
    public static boolean isIntegrated() {
        return !DOLL_BLOCKS.isEmpty();
    }

    /** 联动注册的实体玩偶物品（客户端用） */
    public static Collection<Item> getEntityDollItems() {
        return new ArrayList<>(ENTITY_DOLL_ITEMS.values());
    }

    private static ResourceKey<CreativeModeTab> playerDollTabKey() {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath("kaleidoscope_doll", "player_doll"));
    }

    private static ResourceKey<CreativeModeTab> entityDollTabKey() {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath("kaleidoscope_doll", "entity_doll"));
    }
}
