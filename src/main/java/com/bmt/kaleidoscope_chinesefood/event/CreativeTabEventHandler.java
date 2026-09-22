package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * 创造栏内容调整。
 * 原 Forge 的 {@code BuildCreativeModeTabContentsEvent} 改为 Fabric 的
 * {@code ItemGroupEvents.modifyEntriesEvent(ResourceKey<CreativeModeTab>)}
 * （每个标签页一个回调，不再有单一的全局事件对象）。
 */
public class CreativeTabEventHandler {
    // 原 KaleidoscopeChineseFood.fromNamespaceAndPath("kaleidoscope_cookery", "cookery_food")
    private static final ResourceLocation COOKERY_FOOD_TAB_ID = new ResourceLocation("kaleidoscope_cookery", "cookery_food");
    // 原 KaleidoscopeChineseFood.fromNamespaceAndPath("minecraft", "spawn_eggs")
    private static final ResourceLocation SPAWN_EGGS_TAB_ID = new ResourceLocation("minecraft", "spawn_eggs");
    private static final ResourceKey<CreativeModeTab> COOKERY_FOOD_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, COOKERY_FOOD_TAB_ID);
    // 等价于原版 CreativeModeTabs.SPAWN_EGGS（ResourceKey 按 registry + location 判等）
    private static final ResourceKey<CreativeModeTab> SPAWN_EGGS_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, SPAWN_EGGS_TAB_ID);

    public CreativeTabEventHandler() {
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(COOKERY_FOOD_TAB)
            .register(entries -> addItemsToTabs(COOKERY_FOOD_TAB, entries));
        ItemGroupEvents.modifyEntriesEvent(SPAWN_EGGS_TAB)
            .register(entries -> addItemsToTabs(SPAWN_EGGS_TAB, entries));
    }

    /** 原 addItemsToTabs(BuildCreativeModeTabContentsEvent event)。 */
    public static void addItemsToTabs(ResourceKey<CreativeModeTab> tabKey, FabricItemGroupEntries entries) {
        if (tabKey.location().equals(COOKERY_FOOD_TAB_ID)) {
            // 原逻辑：遍历 event.getEntries() 用 iterator.remove() 去掉厨艺食物标签页里重复出现的国味物品
            // （厨艺的 cookery_food 标签页会无命名空间过滤地遍历 FoodBiteRegistry / PlateRegistry，
            //  因此国味的碗菜与盘装食物会被自动塞进去）。
            // Fabric 的 item group 事件没有「删除条目」API，但 FabricItemGroupEntries#getDisplayStacks /
            // getSearchTabStacks 返回的是会被写回标签页的实时列表（Fabric 的 ItemGroupMixin 在事件回调后
            // 会把这两个列表的内容写回标签页），所以直接 removeIf 与原效果等价。
            entries.getDisplayStacks().removeIf(CreativeTabEventHandler::isChineseFoodItem);
            entries.getSearchTabStacks().removeIf(CreativeTabEventHandler::isChineseFoodItem);
        }

        if (tabKey.location().equals(SPAWN_EGGS_TAB_ID)) {
            entries.accept(ModItems.YELLOW_CROAKER_SPAWN_EGG);
        }
    }

    private static boolean isChineseFoodItem(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && id.getNamespace().equals("kaleidoscope_chinesefood");
    }
}
