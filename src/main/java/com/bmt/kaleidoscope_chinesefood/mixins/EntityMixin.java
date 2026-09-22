package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
    public EntityMixin() {
    }

    @Inject(
        method = {"isInWater"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void onIsInWater(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity living && living.hasEffect(ModEffects.LAVA_SWIM)) {
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
        if (entity instanceof LivingEntity living && living.hasEffect(ModEffects.LAVA_SWIM)) {
            cir.setReturnValue(false);
        }
    }

    // Fabric 无流体类型（FluidType）概念：原「是否身处岩浆流体类型」的判定改为直接判定所在流体状态
    @WrapOperation(
        method = {"updateSwimming"},
        at = {@At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;setSwimming(Z)V"
        )}
    )
    private void wrapSetSwimming(Entity instance, boolean original, Operation<Void> op) {
        if (instance instanceof LivingEntity living && living.hasEffect(ModEffects.LAVA_SWIM)) {
            FluidState fluidState = instance.level().getFluidState(instance.blockPosition());
            if (fluidState.is(FluidTags.LAVA)) {
                op.call(new Object[]{instance, true});
                return;
            }
        }

        op.call(new Object[]{instance, original});
    }

    @Inject(
        method = {"tick"},
        at = {@At("TAIL")}
    )
    private void onTick(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof LivingEntity living
            && living.hasEffect(ModEffects.LAVA_SWIM)
            && entity.level().getFluidState(entity.blockPosition()).is(FluidTags.LAVA)) {
            entity.fallDistance = 0.0F;
        }
    }
}
