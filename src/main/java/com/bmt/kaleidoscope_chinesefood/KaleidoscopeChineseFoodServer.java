package com.bmt.kaleidoscope_chinesefood;

import net.fabricmc.api.DedicatedServerModInitializer;

/**
 * 专用服务端入口点：与客户端入口点一样在所有 main 入口点之后运行，
 * 负责触发依赖 cookery 的食物/物品注册阶段（见 {@link #runFoodPhase}）。
 */
public class KaleidoscopeChineseFoodServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        KaleidoscopeChineseFood.runFoodPhase();
    }
}
