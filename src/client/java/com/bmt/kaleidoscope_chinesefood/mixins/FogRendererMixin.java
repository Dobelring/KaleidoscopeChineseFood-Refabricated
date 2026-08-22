package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric replacement for NeoForge's ViewportEvent.ComputeFogColor / RenderFog:
 * tints the fog and extends visibility while swimming in lava with the LavaSwim effect.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void kaleidoscope$onSetupColor(Camera camera, float partialTick, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        if (camera.getEntity() instanceof Player player
                && player.hasEffect(ModEffects.LAVA_SWIM)
                && camera.getFluidInCamera() == FogType.LAVA) {
            RenderSystem.setShaderFogColor(0.9F, 0.6F, 0.3F);
        }
    }

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void kaleidoscope$onSetupFog(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean thickFog, float partialTick, CallbackInfo ci) {
        if (camera.getEntity() instanceof Player player
                && player.hasEffect(ModEffects.LAVA_SWIM)
                && camera.getFluidInCamera() == FogType.LAVA) {
            float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
            RenderSystem.setShaderFogStart(0.1F);
            RenderSystem.setShaderFogEnd(Math.min(renderDistance * 16.0F, 48.0F));
        }
    }
}
