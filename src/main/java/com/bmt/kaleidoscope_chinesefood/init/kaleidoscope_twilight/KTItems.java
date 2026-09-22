package com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight;

import com.bmt.kaleidoscope_chinesefood.init.ModFoods;
import com.bmt.kaleidoscope_chinesefood.item.BunItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class KTItems {
    public static final String KT_MODID = "kaleidoscope_twilight";
    public static final ResourceLocation FROZEN_BUN_ID = new ResourceLocation("kaleidoscope_twilight", "frozen_bun");
    public static final Item FROZEN_BUN = new BunItem(new Properties().stacksTo(64).food(ModFoods.FROZEN_BUN), "kaleidoscope_twilight");

    public KTItems() {
    }

    public static void registerKTItems() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_twilight", "frozen_bun"), FROZEN_BUN);
    }
}
