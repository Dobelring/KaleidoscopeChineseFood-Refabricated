package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.client.gui.TextEditScreen;
import com.bmt.kaleidoscope_chinesefood.network.TextEditOpenS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

/** 客户端网络接收器（S2C）：收到服务端请求后打开墙面文字编辑界面 */
public class ClientNetworkHandlers {
   public static void register() {
      ClientPlayNetworking.registerGlobalReceiver(TextEditOpenS2CPayload.TYPE, (payload, context) -> {
         Minecraft mc = context.client();
         mc.execute(() -> {
            if (mc.level == null || mc.player == null) {
               return;
            }
            // 文字/上限/格数全部由服务端下发，不读客户端 BE，避免同步延迟导致显示上一个方块的内容
            mc.setScreen(new TextEditScreen(
               payload.pos(), payload.text(), payload.maxChars(), payload.segmentCount(), payload.vertical()
            ));
         });
      });
   }
}
