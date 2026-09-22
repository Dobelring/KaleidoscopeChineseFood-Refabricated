package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry.PlateData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class ModPlateRegistry {
    public static final ResourceLocation GOLDEN_APPLE_PLATTER = id("golden_apple_platter");
    public static final ResourceLocation ENCHANTED_GOLDEN_APPLE_PLATTER = id("enchanted_golden_apple_platter");

    public ModPlateRegistry() {
    }

    public static void init() {
        registerPlateData(GOLDEN_APPLE_PLATTER, PlateData.create(4).setServingItems(() -> Items.GOLDEN_APPLE).setLootItem(Items.BOWL).platterAABB());
        registerPlateData(ENCHANTED_GOLDEN_APPLE_PLATTER, PlateData.create(4).setServingItems(() -> Items.ENCHANTED_GOLDEN_APPLE).setLootItem(Items.BOWL).platterAABB());
    }

    private static void registerPlateData(ResourceLocation id, PlateData data) {
        PlateRegistry.PLATE_DATA_MAP.put(id, data);
    }

    private static ResourceLocation id(String name) {
        return KaleidoscopeChineseFood.id(name);
    }
}
