package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.client.light.DynamicLightManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
   value = {LevelRenderer.class},
   priority = 900
)
public abstract class LevelRendererLightMixin {
   @ModifyReturnValue(
      method = {"getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I"},
      at = {@At("RETURN")}
   )
   private static int kcf$applyDynamicLight(int original, BlockAndTintGetter level, BlockState state, BlockPos pos) {
      int dynamicLight = DynamicLightManager.getLightLevel(pos);
      if (dynamicLight <= 0) {
         return original;
      } else {
         int blockLight = original >> 4 & 15;
         return dynamicLight <= blockLight ? original : original & -241 | dynamicLight << 4;
      }
   }
}
