package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModFuels;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.FuelValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 官方 1.1.16 家具燃料（26.1.2 适配）：1.20/1.21.1 的 AbstractFurnaceBlockEntity
 * isFuel/getBurnDuration 在 26.1 收敛到 {@link FuelValues}（数据驱动燃料表）——
 * 注入它的 isFuel / burnDuration 即覆盖熔炉的燃料判定与燃烧时长。
 */
@Mixin({FuelValues.class})
public abstract class FuelValuesMixin {
    public FuelValuesMixin() {
    }

    @Inject(
        method = {"isFuel"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void kaleidoscope$isCustomFuel(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ModFuels.burnTimeOf(stack) > 0) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
        method = {"burnDuration"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void kaleidoscope$customBurnTime(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        int burnTime = ModFuels.burnTimeOf(stack);
        if (burnTime > 0) {
            cir.setReturnValue(burnTime);
        }
    }
}
