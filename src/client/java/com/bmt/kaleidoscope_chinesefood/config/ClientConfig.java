package com.bmt.kaleidoscope_chinesefood.config;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class ClientConfig {
    public static final ModConfigSpec SPEC;
    public static float COUPLET_TEXT_SCALE;
    public static float COUPLET_HORIZONTAL_OFFSET;
    public static float COUPLET_DOUBLE_BASE_Y;
    public static float COUPLET_TRIPLE_BASE_Y;
    public static float COUPLET_VERTICAL_SPACING;
    public static float BANNER_TEXT_SCALE;
    public static float BANNER_CHAR_WIDTH;
    public static float BANNER_SINGLE_OFFSET;
    public static float BANNER_DOUBLE_OFFSET;
    public static float BANNER_TRIPLE_OFFSET;
    public static float BANNER_VERTICAL_OFFSET;
    public static ConfigValue<Double> _COUPLET_TEXT_SCALE;
    public static ConfigValue<Double> _COUPLET_HORIZONTAL_OFFSET;
    public static ConfigValue<Double> _COUPLET_DOUBLE_BASE_Y;
    public static ConfigValue<Double> _COUPLET_TRIPLE_BASE_Y;
    public static ConfigValue<Double> _COUPLET_VERTICAL_SPACING;
    public static ConfigValue<Double> _BANNER_TEXT_SCALE;
    public static ConfigValue<Double> _BANNER_CHAR_WIDTH;
    public static ConfigValue<Double> _BANNER_SINGLE_OFFSET;
    public static ConfigValue<Double> _BANNER_DOUBLE_OFFSET;
    public static ConfigValue<Double> _BANNER_TRIPLE_OFFSET;
    public static ConfigValue<Double> _BANNER_VERTICAL_OFFSET;
    private static final double TEXT_SCALE_MIN = 0.01;
    private static final double TEXT_SCALE_MAX = 0.05;
    private static final double HORIZONTAL_OFFSET_MIN = -1.0;
    private static final double HORIZONTAL_OFFSET_MAX = 1.0;
    private static final double BASE_Y_MIN = -100.0;
    private static final double BASE_Y_MAX = 100.0;
    private static final double VERTICAL_SPACING_MIN = 5.0;
    private static final double VERTICAL_SPACING_MAX = 15.0;
    private static final double BANNER_SCALE_MIN = 0.01;
    private static final double BANNER_SCALE_MAX = 0.05;
    private static final double BANNER_CHAR_WIDTH_MIN = 5.0;
    private static final double BANNER_CHAR_WIDTH_MAX = 15.0;
    private static final double BANNER_HORIZONTAL_OFFSET_MIN = -50.0;
    private static final double BANNER_HORIZONTAL_OFFSET_MAX = 50.0;
    private static final double BANNER_VERTICAL_OFFSET_MIN = -5.0;
    private static final double BANNER_VERTICAL_OFFSET_MAX = 5.0;

    public static void init() {
        NeoForgeConfigRegistry.INSTANCE.register(KaleidoscopeChineseFood.MODID, ModConfig.Type.CLIENT, SPEC);
        NeoForgeModConfigEvents.loading(KaleidoscopeChineseFood.MODID).register(ClientConfig::onLoad);
        NeoForgeModConfigEvents.reloading(KaleidoscopeChineseFood.MODID).register(ClientConfig::onReload);
        validateAndCacheConfigValues();
    }

    private static void onLoad(ModConfig config) {
        if (config.getSpec() == SPEC) {
            validateAndCacheConfigValues();
        }
    }

    private static void onReload(ModConfig config) {
        if (config.getSpec() == SPEC) {
            validateAndCacheConfigValues();
        }
    }

    public static void validateAndCacheConfigValues() {
        COUPLET_TEXT_SCALE = clampValue(_COUPLET_TEXT_SCALE, 0.01, 0.05).floatValue();
        COUPLET_HORIZONTAL_OFFSET = clampValue(_COUPLET_HORIZONTAL_OFFSET, -1.0, 1.0).floatValue();
        COUPLET_DOUBLE_BASE_Y = clampValue(_COUPLET_DOUBLE_BASE_Y, -100.0, 100.0).floatValue();
        COUPLET_TRIPLE_BASE_Y = clampValue(_COUPLET_TRIPLE_BASE_Y, -100.0, 100.0).floatValue();
        COUPLET_VERTICAL_SPACING = clampValue(_COUPLET_VERTICAL_SPACING, 5.0, 15.0).floatValue();
        BANNER_TEXT_SCALE = clampValue(_BANNER_TEXT_SCALE, 0.01, 0.05).floatValue();
        BANNER_CHAR_WIDTH = clampValue(_BANNER_CHAR_WIDTH, 5.0, 15.0).floatValue();
        BANNER_SINGLE_OFFSET = clampValue(_BANNER_SINGLE_OFFSET, -50.0, 50.0).floatValue();
        BANNER_DOUBLE_OFFSET = clampValue(_BANNER_DOUBLE_OFFSET, -50.0, 50.0).floatValue();
        BANNER_TRIPLE_OFFSET = clampValue(_BANNER_TRIPLE_OFFSET, -50.0, 50.0).floatValue();
        BANNER_VERTICAL_OFFSET = clampValue(_BANNER_VERTICAL_OFFSET, -5.0, 5.0).floatValue();
    }

    private static Double clampValue(ConfigValue<Double> value, double min, double max) {
        double current = value.get();
        if (current < min) {
            value.set(min);
            return min;
        } else if (current > max) {
            value.set(max);
            return max;
        } else {
            return current;
        }
    }

    static {
        Builder BUILDER = new Builder();
        BUILDER.push("Couplet Rendering Settings");
        _COUPLET_TEXT_SCALE = BUILDER.comment("对联文字大小 | 默认值: 0.025 | 范围: 0.01-0.05").define("textScale", 0.025);
        _COUPLET_HORIZONTAL_OFFSET = BUILDER.comment("对联文字水平偏移量 | 正值向右，负值向左 | 默认值: 0.2 | 范围: -1.0-1.0").define("horizontalOffset", 0.2);
        _COUPLET_DOUBLE_BASE_Y = BUILDER.comment("(2格高)对联文字垂直偏移量 | 正值向上，负值向下 | 默认值: -19.5 | 范围: -100.0-100.0").define("doubleBaseY", -19.5);
        _COUPLET_TRIPLE_BASE_Y = BUILDER.comment("(3格高)对联文字垂直偏移量 | 正值向上，负值向下 | 默认值: -39.5 | 范围: -100.0-100.0").define("tripleBaseY", -39.5);
        _COUPLET_VERTICAL_SPACING = BUILDER.comment("对联文字间距 | 默认值: 9.6 | 范围: 5.0-15.0").define("verticalSpacing", 9.6);
        BUILDER.pop();
        BUILDER.push("Banner Rendering Settings");
        _BANNER_TEXT_SCALE = BUILDER.comment("横幅文字大小 | 默认值: 0.022 | 范围: 0.01-0.05").define("bannerTextScale", 0.022);
        _BANNER_VERTICAL_OFFSET = BUILDER.comment("横幅文字垂直偏移量 | 正值向上，负值向下 | 默认值: 2.50 | 范围: -5.0-5.0").define("bannerVerticalOffset", 2.5);
        _BANNER_SINGLE_OFFSET = BUILDER.comment("(1格宽)横幅文字水平偏移量 | 正值向右，负值向左 | 默认值: -0.9 | 范围: -50.0-50.0").define("bannerSingleOffset", -0.9);
        _BANNER_DOUBLE_OFFSET = BUILDER.comment("(2格宽)横幅文字水平偏移量 | 正值向右，负值向左 | 默认值: 14.00 | 范围: -50.0-50.0").define("bannerDoubleOffset", 14.0);
        _BANNER_TRIPLE_OFFSET = BUILDER.comment("(3格宽)横幅文字水平偏移量 | 正值向右，负值向左 | 默认值: 28.9 | 范围: -50.0-50.0").define("bannerTripleOffset", 28.9);
        _BANNER_CHAR_WIDTH = BUILDER.comment("横幅文字间距 | 默认值: 9.5 | 范围: 5.0-15.0").define("bannerCharWidth", 9.5);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
