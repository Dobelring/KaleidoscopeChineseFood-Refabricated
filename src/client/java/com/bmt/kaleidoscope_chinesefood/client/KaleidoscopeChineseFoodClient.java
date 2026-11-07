package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.client.light.DynamicLightEvents;
import net.fabricmc.api.ClientModInitializer;

public class KaleidoscopeChineseFoodClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 1.2.0:配置 GUI 删除(对齐官方),ClientConfig 滑条随 GlyphMetrics 排版一起移除
        ClientSetup.init();
        ClientNetworkHandlers.register();
        // 动态光引擎(原 DynamicLightEvents 的 ClientTickEvent.Post 订阅)
        DynamicLightEvents.register();
    }
}
