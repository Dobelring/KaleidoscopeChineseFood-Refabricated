package com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight;

import com.bmt.kaleidoscope_chinesefood.init.ModFoods;
import com.bmt.kaleidoscope_chinesefood.item.BunItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class KTItems {
    public static final String KT_MODID = "kaleidoscope_twilight";
    public static final Identifier FROZEN_BUN_ID = Identifier.fromNamespaceAndPath("kaleidoscope_twilight", "frozen_bun");
    public static BunItem FROZEN_BUN;

    public static void register() {
        ResourceKey<Item> key = ResourceKey.create(BuiltInRegistries.ITEM.key(), FROZEN_BUN_ID);
        FROZEN_BUN = Registry.register(
                BuiltInRegistries.ITEM,
                key,
                new BunItem(new Properties().setId(key).stacksTo(64).food(ModFoods.FROZEN_BUN), "kaleidoscope_twilight")
        );
    }
}
