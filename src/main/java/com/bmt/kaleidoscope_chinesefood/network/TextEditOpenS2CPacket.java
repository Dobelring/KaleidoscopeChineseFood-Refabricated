package com.bmt.kaleidoscope_chinesefood.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * 服务端 → 客户端：请求打开告示/对联文本编辑界面。
 * 原 Forge 的 encode/decode/handle + DistExecutor 分派改为 FabricPacket 范式，
 * 客户端接收器在 {@link ClientPacketHandler#registerClient()} 里注册。
 */
public class TextEditOpenS2CPacket implements FabricPacket {
    public static final PacketType<TextEditOpenS2CPacket> TYPE = PacketType.create(ModNetwork.TEXT_EDIT_OPEN_PACKET, TextEditOpenS2CPacket::new);

    private final BlockPos pos;

    public TextEditOpenS2CPacket(BlockPos pos) {
        this.pos = pos;
    }

    /** 原 decode(FriendlyByteBuf)。 */
    public TextEditOpenS2CPacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    /** 原 encode(TextEditOpenS2CPacket, FriendlyByteBuf)。 */
    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    /** 客户端接收器需要读取坐标。 */
    public BlockPos getPos() {
        return this.pos;
    }
}
