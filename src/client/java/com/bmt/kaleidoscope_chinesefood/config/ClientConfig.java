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
    public static ConfigValue<Integer> _CONFIG_VERSION;
    /** 渲染调参版本号；低于此值的 toml 会被重置为新默认值 */
    private static final int CONFIG_VERSION = 2;
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
        // 配置在 mod 初始化阶段就已经读进 SPEC，此时直接迁移一次；
        // 不能只依赖 loading 事件（实测该事件在本移植版下不会在缓存之前生效）
        migrateIfStale();
        validateAndCacheConfigValues();
    }

    private static void onLoad(ModConfig config) {
        if (config.getType() == ModConfig.Type.CLIENT) {
            migrateIfStale();
            validateAndCacheConfigValues();
        }
    }

    private static void onReload(ModConfig config) {
        if (config.getType() == ModConfig.Type.CLIENT) {
            migrateIfStale();
            validateAndCacheConfigValues();
        }
    }

    /**
     * 1.1.11 重构了对联/横批渲染（按分组锚点渲染 + 发光描边），调参默认值全部变化；
     * 旧 toml 里遗留的 1.1.10 数值套到新渲染器上会明显错位，因此做一次一次性重置。
     * <p>
     * 注意两点实测结论：① 加载事件在本移植版里以 {@code config.getSpec() == SPEC} 判定会失效，
     * 必须按 {@code config.getType()} 判定；② 配置在 mod 初始化阶段往往已经读入，
     * 所以 init() 里也要跑一次（用 isLoaded() 兜底，避免在未加载时碰 SPEC）。
     */
    private static void migrateIfStale() {
        if (!SPEC.isLoaded() || _CONFIG_VERSION.get() >= CONFIG_VERSION) {
            return;
        }

        _COUPLET_TEXT_SCALE.set(0.018);
        _COUPLET_HORIZONTAL_OFFSET.set(0.2);
        _COUPLET_DOUBLE_BASE_Y.set(-26.5);
        _COUPLET_TRIPLE_BASE_Y.set(-54.5);
        _COUPLET_VERTICAL_SPACING.set(11.0);
        _BANNER_TEXT_SCALE.set(0.018);
        _BANNER_VERTICAL_OFFSET.set(4.0);
        _BANNER_SINGLE_OFFSET.set(-0.5);
        _BANNER_DOUBLE_OFFSET.set(19.0);
        _BANNER_TRIPLE_OFFSET.set(38.9);
        _BANNER_CHAR_WIDTH.set(11.0);
        _CONFIG_VERSION.set(CONFIG_VERSION);
        SPEC.save();
        KaleidoscopeChineseFood.LOGGER.info("Migrated couplet/banner render settings to the 1.1.11 defaults");
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
        _COUPLET_TEXT_SCALE = BUILDER.comment("对联文字大小 | 默认值: 0.018 | 范围: 0.01-0.05").define("textScale", 0.018);
        _COUPLET_HORIZONTAL_OFFSET = BUILDER.comment("对联文字水平偏移量 | 正值向右，负值向左 | 默认值: 0.2 | 范围: -1.0-1.0").define("horizontalOffset", 0.2);
        _COUPLET_DOUBLE_BASE_Y = BUILDER.comment("(2格高)对联文字垂直偏移量 | 正值向上，负值向下 | 默认值: -26.5 | 范围: -100.0-100.0").define("doubleBaseY", -26.5);
        _COUPLET_TRIPLE_BASE_Y = BUILDER.comment("(3格高)对联文字垂直偏移量 | 正值向上，负值向下 | 默认值: -54.5 | 范围: -100.0-100.0").define("tripleBaseY", -54.5);
        _COUPLET_VERTICAL_SPACING = BUILDER.comment("对联文字间距 | 默认值: 11.0 | 范围: 5.0-15.0").define("verticalSpacing", 11.0);
        BUILDER.pop();
        BUILDER.push("Banner Rendering Settings");
        _BANNER_TEXT_SCALE = BUILDER.comment("横幅文字大小 | 默认值: 0.018 | 范围: 0.01-0.05").define("bannerTextScale", 0.018);
        _BANNER_VERTICAL_OFFSET = BUILDER.comment("横幅文字垂直偏移量 | 正值向上，负值向下 | 默认值: 4.00 | 范围: -5.0-5.0").define("bannerVerticalOffset", 4.0);
        _BANNER_SINGLE_OFFSET = BUILDER.comment("(1格宽)横幅文字水平偏移量 | 正值向右，负值向左 | 默认值: -0.5 | 范围: -50.0-50.0").define("bannerSingleOffset", -0.5);
        _BANNER_DOUBLE_OFFSET = BUILDER.comment("(2格宽)横幅文字水平偏移量 | 正值向右，负值向左 | 默认值: 19.00 | 范围: -50.0-50.0").define("bannerDoubleOffset", 19.0);
        _BANNER_TRIPLE_OFFSET = BUILDER.comment("(3格宽)横幅文字水平偏移量 | 正值向右，负值向左 | 默认值: 38.9 | 范围: -50.0-50.0").define("bannerTripleOffset", 38.9);
        _BANNER_CHAR_WIDTH = BUILDER.comment("横幅文字间距 | 默认值: 11.0 | 范围: 5.0-15.0").define("bannerCharWidth", 11.0);
        BUILDER.pop();
        BUILDER.push("Internal");
        _CONFIG_VERSION = BUILDER.comment("内部字段：渲染调参版本号，请勿手动修改").define("configVersion", 0);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
