package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModFuels;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 官方 1.1.16 家具燃料：Forge 用 FurnaceFuelBurnTimeEvent / NeoForge 用 data map，
 * Fabric 都没有，改注入原版熔炉的 {@code isFuel}（静态目标 → 处理器必须 static）
 * 与 {@code getBurnDuration}。
 */
@Mixin({AbstractFurnaceBlockEntity.class})
public abstract class AbstractFurnaceBlockEntityMixin {
    public AbstractFurnaceBlockEntityMixin() {
    }

    @Inject(
        method = {"isFuel"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private static void kaleidoscope$isCustomFuel(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ModFuels.burnTimeOf(stack) > 0) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
        method = {"getBurnDuration"},
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
