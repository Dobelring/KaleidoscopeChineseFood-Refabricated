package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 官方 1.1.16 重构岩浆畅游（26.1.2 适配）：26.1 的 travel 不再直接调 isInWater，
 * 改为 travel → shouldTravelInFluid(流体状态) → travelInFluid → isInWater() 分水/岩浆分支。
 * 只改写 {@code travelInFluid} 内部的 isInWater() 调用点（有岩浆畅游效果时把岩浆当水处理，
 * 走 travelInWater 分支获得游泳物理），不再全域谎报 isInWater。
 * shouldTravelInFluid 的判定是 isInWater()||isInLava()，岩浆本身就能进入 travelInFluid，无需改写。
 */
@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
    protected LivingEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
        method = {"travelInFluid"},
        at = {@At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"
        )}
    )
    private boolean kaleidoscope$treatLavaAsWaterInTravel(boolean original) {
        LivingEntity self = (LivingEntity) (Object) this;
        return !original && self.isInLava() && self.hasEffect(ModEffects.LAVA_SWIM) ? true : original;
    }
}
