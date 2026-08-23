package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric replacement for NeoForge's ViewportEvent.ComputeFogColor / RenderFog:
 * tints the fog and extends visibility while swimming in lava with the LavaSwim effect.
 * 26.x: fog color and distances both live on the returned {@link FogData}.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "setupFog", at = @At("RETURN"))
    private static void kaleidoscope$onSetupFog(
        Camera camera,
        int renderDistanceChunks,
        DeltaTracker deltaTracker,
        float partialTick,
        ClientLevel level,
        CallbackInfoReturnable<FogData> cir
    ) {
        if (camera.entity() instanceof Player player
                && player.hasEffect(ModEffects.LAVA_SWIM)
                && camera.getFluidInCamera() == FogType.LAVA) {
            FogData data = cir.getReturnValue();
            data.color.set(0.9F, 0.6F, 0.3F, data.color.w);
            float fogEnd = Math.min((float)renderDistanceChunks * 16.0F, 48.0F);
            data.renderDistanceStart = 0.1F;
            data.renderDistanceEnd = fogEnd;
            data.environmentalStart = 0.1F;
            data.environmentalEnd = fogEnd;
        }
    }
}
