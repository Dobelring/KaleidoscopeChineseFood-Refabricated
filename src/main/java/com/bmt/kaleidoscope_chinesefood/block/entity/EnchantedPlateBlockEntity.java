package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EnchantedPlateBlockEntity extends BlockEntity {
    public EnchantedPlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENCHANTED_PLATE, pos, state);
    }
}
