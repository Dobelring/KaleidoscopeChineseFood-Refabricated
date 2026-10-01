package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.world.StrawBedTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 稻草床不设置重生点：原版在成功入睡的当刻写重生点（官方用 NeoForge
 * PlayerSetSpawnEvent 取消，Fabric 无对应事件，直接拦 ServerPlayer#setRespawnPosition）。
 */
@Mixin(ServerPlayer.class)
public class PlayerSetSpawnMixin {
    @Inject(
            method = "setRespawnPosition(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void kaleidoscopeChineseFood$cancelStrawBedSpawn(ResourceKey<Level> dimension, BlockPos pos, float angle,
                                                             boolean forced, boolean sendMessage, CallbackInfo ci) {
        if (StrawBedTracker.shouldCancelSpawnSet((ServerPlayer) (Object) this)) {
            ci.cancel();
        }
    }
}
