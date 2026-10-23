package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.client.light.DynamicLightManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelLighter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 26.2+ 的光照采样不再有 {@code LevelRenderer.getLightColor} 静态汇点，
 * 方块/地形路径收敛到 {@code BlockModelLighter.getLightCoords}（内部再走 $Cache 记忆化，
 * 且 Cache 仅被外层调用，挂外层即可全覆盖）。
 */
@Mixin(
   value = {BlockModelLighter.class},
   priority = 900
)
public abstract class BlockModelLighterLightMixin {
   @ModifyReturnValue(
      method = {"getLightCoords(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;)I"},
      at = {@At("RETURN")}
   )
   private int kcf$applyDynamicLight(int original, BlockState state, BlockAndTintGetter level, BlockPos pos) {
      int dynamicLight = DynamicLightManager.getLightLevel(pos);
      if (dynamicLight <= 0) {
         return original;
      } else {
         int blockLight = original >> 4 & 15;
         return dynamicLight <= blockLight ? original : original & -241 | dynamicLight << 4;
      }
   }
}
