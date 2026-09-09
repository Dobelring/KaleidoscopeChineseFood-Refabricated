package com.bmt.kaleidoscope_chinesefood;

import com.bmt.kaleidoscope_chinesefood.config.ModConfig;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

/**
 * 26.1 fabric-resource-loader-v1 新 API：ResourceLoader.registerBuiltinPack 对同一 id
 * 自动注册 CLIENT_RESOURCES 与 SERVER_DATA 两个包；id 只填目录名。
 * pack.mcmeta 的 min_format/max_format 需与 26.2 的资源包(88)/数据包(107.1)版本匹配。
 */
public class ModBuiltInResourcePacks {
    public static void register() {
        boolean oldFridge = ResourceLoader.registerBuiltinPack(
                KaleidoscopeChineseFood.id("old_fridge"),
                FabricLoader.getInstance().getModContainer(KaleidoscopeChineseFood.MODID).orElseThrow(),
                Component.translatable("resourcepack.kaleidoscope_chinesefood.old_fridge"),
                PackActivationType.NORMAL
        );
        if (!oldFridge) {
            KaleidoscopeChineseFood.LOGGER.warn("Built-in resource pack old_fridge failed to register");
        }
        if (ModConfig.enableCustomPacks) {
            boolean fuzzy = ResourceLoader.registerBuiltinPack(
                    KaleidoscopeChineseFood.id("fuzzy_cooking_recipes"),
                    FabricLoader.getInstance().getModContainer(KaleidoscopeChineseFood.MODID).orElseThrow(),
                    Component.translatable("resourcepack.kaleidoscope_chinesefood.fuzzy_cooking_recipes"),
                    PackActivationType.ALWAYS_ENABLED
            );
            if (!fuzzy) {
                KaleidoscopeChineseFood.LOGGER.warn("Built-in resource pack fuzzy_cooking_recipes failed to register");
            }
        }
    }
}
