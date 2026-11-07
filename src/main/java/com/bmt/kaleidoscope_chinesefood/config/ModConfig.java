package com.bmt.kaleidoscope_chinesefood.config;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

/**
 * 配置类。Forge Config API Port 在 Fabric 1.21.1 上提供
 * {@link ModConfigSpec}，所以配置写法保持不变。
 * <p>
 * 1.2.0:删除 enableCustomPacks(内置资源包改为始终注册,对齐官方);
 * 新增 instantSmeltingOnlyOres(瞬间熔炼仅对矿石生效,默认关)。
 */
public class ModConfig {
    public static final Builder BUILDER = new Builder();
    public static final BooleanValue INSTANT_SMELTING_ONLY_ORES =
            BUILDER.comment("效果是否仅对矿石生效").define("instantSmeltingOnlyOres", false);
    public static final ModConfigSpec SPEC;

    public static void init() {
        NeoForgeConfigRegistry.INSTANCE.register(KaleidoscopeChineseFood.MODID, net.neoforged.fml.config.ModConfig.Type.COMMON, SPEC);
    }

    static {
        BUILDER.push("Compatibility Settings");
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
