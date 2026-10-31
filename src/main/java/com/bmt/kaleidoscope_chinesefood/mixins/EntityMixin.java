package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 官方 1.1.16 重构岩浆畅游：不再全域谎报 isInWater/isInLava（旧方案会把溺水、
 * 脚下方块判定等其他 isInWater 采样点全部带歪），只做两件事——
 * <ul>
 * <li>{@code lavaHurt} 直接取消（免岩浆伤害，替代旧 isInLava 谎报 + 事件免伤组合）；</li>
 * <li>客户端 {@code isOnFire} 返 false（岩浆里不渲染火焰覆盖层）。</li>
 * </ul>
 * 游泳物理由 {@link LivingEntityMixin}（travel 内 isInWater 调用点改写）负责，
 * 岩浆雾由 {@link CameraMixin}（getFluidInCamera → NONE）根治，同时天然兼容 shader 模组。
 */
@Mixin({Entity.class})
public abstract class EntityMixin {
    public EntityMixin() {
    }

    @Inject(
        method = {"lavaHurt"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void kaleidoscope$cancelLavaHurt(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity.isInLava() && entity instanceof LivingEntity living && living.hasEffect(ModEffects.LAVA_SWIM)) {
            ci.cancel();
        }
    }

    @Inject(
        method = {"isOnFire"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void kaleidoscope$hideFireOverlayInLava(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity.level().isClientSide() && entity.isInLava() && entity instanceof LivingEntity living && living.hasEffect(ModEffects.LAVA_SWIM)) {
            cir.setReturnValue(false);
        }
    }
}
