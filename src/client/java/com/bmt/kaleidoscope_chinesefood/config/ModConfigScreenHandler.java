package com.bmt.kaleidoscope_chinesefood.config;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;

public class ModConfigScreenHandler {
    public static void register() {
        ConfigScreenFactoryRegistry.INSTANCE.register(KaleidoscopeChineseFood.MODID, (modId, parent) -> new ModConfigScreen(parent));
    }
}
