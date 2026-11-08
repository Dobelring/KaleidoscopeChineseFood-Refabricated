package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.bmt.kaleidoscope_chinesefood.block.entity.FreezerBlockEntity;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * 机械上冰箱的虚拟方块实体：交互距离按机械全局坐标计算（对齐官方同名类）。
 */
public class ContraptionFreezerBlockEntity extends FreezerBlockEntity {
    private final AbstractContraptionEntity contraptionEntity;

    public ContraptionFreezerBlockEntity(BlockPos pos, BlockState state, AbstractContraptionEntity contraptionEntity) {
        super(pos, state);
        this.contraptionEntity = contraptionEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.contraptionEntity.isRemoved()) {
            return false;
        }
        Vec3 center = this.contraptionEntity.toGlobalVector(Vec3.atLowerCornerOf(this.getBlockPos()), 1.0F);
        return player.distanceToSqr(center) <= 64.0;
    }
}
