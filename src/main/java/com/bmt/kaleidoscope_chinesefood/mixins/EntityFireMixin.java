package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.x 适配：26.1 起岩浆不再经 lavaHurt 设火，而是走 InsideBlockEffect 管线
 * （LavaFluid.entityInside → InsideBlockEffectType.LAVA_IGNITE，延迟到实体侧执行），
 * 只取消 lavaHurt 拦不住点火——实测带岩浆畅游效果上岸会带着火。
 * 在 setRemainingFireTicks 漏斗处拦截：有岩浆畅游效果且身处岩浆时不接受正值点燃
 * （灭火路径写 0/负值不受影响，岸上站火照常烧）。
 */
@Mixin({Entity.class})
public abstract class EntityFireMixin {
    public EntityFireMixin() {
    }

    @Inject(
        method = {"setRemainingFireTicks"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void kaleidoscope$noLavaFire(int ticks, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (ticks > 0 && self.isInLava() && self instanceof LivingEntity living && living.hasEffect(ModEffects.LAVA_SWIM)) {
            ci.cancel();
        }
    }
}
