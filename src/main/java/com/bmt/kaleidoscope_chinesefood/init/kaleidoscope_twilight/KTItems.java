package com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight;

import com.bmt.kaleidoscope_chinesefood.init.ModFoods;
import com.bmt.kaleidoscope_chinesefood.item.BunItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item.Properties;

public class KTItems {
    public static final String KT_MODID = "kaleidoscope_twilight";
    public static final Identifier FROZEN_BUN_ID = Identifier.fromNamespaceAndPath("kaleidoscope_twilight", "frozen_bun");
    public static BunItem FROZEN_BUN;

    public static void register() {
        // 1.21.11 起 Item.Properties 必须显式 setId，且需与注册 id 一致
        FROZEN_BUN = Registry.register(
                BuiltInRegistries.ITEM,
                FROZEN_BUN_ID,
                new BunItem(new Properties()
                        .setId(ResourceKey.create(Registries.ITEM, FROZEN_BUN_ID))
                        .stacksTo(64)
                        .food(ModFoods.FROZEN_BUN), KT_MODID)
        );
    }
}
