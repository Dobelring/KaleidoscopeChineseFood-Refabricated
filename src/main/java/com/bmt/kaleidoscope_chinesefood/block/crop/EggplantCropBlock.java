package com.bmt.kaleidoscope_chinesefood.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class EggplantCropBlock extends BaseCropBlock {
    public EggplantCropBlock(Properties properties, Supplier<Item> result, Supplier<Item> seed) {
        super(properties, result, seed);
    }

    /**
     * 对齐 cookery 自家作物（ChiliCropBlock/LettuceCropBlock）：空手/镰刀右键成熟作物
     * 直接收割并触发挥手动画。BaseCropBlock 只覆写了 useItemOn（手持物品路径），
     * 而收割触发通过 useWithoutItem 实现，与辣椒/生菜行为一致。
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if ((Integer) state.getValue(AGE) >= this.getMaxAge()) {
            Block.popResource(level, pos, this.result.get().getDefaultInstance());
            this.onUseBreakCrop(level, pos);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}