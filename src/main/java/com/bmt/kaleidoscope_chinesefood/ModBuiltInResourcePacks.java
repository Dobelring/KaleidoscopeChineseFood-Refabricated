package com.bmt.kaleidoscope_chinesefood;

import com.bmt.kaleidoscope_chinesefood.config.ModConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public class ModBuiltInResourcePacks {
    public static void register() {
        boolean oldFridge = ResourceManagerHelper.registerBuiltinResourcePack(
                KaleidoscopeChineseFood.id("old_fridge"),
                FabricLoader.getInstance().getModContainer(KaleidoscopeChineseFood.MODID).orElseThrow(),
                Component.translatable("resourcepack.kaleidoscope_chinesefood.old_fridge"),
                ResourcePackActivationType.NORMAL
        );
        if (!oldFridge) {
            KaleidoscopeChineseFood.LOGGER.warn("Built-in resource pack old_fridge failed to register");
        }
        if (ModConfig.enableCustomPacks) {
            // fabric-api 会在 id 之后自动拼 resourcepacks/ 前缀，id 只能填目录名
            boolean fuzzy = ResourceManagerHelper.registerBuiltinResourcePack(
                    KaleidoscopeChineseFood.id("fuzzy_cooking_recipes"),
                    FabricLoader.getInstance().getModContainer(KaleidoscopeChineseFood.MODID).orElseThrow(),
                    Component.translatable("resourcepack.kaleidoscope_chinesefood.fuzzy_cooking_recipes"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
            if (!fuzzy) {
                KaleidoscopeChineseFood.LOGGER.warn("Built-in resource pack fuzzy_cooking_recipes failed to register");
            }
        }
    }
}
