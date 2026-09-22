package com.bmt.kaleidoscope_chinesefood.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class LavaSwimEffect extends MobEffect {
    public LavaSwimEffect() {
        super(MobEffectCategory.BENEFICIAL, 16737792);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Forge 原写法：entity.isInFluidType(ForgeMod.LAVA_TYPE.get())
        // Fabric 无 FluidType，改用原版岩浆判定：Entity#isInLava()（等价于包围盒内岩浆流体高度 > 0）
        if (entity.isInLava()) {
            entity.clearFire();
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}
