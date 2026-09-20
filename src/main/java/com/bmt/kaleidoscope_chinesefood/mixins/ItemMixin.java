package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModPlateRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** 附魔金苹果拼盘物品自带附魔光效。 */
@Mixin(Item.class)
public class ItemMixin {
    private static Item kaleidoscope$enchantedPlateItem;

    @Inject(method = "isFoil", at = @At("RETURN"), cancellable = true)
    private void kaleidoscope$isFoil(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            return;
        }
        if (kaleidoscope$enchantedPlateItem == null) {
            kaleidoscope$enchantedPlateItem = BuiltInRegistries.ITEM.get(ModPlateRegistry.ENCHANTED_GOLDEN_APPLE_PLATTER);
            // 拼盘物品由 cookery 在自身初始化阶段代注册，首次调用时若还没就绪就留待下次
            if (kaleidoscope$enchantedPlateItem == Items.AIR) {
                kaleidoscope$enchantedPlateItem = null;
                return;
            }
        }
        if ((Item) (Object) this == kaleidoscope$enchantedPlateItem) {
            cir.setReturnValue(true);
        }
    }
}
