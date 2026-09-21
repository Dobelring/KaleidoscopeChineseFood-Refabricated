package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * 创造栏调整（对应官方的 {@code CreativeTabEventHandler}）：
 * <ul>
 *   <li>黄鱼刷怪蛋放进原版 {@code minecraft:spawn_eggs} 页；</li>
 *   <li>厨房 {@code cookery_food} 页会把本模组的菜品/拼盘/茶杯重复收录一遍
 *       （cookery 的该页裸遍历 {@code FOOD_DATA_MAP}/{@code PLATE_DATA_MAP}/{@code TEACUP_DATA_MAP}，
 *       不按命名空间过滤），这里按命名空间摘掉。</li>
 * </ul>
 */
public final class CreativeTabEvents {
    private static final ResourceKey<CreativeModeTab> SPAWN_EGGS =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.withDefaultNamespace("spawn_eggs"));
    private static final ResourceKey<CreativeModeTab> COOKERY_FOOD =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "cookery_food"));

    private CreativeTabEvents() {
    }

    public static void register() {
        CreativeModeTabEvents.modifyOutputEvent(SPAWN_EGGS)
                .register(output -> output.accept(new ItemStack(ModItems.YELLOW_CROAKER_SPAWN_EGG)));

        CreativeModeTabEvents.modifyOutputEvent(COOKERY_FOOD)
                .register(CreativeTabEvents::removeOurEntries);
    }

    private static void removeOurEntries(FabricCreativeModeTabOutput output) {
        output.getDisplayStacks().removeIf(CreativeTabEvents::isOurs);
        output.getSearchTabStacks().removeIf(CreativeTabEvents::isOurs);
    }

    private static boolean isOurs(ItemStack stack) {
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && id.getNamespace().equals(KaleidoscopeChineseFood.MODID);
    }
}
