package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.world.StrawBedTracker;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * 稻草床 Fabric 事件接线（官方 NeoForge StrawBedEventHandler 的对应实现）：
 * - 醒来（含被中断）：消耗稻草床并记统计（官方 PlayerWakeUpEvent）
 * - 登出：清理挂起/激活记录（官方 PlayerLoggedOutEvent）
 * - 不设置重生点：由 PlayerSetSpawnMixin 拦截（官方 PlayerSetSpawnEvent，Fabric 无对应事件）
 */
public class StrawBedSleepEvents {
    public static void register() {
        EntitySleepEvents.STOP_SLEEPING.register((entity, pos) -> StrawBedTracker.onWake(entity));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                StrawBedTracker.clear(handler.getPlayer()));
    }
}
