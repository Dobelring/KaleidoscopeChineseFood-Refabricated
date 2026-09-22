package com.bmt.kaleidoscope_chinesefood.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * 网络注册。
 * <p>
 * 原 Forge 的 {@code SimpleChannel} / {@code NetworkRegistry.newSimpleChannel} /
 * {@code PacketDistributor} 在 Fabric 上不存在，改为 Fabric API 的
 * {@code FabricPacket} + {@code PacketType}（范式照抄 cookery 的
 * {@code network/NetworkHandler.java} 与 {@code network/message/ThrowBaoziMessage.java}）。
 * 客户端接收器见 {@link ClientPacketHandler}。
 */
public class ModNetwork {
    // 文本编辑界面：服务端 → 客户端（请求打开编辑界面）
    public static final ResourceLocation TEXT_EDIT_OPEN_PACKET = new ResourceLocation("kaleidoscope_chinesefood", "text_edit_open");
    // 文本编辑界面：客户端 → 服务端（提交文本）
    public static final ResourceLocation TEXT_EDIT_UPDATE_PACKET = new ResourceLocation("kaleidoscope_chinesefood", "text_edit_update");

    public ModNetwork() {
    }

    /**
     * 由主类 {@code KaleidoscopeChineseFood#onInitialize} 调用。
     * 原 {@code CHANNEL.registerMessage(...)} 的两条注册里，S2C 包在 Fabric 上无需在服务端注册，
     * 只有 C2S 包需要注册服务端接收器。
     */
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(TextEditUpdateC2SPacket.TYPE, new TextEditUpdateC2SPacket());
    }

    /**
     * 原 {@code CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg)}。
     * 供 {@code block/CoupletBlock} 与 {@code block/HorizontalBannerBlock} 调用。
     */
    public static void sendToPlayer(TextEditOpenS2CPacket msg, ServerPlayer player) {
        ServerPlayNetworking.send(player, msg);
    }
}
