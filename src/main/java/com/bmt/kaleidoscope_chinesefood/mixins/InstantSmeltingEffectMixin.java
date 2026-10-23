package com.bmt.kaleidoscope_chinesefood.mixins;

import com.github.ysbbbbbb.kaleidoscopecookery.effect.InstantSmeltingEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 对齐官方 1.1.15：禁用 cookery 自带的"即时熔炼掉落"实现。
 * 官方在 NeoForge 1.21.1 上也只禁不补，移植版保持同款行为。
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
