package com.bmt.kaleidoscope_chinesefood.integration;

import net.fabricmc.loader.api.FabricLoader;

/**
 * 与「森罗物语：玩偶」的联动入口。
 * <p>
 * 语义与原 Forge 版一致：只有当玩偶模组存在、且 nether / world_liquor
 * 这两个「本应提供这些作者玩偶」的联动模组都缺失时，
 * 才由国味自己注册 {@code kaleidoscope_chinesefood:doll_0..doll_5}。
 * <p>
 * 注意：本类不能引用玩偶模组的任何类（软依赖），真正的注册在
 * {@link KaleidoscopeDollIntegrationImpl} 里，只有守卫通过时才会被类加载。
 */
public class KaleidoscopeDollIntegration {
    private static final String DOLL_MOD_ID = "kaleidoscope_doll";
    private static final String NETHER_MOD_ID = "kaleidoscope_nether";
    private static final String LIQUOR_MOD_ID = "kaleidoscope_world_liquor";

    public KaleidoscopeDollIntegration() {
    }

    public static void register() {
        if (FabricLoader.getInstance().isModLoaded(DOLL_MOD_ID)
            && !FabricLoader.getInstance().isModLoaded(NETHER_MOD_ID)
            && !FabricLoader.getInstance().isModLoaded(LIQUOR_MOD_ID)
        ) {
            KaleidoscopeDollIntegrationImpl.register();
        }
    }
}
