package com.bmt.kaleidoscope_chinesefood.network;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端 → 客户端：请求打开墙面文字编辑界面。
 * <p>
 * 官方 1.1.11 只下发坐标、由客户端读自己的 BE 取文字，但客户端 BE 可能尚未收到同步
 * （编辑完一个横幅立刻开另一个会显示上一个的文字），因此这里把权威的文字/长度上限/
 * 分组格数一并下发，界面完全不依赖客户端 BE 状态。
 */
public record TextEditOpenS2CPayload(BlockPos pos, String text, int maxChars, int segmentCount, boolean vertical)
        implements CustomPacketPayload {
    public static final Type<TextEditOpenS2CPayload> TYPE = new Type<>(KaleidoscopeChineseFood.id("text_edit_open"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TextEditOpenS2CPayload> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeBlockPos(msg.pos());
                buf.writeUtf(msg.text(), 2048);
                buf.writeVarInt(msg.maxChars());
                buf.writeVarInt(msg.segmentCount());
                buf.writeBoolean(msg.vertical());
            },
            buf -> new TextEditOpenS2CPayload(
                    buf.readBlockPos(), buf.readUtf(2048), buf.readVarInt(), buf.readVarInt(), buf.readBoolean()
            )
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
