package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Player.class})
public abstract class PlayerEntityMixin {
    @Inject(
            method = {"isSwimming"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void onIsSwimming(CallbackInfoReturnable<Boolean> cir) {
        Player player = (Player) (Object) this;
        if (player.hasEffect(ModEffects.LAVA_SWIM)) {
            FluidState fluidState = player.level().getFluidState(player.blockPosition());
            if (fluidState.is(FluidTags.LAVA)) {
                boolean isMoving = player.getDeltaMovement().lengthSqr() > 0.001;
                cir.setReturnValue(player.isInWater() && isMoving);
            }
        }
    }
}
