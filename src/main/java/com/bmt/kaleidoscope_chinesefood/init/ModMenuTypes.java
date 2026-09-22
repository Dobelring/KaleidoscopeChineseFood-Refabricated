package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.menu.FreezerMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModMenuTypes {
    // Fabric：ExtendedScreenHandlerType 的工厂签名就是 (int, Inventory, FriendlyByteBuf)，
    // 与 FreezerMenu 保留的构造器一致，并且它禁用了原版 (int, Inventory) 的打开路径
    public static final ExtendedScreenHandlerType<FreezerMenu> FREEZER_MENU = new ExtendedScreenHandlerType<>(FreezerMenu::new);

    public ModMenuTypes() {
    }

    public static void registerMenuTypes() {
        Registry.register(BuiltInRegistries.MENU, new ResourceLocation("kaleidoscope_chinesefood", "freezer_menu"), FREEZER_MENU);
    }
}
