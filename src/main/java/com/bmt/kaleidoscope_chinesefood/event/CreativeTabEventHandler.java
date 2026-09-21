package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import java.util.List;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * 厨房的「食物」页会无命名空间过滤地遍历 cookery 的食品 / 拼盘 / 茶杯数据表
 * （{@code FoodBiteRegistry.FOOD_DATA_MAP}、{@code TeacupRegistry.TEACUP_DATA_MAP}、
 * {@code PlateRegistry.PLATE_DATA_MAP}），国味注册进去的条目会被顺带收录，
 * 于是同一件物品既在国味页又在厨房食物页出现。这里把它们摘掉。
 */
public class CreativeTabEventHandler {
    private static final ResourceKey<CreativeModeTab> COOKERY_FOOD_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "cookery_food"));

    public static void register() {
        // 展示列表与搜索列表是两份独立列表，都要摘，否则搜索时仍能搜到重复项
        ItemGroupEvents.modifyEntriesEvent(COOKERY_FOOD_TAB).register(CreativeTabEventHandler::removeOurEntries);
    }

    private static void removeOurEntries(FabricItemGroupEntries entries) {
        removeOur(entries.getDisplayStacks());
        removeOur(entries.getSearchTabStacks());
    }

    private static void removeOur(List<ItemStack> stacks) {
        stacks.removeIf(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem())
                .getNamespace().equals(KaleidoscopeChineseFood.MODID));
    }
}
