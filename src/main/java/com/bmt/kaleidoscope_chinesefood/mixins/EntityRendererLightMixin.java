package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.client.light.DynamicLightManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({EntityRenderer.class})
public abstract class EntityRendererLightMixin<T extends Entity> {
   @ModifyReturnValue(
      method = {"getBlockLightLevel"},
      at = {@At("RETURN")}
   )
   private int kcf$addDynamicLight(int original, T entity, BlockPos pos) {
      return original >= 15 ? original : Math.max(original, DynamicLightManager.getLightLevel(pos));
   }
}
