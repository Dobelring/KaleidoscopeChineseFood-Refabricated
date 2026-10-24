package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.client.light.DynamicLightManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 26.2+ 的光照采样汇点从 LevelRenderer 挪到了 {@code LightCoordsUtil.getLightCoords}
 * （原版 BlockModelLighter$Cache 与 Sodium 0.9 的 LightDataAccess 都汇入这里），
 * 挂这一个即可同时覆盖原版与 Sodium 的地形管线。
 */
@Mixin(
   value = {LightCoordsUtil.class},
   priority = 900
)
public abstract class LightCoordsUtilLightMixin {
   @ModifyReturnValue(
      method = {"getLightCoords(Lnet/minecraft/util/LightCoordsUtil$BrightnessGetter;Lnet/minecraft/world/level/BlockAndLightGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I"},
      at = {@At("RETURN")}
   )
   private static int kcf$applyDynamicLight(int original, LightCoordsUtil.BrightnessGetter brightnessGetter, BlockAndLightGetter level, BlockState state, BlockPos pos) {
      int dynamicLight = DynamicLightManager.getLightLevel(pos);
      if (dynamicLight <= 0) {
         return original;
      } else {
         int blockLight = original >> 4 & 15;
         return dynamicLight <= blockLight ? original : original & -241 | dynamicLight << 4;
      }
   }
}
