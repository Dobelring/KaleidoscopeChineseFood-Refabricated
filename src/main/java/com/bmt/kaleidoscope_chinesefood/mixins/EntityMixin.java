package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public abstract class EntityMixin {
    @Inject(
            method = {"isInWater"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void onIsInWater(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.LAVA_SWIM)) {
            FluidState fluidState = entity.level().getFluidState(entity.blockPosition());
            if (fluidState.is(FluidTags.LAVA)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = {"isInLava"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void onIsInLava(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.LAVA_SWIM)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = {"getFluidHeight"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void onGetFluidHeight(CallbackInfoReturnable<Double> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.LAVA_SWIM)) {
            FluidState fluidState = entity.level().getFluidState(entity.blockPosition());
            if (fluidState.is(FluidTags.LAVA)) {
                cir.setReturnValue((double) fluidState.getHeight(entity.level(), entity.blockPosition()));
            }
        }
    }

    @Inject(
            method = {"tick"},
            at = {@At("TAIL")}
    )
    private void onTick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(ModEffects.LAVA_SWIM)) {
            FluidState fluidState = entity.level().getFluidState(entity.blockPosition());
            if (fluidState.is(FluidTags.LAVA)) {
                entity.fallDistance = 0.0F;
            }
        }
    }
}
