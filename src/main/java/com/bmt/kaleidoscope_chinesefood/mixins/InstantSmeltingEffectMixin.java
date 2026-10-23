package com.bmt.kaleidoscope_chinesefood.mixins;

import com.github.ysbbbbbb.kaleidoscopecookery.effect.InstantSmeltingEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 对齐官方 1.1.15：禁用 cookery 自带的"即时熔炼掉落"实现，
 * 由本模组 InstantSmeltingDropsMixin 的预算版取而代之。
 */
@Mixin({InstantSmeltingEffect.class})
public abstract class InstantSmeltingEffectMixin {
   @Inject(
      method = {"onGetDrops"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private static void kcf$cancelCookerySmelting(CallbackInfo ci) {
      ci.cancel();
   }
}
