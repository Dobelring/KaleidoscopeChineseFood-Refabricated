package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.client.light.DynamicLightEvents;
import com.bmt.kaleidoscope_chinesefood.config.ClientConfig;
import net.fabricmc.api.ClientModInitializer;

public class KaleidoscopeChineseFoodClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfig.init();
        ClientSetup.init();
        ClientNetworkHandlers.register();
        // 动态光引擎(原 DynamicLightEvents 的 ClientTickEvent.Post 订阅)
        DynamicLightEvents.register();
    }
}
