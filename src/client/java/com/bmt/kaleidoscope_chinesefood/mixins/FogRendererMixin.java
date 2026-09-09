package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.LavaFogEnvironment;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric replacement for NeoForge's ViewportEvent.ComputeFogColor / RenderFog:
 * 1.21.11 的雾改为 FogEnvironment 体系，原 FogRenderer.setupColor/setupFog 与
 * RenderSystem.setShaderFog* 均已移除，因此注入岩浆雾环境：
 * 拥有熔岩畅泳效果时把雾染成橙红色并大幅延长可视距离。
 */
@Mixin(LavaFogEnvironment.class)
public abstract class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("TAIL"))
    private void kaleidoscope$onSetupFog(FogData fogData, Camera camera, ClientLevel level, float farPlaneDistance, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (camera.entity() instanceof Player player && player.hasEffect(ModEffects.LAVA_SWIM)) {
            fogData.environmentalStart = 0.1F;
            fogData.environmentalEnd = Math.min(farPlaneDistance * 16.0F, 48.0F);
            fogData.skyEnd = fogData.environmentalEnd;
            fogData.cloudEnd = fogData.environmentalEnd;
        }
    }

    @Inject(method = "getBaseColor", at = @At("HEAD"), cancellable = true)
    private void kaleidoscope$onGetBaseColor(ClientLevel level, Camera camera, int packedLight, float partialTick, CallbackInfoReturnable<Integer> cir) {
        if (camera.entity() instanceof Player player && player.hasEffect(ModEffects.LAVA_SWIM)) {
            // 0.9F, 0.6F, 0.3F 橙红色雾
            cir.setReturnValue(0xFFE6994D);
        }
    }
}
