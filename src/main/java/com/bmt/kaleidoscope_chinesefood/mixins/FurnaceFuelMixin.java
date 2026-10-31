package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModFuels;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 官方 1.1.16 家具燃料（26.3 适配）：燃料判定收敛到物品的 COOKING_FUEL 数据组件——
 * 槽位放置走 {@code AbstractFurnaceMenu.isFuel}（组件存在性）、
 * 燃烧时长走 {@code AbstractFurnaceBlockEntity.getBurnDuration}。两者都注入查本模组燃料表。
 */
public class FurnaceFuelMixin {
    public FurnaceFuelMixin() {
    }

    @Mixin({AbstractFurnaceMenu.class})
    public abstract static class MenuFuelMixin {
        @Inject(
            method = {"isFuel"},
            at = {@At("HEAD")},
            cancellable = true
        )
        protected void kaleidoscope$isCustomFuel(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
            if (ModFuels.burnTimeOf(stack) > 0) {
                cir.setReturnValue(true);
            }
        }
    }

    @Mixin({AbstractFurnaceBlockEntity.class})
    public abstract static class BurnDurationMixin {
        @Inject(
            method = {"getBurnDuration"},
            at = {@At("HEAD")},
            cancellable = true
        )
        protected void kaleidoscope$customBurnTime(ServerLevel level, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
            int burnTime = ModFuels.burnTimeOf(stack);
            if (burnTime > 0) {
                cir.setReturnValue(burnTime);
            }
        }
    }
}
