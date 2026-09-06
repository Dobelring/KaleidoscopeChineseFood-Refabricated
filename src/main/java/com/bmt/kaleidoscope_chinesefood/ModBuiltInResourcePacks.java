package com.bmt.kaleidoscope_chinesefood;

import com.bmt.kaleidoscope_chinesefood.config.ModConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModBuiltInResourcePacks {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModBuiltInResourcePacks.class);

    public static void register() {
        // 注意：4 参重载内部会自动拼接 "resourcepacks/" + id.getPath()，
        // id 只能填目录名（old_fridge / fuzzy_cooking_recipes），否则路径重复导致注册静默失败
        boolean fridgePack = ResourceManagerHelper.registerBuiltinResourcePack(
                KaleidoscopeChineseFood.id("old_fridge"),
                FabricLoader.getInstance().getModContainer(KaleidoscopeChineseFood.MODID).orElseThrow(),
                Component.translatable("resourcepack.kaleidoscope_chinesefood.old_fridge"),
                ResourcePackActivationType.NORMAL
        );
        if (!fridgePack) {
            LOGGER.warn("[KCF] Failed to register builtin resource pack old_fridge (folder resourcepacks/old_fridge missing)");
        }
        if (ModConfig.enableCustomPacks) {
            boolean fuzzyPack = ResourceManagerHelper.registerBuiltinResourcePack(
                    KaleidoscopeChineseFood.id("fuzzy_cooking_recipes"),
                    FabricLoader.getInstance().getModContainer(KaleidoscopeChineseFood.MODID).orElseThrow(),
                    Component.translatable("resourcepack.kaleidoscope_chinesefood.fuzzy_cooking_recipes"),
                    ResourcePackActivationType.ALWAYS_ENABLED
            );
            if (!fuzzyPack) {
                LOGGER.warn("[KCF] Failed to register builtin datapack fuzzy_cooking_recipes (folder resourcepacks/fuzzy_cooking_recipes missing)");
            }
        }
    }
}
