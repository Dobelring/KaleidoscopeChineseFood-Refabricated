package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.inventory.FreezerMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenuTypes {
    public static MenuType<FreezerMenu> FREEZER_TOP_MENU;
    public static MenuType<FreezerMenu> FREEZER_BOTTOM_MENU;

    public static void register() {
        // Two menu types so the client receives the correct compartment size (36 vs 54)
        // without needing a custom network sync payload.
        FREEZER_TOP_MENU = Registry.register(
                BuiltInRegistries.MENU,
                KaleidoscopeChineseFood.id("freezer_top_menu"),
                new MenuType<>((id, inv) -> new FreezerMenu(id, inv, new SimpleContainer(36)), FeatureFlags.VANILLA_SET)
        );
        FREEZER_BOTTOM_MENU = Registry.register(
                BuiltInRegistries.MENU,
                KaleidoscopeChineseFood.id("freezer_bottom_menu"),
                new MenuType<>((id, inv) -> new FreezerMenu(id, inv, new SimpleContainer(54)), FeatureFlags.VANILLA_SET)
        );
    }
}
