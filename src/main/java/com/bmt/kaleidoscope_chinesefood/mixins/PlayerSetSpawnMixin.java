package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.world.StrawBedTracker;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 稻草床不设置重生点：原版在成功入睡的当刻写重生点（官方用 NeoForge
 * PlayerSetSpawnEvent 取消，Fabric 无对应事件，直接拦 ServerPlayer#setRespawnPosition。
 * 26.x 签名为 (RespawnConfig, boolean)，与 1.21.1 的五参签名不同）。
 */
@Mixin(ServerPlayer.class)
public class PlayerSetSpawnMixin {
    @Inject(
            method = "setRespawnPosition(Lnet/minecraft/server/level/ServerPlayer$RespawnConfig;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void kaleidoscopeChineseFood$cancelStrawBedSpawn(CallbackInfo ci) {
        if (StrawBedTracker.shouldCancelSpawnSet((ServerPlayer) (Object) this)) {
            ci.cancel();
        }
    }
}
