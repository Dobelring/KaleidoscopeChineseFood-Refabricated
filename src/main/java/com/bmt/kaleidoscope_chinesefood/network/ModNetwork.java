package com.bmt.kaleidoscope_chinesefood.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * 网络层注册（common 源集，客户端与服务端都会执行）：
 * 载荷编解码器注册 + 服务端侧 C2S 接收器。
 * 客户端侧 S2C 接收器在客户端入口点注册（见 ClientNetworkHandlers）。
 */
public class ModNetwork {
    public static void register() {
        // 26.x 的 Fabric 网络 API：serverboundPlay/clientboundPlay
        PayloadTypeRegistry.clientboundPlay().register(TextEditOpenS2CPayload.TYPE, TextEditOpenS2CPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TextEditUpdateC2SPayload.TYPE, TextEditUpdateC2SPayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                TextEditUpdateC2SPayload.TYPE,
                (payload, context) -> context.server().execute(() -> TextEditUpdateC2SPayload.handle(payload, context.player()))
        );
    }
}
