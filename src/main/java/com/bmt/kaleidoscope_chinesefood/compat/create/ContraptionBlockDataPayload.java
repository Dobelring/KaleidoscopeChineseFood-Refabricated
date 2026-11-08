package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Create 机械方块数据同步包（对齐官方 compat.create.ContraptionBlockDataPayload），
 * 26.x Fabric 网络范式（CustomPacketPayload + StreamCodec）：服务端 → 客户端。
 * 客户端接收见 ClientContraptionBlockDataHandler（client 源集）。
 */
public record ContraptionBlockDataPayload(int entityId, BlockPos localPos, BlockState state, CompoundTag nbt)
        implements CustomPacketPayload {
    public static final Type<ContraptionBlockDataPayload> TYPE =
            new Type<>(KaleidoscopeChineseFood.id("contraption_block_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ContraptionBlockDataPayload> STREAM_CODEC = StreamCodec.of(
            ContraptionBlockDataPayload::write,
            ContraptionBlockDataPayload::read
    );

    private static ContraptionBlockDataPayload read(RegistryFriendlyByteBuf buf) {
        int entityId = buf.readInt();
        BlockPos localPos = buf.readBlockPos();
        CompoundTag stateTag = buf.readNbt();
        BlockState state = stateTag == null
                ? Blocks.AIR.defaultBlockState()
                : NbtUtils.readBlockState(buf.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK), stateTag);
        CompoundTag nbt = buf.readNbt();
        return new ContraptionBlockDataPayload(entityId, localPos, state, nbt == null ? new CompoundTag() : nbt);
    }

    private static void write(RegistryFriendlyByteBuf buf, ContraptionBlockDataPayload msg) {
        buf.writeInt(msg.entityId());
        buf.writeBlockPos(msg.localPos());
        buf.writeNbt(NbtUtils.writeBlockState(msg.state()));
        buf.writeNbt(msg.nbt());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
