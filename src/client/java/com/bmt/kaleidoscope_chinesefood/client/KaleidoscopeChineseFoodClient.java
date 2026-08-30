package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.client.event.TooltipEvents;
import com.bmt.kaleidoscope_chinesefood.config.ClientConfig;
import com.bmt.kaleidoscope_chinesefood.config.ModConfigScreenHandler;
import net.fabricmc.api.ClientModInitializer;

public class KaleidoscopeChineseFoodClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 与 cookery 相关的食物/物品注册必须等所有 main 入口点跑完（cookery 初始化完毕），
        // Fabric 按字母序调用入口点（chinesefood 在 cookery 之前），详见 runFoodPhase 注释
        KaleidoscopeChineseFood.runFoodPhase();
        ClientConfig.init();
        ModConfigScreenHandler.register();
        ClientSetup.init();
        TooltipEvents.register();
    }
}
