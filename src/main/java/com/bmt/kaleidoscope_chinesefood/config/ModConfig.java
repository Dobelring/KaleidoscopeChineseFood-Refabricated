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
 */
public class ModConfig {
    private static final Builder BUILDER = new Builder();
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    public static final BooleanValue ENABLE_CUSTOM_PACKS = BUILDER.comment("是否启用模糊烹饪配方").define("enableCustomPacks", true);

    public ModConfig() {
    }

    static {
        BUILDER.push("Compatibility Settings");
        BUILDER.pop();
    }
}
