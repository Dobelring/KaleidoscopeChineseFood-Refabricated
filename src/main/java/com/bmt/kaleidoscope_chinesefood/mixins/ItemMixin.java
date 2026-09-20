package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModPlateRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让附魔金苹果拼盘的物品在物品栏/手中带附魔光效。
 * <p>
 * 官方在物品上覆写 {@code isFoil}，但拼盘物品由 cookery 的 {@code WithTooltipsBlockItem} 承载，
 * 所以这里统一在 {@code Item#isFoil} 的返回值上补一刀。
 * 拼盘方块注册在本模组 food phase，故物品引用延迟解析。
 */
@Mixin(Item.class)
public abstract class ItemMixin {
    @Unique
    private static Item kaleidoscope_chinesefood$enchantedPlateItem;
    @Unique
    private static boolean kaleidoscope_chinesefood$enchantedPlateResolved;

    @Inject(method = "isFoil", at = @At("RETURN"), cancellable = true)
    private void kaleidoscope_chinesefood$isFoil(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue())) {
            return;
        }
        if (!kaleidoscope_chinesefood$enchantedPlateResolved) {
            kaleidoscope_chinesefood$enchantedPlateItem = BuiltInRegistries.ITEM.getValue(ModPlateRegistry.ENCHANTED_GOLDEN_APPLE_PLATTER);
            kaleidoscope_chinesefood$enchantedPlateResolved = kaleidoscope_chinesefood$enchantedPlateItem != null;
            if (!kaleidoscope_chinesefood$enchantedPlateResolved) {
                return;
            }
        }
        if ((Object) this == kaleidoscope_chinesefood$enchantedPlateItem) {
            cir.setReturnValue(true);
        }
    }
}
