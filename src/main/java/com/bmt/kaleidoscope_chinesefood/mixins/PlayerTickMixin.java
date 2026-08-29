package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.event.FoodEventHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerTickMixin {
    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void kcf$onTick(CallbackInfo ci) {
        FoodEventHandler.onPlayerTick((Player) (Object) this);
    }
}
