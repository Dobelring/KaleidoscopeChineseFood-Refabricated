package com.bmt.kaleidoscope_chinesefood.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

/**
 * 配置类。Forge Config API Port 在 Fabric 1.20.1 上原样提供
 * {@link net.minecraftforge.common.ForgeConfigSpec}，所以配置写法保持不变。
 * <p>
 * 原 Forge 的 {@code @EventBusSubscriber(modid = ..., bus = Bus.MOD)} 与空的
 * {@code onLoad(Loading)} / {@code onReload(Reloading)} 已删除（Fabric 无 mod 总线）。
 * 注册改由主类完成：
 * {@code ForgeConfigRegistry.INSTANCE.register("kaleidoscope_chinesefood", ModConfig.Type.COMMON, ModConfig.SPEC)}
 * <p>
 * 1.2.0：删除 enableCustomPacks（内置资源包改为始终注册，对齐官方）；
 * 新增 instantSmeltingOnlyOres（瞬间熔炼仅对矿石生效，默认关）。
 */
public class ModConfig {
    private static final Builder BUILDER = new Builder();
    public static final BooleanValue INSTANT_SMELTING_ONLY_ORES = BUILDER.comment("效果是否仅对矿石生效").define("instantSmeltingOnlyOres", false);
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public ModConfig() {
    }

    static {
        BUILDER.push("Compatibility Settings");
        BUILDER.pop();
    }
}
