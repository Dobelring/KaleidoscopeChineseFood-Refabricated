package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

/**
 * 岩浆游泳效果免伤。
 * 原 Forge 的 {@code LivingHurtEvent}（FORGE 总线）在 Fabric 上无对应事件，
 * 改用 {@code ServerLivingEntityEvents.ALLOW_DAMAGE}：返回 false 等价于原
 * {@code event.setCanceled(true)}（都在伤害生效前拦截，且都只在服务端触发）。
 */
public class LavaSwimDamageEvents {
    public LavaSwimDamageEvents() {
    }

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(LavaSwimDamageEvents::onLivingHurt);
    }

    /** 原 onLivingHurt(LivingHurtEvent)。 */
    public static boolean onLivingHurt(LivingEntity entity, DamageSource source, float amount) {
        if (entity.hasEffect(ModEffects.LAVA_SWIM)
            && (
                source.is(DamageTypes.LAVA)
                    || source.is(DamageTypes.IN_FIRE)
                    || source.is(DamageTypes.ON_FIRE)
            )) {
            return false;
        }

        return true;
    }
}
