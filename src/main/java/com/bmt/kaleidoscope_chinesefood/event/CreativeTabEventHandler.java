package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import java.util.List;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

/** 对应官方的 CreativeTabEventHandler。 */
public class CreativeTabEventHandler {
    private static final ResourceKey<CreativeModeTab> COOKERY_FOOD_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "cookery_food"));

    public static void register() {
        // 厨房的「食物」页会无命名空间过滤地遍历 cookery 的食品/拼盘/茶杯数据表，
        // 国味的条目会被顺带收录，这里按官方做法摘掉（父页 + 搜索页都摘）
        ItemGroupEvents.modifyEntriesEvent(COOKERY_FOOD_TAB).register(CreativeTabEventHandler::removeOurEntries);

        // 黄花鱼刷怪蛋放进原版刷怪蛋页
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> entries.accept(ModItems.YELLOW_CROAKER_SPAWN_EGG));
    }

    private static void removeOurEntries(FabricItemGroupEntries entries) {
        removeOur(entries.getDisplayStacks());
        removeOur(entries.getSearchTabStacks());
    }

    private static void removeOur(List<ItemStack> stacks) {
        stacks.removeIf(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals(KaleidoscopeChineseFood.MODID));
    }
}
