package com.bmt.kaleidoscope_chinesefood.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class SaturationShieldEffect extends MobEffect {
   public static final float BASE_CONVERSION_RATIO = 0.5F;
   public static final float RATIO_PER_LEVEL = 1.0F;

   public SaturationShieldEffect() {
      super(MobEffectCategory.BENEFICIAL, 16766720);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return false;
   }
}
