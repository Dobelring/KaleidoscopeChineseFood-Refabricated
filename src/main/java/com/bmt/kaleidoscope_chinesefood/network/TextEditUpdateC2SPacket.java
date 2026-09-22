package com.bmt.kaleidoscope_chinesefood.network;

import com.bmt.kaleidoscope_chinesefood.block.entity.IWallTextBlockEntity;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * 客户端 → 服务端：提交编辑后的文本。
 * 原 Forge 的 encode/decode/handle 改为 FabricPacket 范式，
 * 注册在 {@link ModNetwork#init()}（服务端接收器）。
 * 原 {@code NetworkEvent.Context.enqueueWork/setPacketHandled} 已删除（Fabric 的 receive 已在主线程执行）。
 */
public class TextEditUpdateC2SPacket implements FabricPacket, ServerPlayNetworking.PlayPacketHandler<TextEditUpdateC2SPacket> {
    public static final PacketType<TextEditUpdateC2SPacket> TYPE = PacketType.create(ModNetwork.TEXT_EDIT_UPDATE_PACKET, TextEditUpdateC2SPacket::new);

    private final BlockPos pos;
    private final String text;

    public TextEditUpdateC2SPacket(BlockPos pos, String text) {
        this.pos = pos;
        this.text = text;
    }

    /** 原 decode(FriendlyByteBuf)。 */
    public TextEditUpdateC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readUtf(2048));
    }

    /** Fabric 的 registerGlobalReceiver 需要一个无参实例作为接收器（见 cookery 的 ThrowBaoziMessage）。 */
    public TextEditUpdateC2SPacket() {
        this(BlockPos.ZERO, "");
    }

    /** 原 encode(TextEditUpdateC2SPacket, FriendlyByteBuf)。 */
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeUtf(this.text, 2048);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    /** 原 handle(TextEditUpdateC2SPacket, Supplier<Context>) 的内容。 */
    @Override
    public void receive(TextEditUpdateC2SPacket msg, ServerPlayer player, PacketSender sender) {
        if (player != null) {
            Level level = player.level();
            BlockPos pos = msg.pos;
            if (level.isLoaded(pos)) {
                if (level.getBlockEntity(pos) instanceof IWallTextBlockEntity textBe) {
                    if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) {
                        return;
                    }

                    int max = textBe.getMaxChars();
                    String safe = msg.text == null ? "" : msg.text;
                    safe = safe.replaceAll("[\r\n]", "");
                    if (safe.length() > max) {
                        safe = safe.substring(0, max);
                    }

                    textBe.setText(safe);
                }
            }
        }
    }
}
