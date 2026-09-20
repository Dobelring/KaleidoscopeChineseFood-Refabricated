package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** 只用于给附魔金苹果拼盘挂上附魔光效渲染器，本身不存数据。 */
public class EnchantedPlateBlockEntity extends BlockEntity {
    public EnchantedPlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENCHANTED_PLATE, pos, state);
    }
}
