package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.client.event.TooltipEvents;
import com.bmt.kaleidoscope_chinesefood.client.light.DynamicLightEvents;
import com.bmt.kaleidoscope_chinesefood.compat.ponder.init.PonderCompat;
import net.fabricmc.api.ClientModInitializer;

public class KaleidoscopeChineseFoodClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 依赖 cookery 的注册（food phase）在 client/server 入口点阶段执行，此时 cookery 已初始化完毕
        KaleidoscopeChineseFood.runFoodPhase();
        // 1.2.0：模组配置 GUI 随官方 1.2.0 一并移除（渲染调参 ClientConfig 同步删除，排版改用官方常量）
        ClientSetup.init();
        TooltipEvents.register();
        // 动态光引擎（原 DynamicLightEvents 的 ClientTickEvent.Post 订阅）
        DynamicLightEvents.register();
        // Create 在场时注册泡菜坛的 Ponder 讲解场景（Create Fly 提供 Create 本体）
        PonderCompat.init();
        // S2C：收到服务端请求后打开对联/横批文字编辑界面；Create 机械方块数据同步
        ClientNetworkHandlers.register();
    }
}
