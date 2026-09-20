package com.bmt.kaleidoscope_chinesefood.network;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.entity.IWallTextBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** 客户端 → 服务端：提交编辑后的墙面文字（服务端做距离与长度校验） */
public record TextEditUpdateC2SPayload(BlockPos pos, String text) implements CustomPacketPayload {
    public static final Type<TextEditUpdateC2SPayload> TYPE = new Type<>(KaleidoscopeChineseFood.id("text_edit_update"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TextEditUpdateC2SPayload> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeBlockPos(msg.pos());
                buf.writeUtf(msg.text(), 2048);
            },
            buf -> new TextEditUpdateC2SPayload(buf.readBlockPos(), buf.readUtf(2048))
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** 服务端校验：区块已加载、目标是墙面文字方块、玩家距离 ≤ 8 格，然后去换行并按上限截断 */
    public static void handle(TextEditUpdateC2SPayload payload, ServerPlayer player) {
        Level level = player.level();
        BlockPos pos = payload.pos();
        if (!level.isLoaded(pos)) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof IWallTextBlockEntity textBe) {
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0) {
                return;
            }

            int max = textBe.getMaxChars();
            String safe = payload.text() == null ? "" : payload.text();
            safe = safe.replaceAll("[\r\n]", "");
            if (safe.length() > max) {
                safe = safe.substring(0, max);
            }

            textBe.setText(safe);
        }
    }
}
