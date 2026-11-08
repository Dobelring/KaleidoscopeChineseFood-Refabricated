package com.bmt.kaleidoscope_chinesefood.config;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

/**
 * 1.2.0：删除 enableCustomPacks（内置资源包改为始终注册，对齐官方）；
 * 新增 instantSmeltingOnlyOres（瞬间熔炼仅对矿石生效，默认关）。
 */
public class ModConfig {
    public static final ModConfigSpec SPEC;
    public static final BooleanValue INSTANT_SMELTING_ONLY_ORES;
    public static boolean instantSmeltingOnlyOres = false;

    public static void init() {
        ConfigRegistry.INSTANCE.register(KaleidoscopeChineseFood.MODID, net.neoforged.fml.config.ModConfig.Type.COMMON, SPEC);
        ModConfigEvents.loading(KaleidoscopeChineseFood.MODID).register(ModConfig::onLoad);
        ModConfigEvents.reloading(KaleidoscopeChineseFood.MODID).register(ModConfig::onReload);
    }

    private static void onLoad(net.neoforged.fml.config.ModConfig config) {
        if (config.getType() == net.neoforged.fml.config.ModConfig.Type.COMMON) {
            instantSmeltingOnlyOres = INSTANT_SMELTING_ONLY_ORES.get();
        }
    }

    private static void onReload(net.neoforged.fml.config.ModConfig config) {
        if (config.getType() == net.neoforged.fml.config.ModConfig.Type.COMMON) {
            instantSmeltingOnlyOres = INSTANT_SMELTING_ONLY_ORES.get();
        }
    }

    static {
        Builder BUILDER = new Builder();
        BUILDER.push("Compatibility Settings");
        INSTANT_SMELTING_ONLY_ORES = BUILDER.comment("instantSmeltingOnlyOres: instant smelting effect only applies to ore blocks (default false)").define("instantSmeltingOnlyOres", false);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
