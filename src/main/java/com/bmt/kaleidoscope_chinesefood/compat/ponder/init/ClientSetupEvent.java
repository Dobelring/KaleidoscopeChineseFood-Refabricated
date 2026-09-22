package com.bmt.kaleidoscope_chinesefood.compat.ponder.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Fabric 上不再使用 Forge 的 @EventBusSubscriber，改由客户端入口点
// （client.KaleidoscopeChineseFoodClient）在初始化时调用 onClientSetup()
@Environment(EnvType.CLIENT)
public class ClientSetupEvent {
    public ClientSetupEvent() {
    }

    public static void onClientSetup() {
        PonderCompat.init();
    }
}
