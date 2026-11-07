package com.bmt.kaleidoscope_chinesefood.compat.create;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Create 机械方块数据同步包（对齐官方 compat.create.ContraptionBlockDataPayload），
 * Fabric 网络范式：服务端 → 客户端。客户端接收见 ClientPacketHandler。
 */
public final class ContraptionBlockDataPayload implements FabricPacket {
    public static final PacketType<ContraptionBlockDataPayload> TYPE =
        PacketType.create(new ResourceLocation("kaleidoscope_chinesefood", "contraption_block_data"), ContraptionBlockDataPayload::new);

    private final int entityId;
    private final BlockPos localPos;
    private final BlockState state;
    private final CompoundTag nbt;

    public ContraptionBlockDataPayload(int entityId, BlockPos localPos, BlockState state, CompoundTag nbt) {
        this.entityId = entityId;
        this.localPos = localPos;
        this.state = state;
        this.nbt = nbt;
    }

    public ContraptionBlockDataPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBlockPos(), readState(buf), buf.readNbt());
    }

    private static BlockState readState(FriendlyByteBuf buf) {
        CompoundTag stateTag = buf.readNbt();
        return stateTag == null
            ? Blocks.AIR.defaultBlockState()
            : NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), stateTag);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeBlockPos(this.localPos);
        buf.writeNbt(NbtUtils.writeBlockState(this.state));
        buf.writeNbt(this.nbt);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public BlockPos getLocalPos() {
        return this.localPos;
    }

    public BlockState getState() {
        return this.state;
    }

    public CompoundTag getNbt() {
        return this.nbt;
    }
}
