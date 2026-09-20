package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 附魔金苹果拼盘的方块实体。
 * <p>
 * 自身不存数据，只为把 {@link com.bmt.kaleidoscope_chinesefood.client.renderer.EnchantedPlateBlockEntityRenderer}
 * 挂到附魔拼盘方块上（渲染附魔光效）。
 */
public class EnchantedPlateBlockEntity extends BlockEntity {
    public EnchantedPlateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ENCHANTED_PLATE, pos, state);
    }
}
