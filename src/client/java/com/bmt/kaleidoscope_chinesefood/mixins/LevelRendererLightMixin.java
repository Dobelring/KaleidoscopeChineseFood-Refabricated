package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.client.light.DynamicLightManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 26.x 的光照采样从 {@code getLightColor(BlockAndTintGetter, BlockState, BlockPos)} 改成了
 * {@code getLightCoords(BrightnessGetter, BlockAndLightGetter, BlockState, BlockPos)}，
 * 且 2 参重载内部也汇入 4 参实现，只需挂 4 参这一个汇点。
 */
@Mixin(
   value = {LevelRenderer.class},
   priority = 900
)
public abstract class LevelRendererLightMixin {
   @ModifyReturnValue(
      method = {"getLightCoords(Lnet/minecraft/client/renderer/LevelRenderer$BrightnessGetter;Lnet/minecraft/world/level/BlockAndLightGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I"},
      at = {@At("RETURN")}
   )
   private static int kcf$applyDynamicLight(int original, LevelRenderer.BrightnessGetter brightnessGetter, BlockAndLightGetter level, BlockState state, BlockPos pos) {
      int dynamicLight = DynamicLightManager.getLightLevel(pos);
      if (dynamicLight <= 0) {
         return original;
      } else {
         int blockLight = original >> 4 & 15;
         return dynamicLight <= blockLight ? original : original & -241 | dynamicLight << 4;
      }
   }
}
