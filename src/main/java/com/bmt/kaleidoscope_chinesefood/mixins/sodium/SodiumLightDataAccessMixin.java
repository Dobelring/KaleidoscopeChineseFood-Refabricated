package com.bmt.kaleidoscope_chinesefood.mixins.sodium;

import com.bmt.kaleidoscope_chinesefood.client.light.DynamicLightManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Sodium 0.5.x（1.20.1）的地形光照不走原版 LevelRenderer.getLightColor，
 * 而是在 LightDataAccess#compute 里直接读光照引擎再打包。
 * 这里在打包结果上叠加动态光（注意 Sodium 字位的方块光在低 4 位，与原版 packed 布局不同）。
 */
@Mixin(
   targets = {"me.jellysquid.mods.sodium.client.model.light.data.LightDataAccess"},
   remap = false
)
public abstract class SodiumLightDataAccessMixin {
   @ModifyReturnValue(
      method = {"compute"},
      at = {@At("RETURN")},
      remap = false
   )
   private int kcf$applyDynamicLight(int original, int x, int y, int z) {
      int dynamicLight = DynamicLightManager.getLightLevel(x, y, z);
      if (dynamicLight <= 0) {
         return original;
      } else {
         int blockLight = original & 15;
         return dynamicLight <= blockLight ? original : original & -16 | Math.min(dynamicLight, 15);
      }
   }
}
