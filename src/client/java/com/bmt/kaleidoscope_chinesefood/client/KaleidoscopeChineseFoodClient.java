package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.client.event.TooltipEvents;
import com.bmt.kaleidoscope_chinesefood.config.ClientConfig;
import com.bmt.kaleidoscope_chinesefood.config.ModConfigScreenHandler;
import net.fabricmc.api.ClientModInitializer;

public class KaleidoscopeChineseFoodClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfig.init();
        ModConfigScreenHandler.register();
        ClientSetup.init();
        TooltipEvents.register();
    }
}
