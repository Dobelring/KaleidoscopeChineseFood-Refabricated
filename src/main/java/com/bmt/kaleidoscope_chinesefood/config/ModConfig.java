package com.bmt.kaleidoscope_chinesefood.config;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

public class ModConfig {
    public static final ModConfigSpec SPEC;
    public static final BooleanValue ENABLE_CUSTOM_PACKS;
    public static boolean enableCustomPacks = true;

    public static void init() {
        ConfigRegistry.INSTANCE.register(KaleidoscopeChineseFood.MODID, net.neoforged.fml.config.ModConfig.Type.COMMON, SPEC);
        ModConfigEvents.loading(KaleidoscopeChineseFood.MODID).register(ModConfig::onLoad);
        ModConfigEvents.reloading(KaleidoscopeChineseFood.MODID).register(ModConfig::onReload);
    }

    private static void onLoad(net.neoforged.fml.config.ModConfig config) {
        if (config.getType() == net.neoforged.fml.config.ModConfig.Type.COMMON) {
            enableCustomPacks = ENABLE_CUSTOM_PACKS.get();
        }
    }

    private static void onReload(net.neoforged.fml.config.ModConfig config) {
        if (config.getType() == net.neoforged.fml.config.ModConfig.Type.COMMON) {
            enableCustomPacks = ENABLE_CUSTOM_PACKS.get();
        }
    }

    static {
        Builder BUILDER = new Builder();
        BUILDER.push("Compatibility Settings");
        ENABLE_CUSTOM_PACKS = BUILDER.comment("是否启用模糊烹饪配方").define("enableCustomPacks", true);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
