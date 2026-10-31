package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import com.bmt.kaleidoscope_chinesefood.init.ModTriggers;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * 官方 1.1.16 新增：岩浆畅游进度触发（有岩浆畅游效果 + 身处岩浆 + 游泳状态时达成）。
 * 原 Forge 用 PlayerTickEvent.Post；Fabric 无逐玩家 tick 事件，改为服务端 tick 里遍历玩家。
 */
public class ModTriggerEvents {
    public ModTriggerEvents() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(ModTriggerEvents::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.hasEffect(ModEffects.LAVA_SWIM) && player.isInLava() && player.isSwimming()) {
                ModTriggers.LAVA_SWIM.trigger(player);
            }
        }
    }
}
