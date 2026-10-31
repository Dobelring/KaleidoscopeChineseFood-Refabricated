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
 * 官方 1.1.16 重构岩浆畅游：只在 {@code LivingEntity#travel} 的游泳物理采样点
 * 把「在水中」改写为真（岩浆畅游效果下岩浆=水），替代旧版全域谎报
 * {@code Entity#isInWater} 的方案——不再影响溺水、绊线、其他模组判定等采样点。
 */
@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
    protected LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
        method = {"travel"},
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
