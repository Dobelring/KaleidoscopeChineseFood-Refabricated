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
 * 官方 1.1.16 重构岩浆畅游（实体侧）：不再全域谎报 isInWater/isInLava，只做三件事——
 * <ul>
 * <li>{@code lavaHurt} 直接取消（免岩浆伤害，同时 1.x 的火焰也在 lavaHurt 里设置，一并根治）；</li>
 * <li>客户端 {@code isOnFire} 返 false（岩浆里不渲染火焰覆盖层）；</li>
 * <li>{@code updateSwimming} 的 isInWater 采样点改写（岩浆畅游下疾跑可触发游泳旗标——
 * 双击 W 游泳、游泳动画与岩浆畅游进度都依赖它。官方新版只改了 travel，漏了这处，实测岩浆里无法疾跑游泳）。</li>
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
        if (entity.level().isClientSide && entity.isInLava() && entity instanceof LivingEntity living && living.hasEffect(ModEffects.LAVA_SWIM)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
        method = {"updateSwimming"},
        at = {@At("TAIL")}
    )
    private void kaleidoscope$forceSwimInLava(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!self.isPassenger() && self.isInLava() && self instanceof LivingEntity living && living.hasEffect(ModEffects.LAVA_SWIM) && living.isSprinting()) {
            living.setSwimming(true);
        }
    }
}
