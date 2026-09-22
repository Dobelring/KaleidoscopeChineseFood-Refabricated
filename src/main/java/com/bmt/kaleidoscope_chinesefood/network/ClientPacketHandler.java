package com.bmt.kaleidoscope_chinesefood.network;

import com.bmt.kaleidoscope_chinesefood.block.entity.IWallTextBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.gui.TextEditScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

/**
 * 客户端收包处理。
 * 原 Forge 在 {@code TextEditOpenS2CPacket.handle} 里用
 * {@code DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ...)} 分派到本类；
 * Fabric 侧改为在客户端入口调用 {@link #registerClient()} 注册接收器。
 */
@Environment(EnvType.CLIENT)
public class ClientPacketHandler {
    public ClientPacketHandler() {
    }

    /** 由客户端入口 {@code KaleidoscopeChineseFoodClient#onInitializeClient} 调用。 */
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(TextEditOpenS2CPacket.TYPE,
            (packet, player, responseSender) -> openTextEditScreen(packet.getPos()));
    }

    public static void openTextEditScreen(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null) {
            if (mc.level.isLoaded(pos)) {
                if (mc.level.getBlockEntity(pos) instanceof IWallTextBlockEntity textBe) {
                    mc.setScreen(new TextEditScreen(pos, textBe));
                }
            }
        }
    }
}
