package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

/**
 * 岩浆游泳效果免伤（官方 1.1.16 语义）：
 * <ul>
 * <li>岩浆伤害始终免疫；</li>
 * <li>「着火」伤害仅在身处岩浆时免疫（上岸后身上的火会照常烧）；</li>
 * <li>不再免疫「在火中」伤害（旧版连站在火上也不烧，官方 1.1.16 收紧了）。</li>
 * </ul>
 */
public class LavaSwimDamageEvents {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(LavaSwimDamageEvents::onLivingHurt);
    }

    private static boolean onLivingHurt(LivingEntity entity, DamageSource source, float amount) {
        if (entity.hasEffect(ModEffects.LAVA_SWIM)) {
            if (source.is(DamageTypes.LAVA)) {
                return false;
            }

            if (source.is(DamageTypes.ON_FIRE) && entity.isInLava()) {
                return false;
            }
        }

        return true;
    }
}
