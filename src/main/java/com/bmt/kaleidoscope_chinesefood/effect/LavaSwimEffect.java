package com.bmt.kaleidoscope_chinesefood.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class LavaSwimEffect extends MobEffect {
   public LavaSwimEffect() {
      super(MobEffectCategory.BENEFICIAL, 16737792);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (entity.isInLava()) {
         entity.clearFire();
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return duration % 10 == 0;
   }
}
