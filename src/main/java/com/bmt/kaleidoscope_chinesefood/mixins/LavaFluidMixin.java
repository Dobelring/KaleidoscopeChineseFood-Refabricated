package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.x 上岸着火的真正根因：26.1 起岩浆点火走 InsideBlockEffect 延迟管线——
 * {@code LavaFluid.entityInside} 只"排队" LAVA_IGNITE（{@code applier.apply}），
 * 真正执行在实体侧 flush 时，此时实体可能已离开岩浆（isInLava()==false），
 * 所以在 {@code setRemainingFireTicks} 处带 isInLava 守卫拦不住（实测上岸仍着火）。
 * 改在排队的源头 {@code entityInside} 整体取消：岩浆畅游下跳过 LAVA_IGNITE 的排队，
 * 原 CLEAR_FREEZE 效果（解雪冻）在取消前手动保留。setRemainingFireTicks 拦截仍保留作兜底。
 */
@Mixin({LavaFluid.class})
public abstract class LavaFluidMixin {
    public LavaFluidMixin() {
    }

    @Inject(
        method = {"entityInside"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void kaleidoscope$noLavaIgnite(Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, CallbackInfo ci) {
        if (entity instanceof LivingEntity living && living.hasEffect(ModEffects.LAVA_SWIM)) {
            entity.setTicksFrozen(0);
            ci.cancel();
        }
    }
}
