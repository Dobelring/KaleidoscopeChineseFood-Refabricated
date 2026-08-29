package com.bmt.kaleidoscope_chinesefood;

import com.bmt.kaleidoscope_chinesefood.config.ModConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public class ModBuiltInResourcePacks {
    public static void register() {
        ResourceManagerHelper.registerBuiltinResourcePack(
                KaleidoscopeChineseFood.id("resourcepacks/old_fridge"),
                FabricLoader.getInstance().getModContainer(KaleidoscopeChineseFood.MODID).orElseThrow(),
                Component.translatable("resourcepack.kaleidoscope_chinesefood.old_fridge"),
                ResourcePackActivationType.NORMAL
        );
        if (ModConfig.enableCustomPacks) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    KaleidoscopeChineseFood.id("datapacks/fuzzy_cooking_recipes"),
                    FabricLoader.getInstance().getModContainer(KaleidoscopeChineseFood.MODID).orElseThrow(),
                    Component.translatable("resourcepack.kaleidoscope_chinesefood.fuzzy_cooking_recipes"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
        }
    }
}
