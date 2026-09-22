package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModPlateRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Item.class})
public class ItemMixin {
    private static Item kaleidoscope$enchantedPlateItem;

    public ItemMixin() {
    }

    @Inject(
        method = {"isFoil"},
        at = {@At("RETURN")},
        cancellable = true
    )
    private void kaleidoscope$isFoil(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(Boolean)cir.getReturnValue()) {
            if (kaleidoscope$enchantedPlateItem == null) {
                kaleidoscope$enchantedPlateItem = BuiltInRegistries.ITEM.get(ModPlateRegistry.ENCHANTED_GOLDEN_APPLE_PLATTER);
                if (kaleidoscope$enchantedPlateItem == null) {
                    return;
                }
            }

            if ((Object) this == kaleidoscope$enchantedPlateItem) {
                cir.setReturnValue(true);
            }
        }
    }
}
