package com.bmt.kaleidoscope_chinesefood.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 官方 1.1.16 新增（changelog 未列）：家具类物品可作熔炉燃料。
 * 原 Forge 1.20.1 用 FurnaceFuelBurnTimeEvent、1.21.1 用 NeoForge data map，
 * Fabric 均无等价物，改由 {@code AbstractFurnaceBlockEntityMixin} 注入
 * {@code isFuel} / {@code getBurnDuration} 时查本表。
 */
public class ModFuels {
    private static final List<String> COOKERY_WOODS = List.of(
        "oak", "spruce", "acacia", "bamboo", "birch", "cherry", "crimson", "dark_oak", "jungle", "mangrove", "warped"
    );
    private static volatile Map<Item, Integer> FUEL_TABLE;

    public ModFuels() {
    }

    /** 命中燃料表返回燃烧刻，否则返回 0。物品注册完成后的首次调用时惰性建表。 */
    public static int burnTimeOf(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        Map<Item, Integer> table = FUEL_TABLE;
        if (table == null) {
            table = buildTable();
            FUEL_TABLE = table;
        }

        return table.getOrDefault(stack.getItem(), 0);
    }

    private static Map<Item, Integer> buildTable() {
        Map<Item, Integer> table = new HashMap<>();
        put(table, "kaleidoscope_chinesefood", "bowl_stack", 300);
        put(table, "kaleidoscope_chinesefood", "mooncake_mold", 200);
        put(table, "kaleidoscope_cookery", "stripped_bamboo_bench", 300);
        put(table, "kaleidoscope_cookery", "stripped_bamboo_eight_immortals_table", 1200);
        put(table, "kaleidoscope_cookery", "long_bench", 300);
        put(table, "kaleidoscope_cookery", "eight_immortals_table", 1200);

        for (String wood : COOKERY_WOODS) {
            put(table, "kaleidoscope_cookery", "cook_stool_" + wood, 300);
            put(table, "kaleidoscope_cookery", "chair_" + wood, 300);
            put(table, "kaleidoscope_cookery", "table_" + wood, 300);
        }

        return table;
    }

    private static void put(Map<Item, Integer> table, String namespace, String path, int burnTime) {
        Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(namespace, path));
        if (item != null && item != Items.AIR) {
            table.put(item, burnTime);
        }
    }
}
