package com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight;

import com.bmt.kaleidoscope_chinesefood.init.ModFoods;
import com.bmt.kaleidoscope_chinesefood.item.BunItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item.Properties;

public class KTItems {
    public static final String KT_MODID = "kaleidoscope_twilight";
    public static final Identifier FROZEN_BUN_ID = Identifier.fromNamespaceAndPath("kaleidoscope_twilight", "frozen_bun");
    public static BunItem FROZEN_BUN;

    public static void register() {
        FROZEN_BUN = Registry.register(
                BuiltInRegistries.ITEM,
                FROZEN_BUN_ID,
                new BunItem(new Properties().stacksTo(64).food(ModFoods.FROZEN_BUN), "kaleidoscope_twilight")
        );
    }
}
