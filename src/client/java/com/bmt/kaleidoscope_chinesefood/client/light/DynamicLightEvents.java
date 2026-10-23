package com.bmt.kaleidoscope_chinesefood.client.light;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * 动态光引擎的客户端驱动。原 NeoForge/Forge 是 {@code DynamicLightEvents} 的
 * ClientTickEvent.Post 订阅者，Fabric 改为在客户端入口点调用 {@link #register()}。
 */
@Environment(EnvType.CLIENT)
public final class DynamicLightEvents {
   private DynamicLightEvents() {
   }

   public static void register() {
      ClientTickEvents.END_CLIENT_TICK.register(client -> DynamicLightManager.onClientTick());
   }
}
