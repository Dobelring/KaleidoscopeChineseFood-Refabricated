package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

/**
 * 承载机械上虚拟方块实体的假世界（对齐官方 compat.create.ContraptionVirtualLevel）：
 * 方块/NBT 变更直接写回 contraption 的 StructureBlockInfo 表。
 */
public class ContraptionVirtualLevel extends ContraptionWorld {
    private final AbstractContraptionEntity entity;
    private final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();

    public ContraptionVirtualLevel(AbstractContraptionEntity entity) {
        super(entity.level(), entity.getContraption());
        this.entity = entity;
    }

    public AbstractContraptionEntity getEntity() {
        return this.entity;
    }

    public void putBlockEntity(BlockPos pos, BlockEntity be) {
        this.blockEntities.put(pos, be);
    }

    public void removeBlockEntity(BlockPos pos) {
        this.blockEntities.remove(pos);
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.blockEntities.get(pos);
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags) {
        return this.setBlock(pos, state, flags, 512);
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        StructureTemplate.StructureBlockInfo info = ContraptionBlocks.getInfo(this.entity, pos);
        CompoundTag nbt = info == null ? null : info.nbt();
        BlockEntity be = this.blockEntities.get(pos);
        if (be != null) {
            nbt = ContraptionBlocks.save(be, this);
        }
        ContraptionBlocks.update(this.entity, pos, state, nbt);
        if (be != null) {
            be.setBlockState(state);
        }
        return true;
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean isMoving) {
        ContraptionBlocks.remove(this.entity, pos);
        return true;
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity source, int recursionLeft) {
        return this.removeBlock(pos, false);
    }

    @Override
    public void blockUpdated(BlockPos pos, Block block) {
    }

    @Override
    public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        StructureTemplate.StructureBlockInfo info = ContraptionBlocks.getInfo(this.entity, pos);
        BlockEntity be = this.blockEntities.get(pos);
        if (info == null || be == null) {
            return;
        }
        ContraptionBlocks.updateNbt(this.entity, pos, ContraptionBlocks.save(be, this));
    }

    @Override
    public void updateNeighbourForOutputSignal(BlockPos pos, Block block) {
    }

    @Override
    public void playSound(@Nullable Player player, BlockPos pos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        ContraptionBlocks.playSound(this.entity, pos, sound, source, volume, pitch);
    }
}
