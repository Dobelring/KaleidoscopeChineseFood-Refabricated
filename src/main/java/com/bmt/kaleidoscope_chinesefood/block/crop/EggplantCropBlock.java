package com.bmt.kaleidoscope_chinesefood.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
     * 对齐 cookery 自家作物（ChiliCropBlock/LettuceCropBlock）：空手右键成熟作物直接收割。
     * BaseCropBlock 只覆写了 useItemOn（手持路径），镰刀/空手回退触发的 useWithoutItem
     * 走原版默认 PASS，导致收割结果与挥手动画判定和辣椒/生菜不一致。
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

    /**
     * 26.1 (1.21.5+) invokes Block.useItemOn before ItemStack.useOn, so a right-click on the
     * fully-grown crop with bone meal in hand would harvest it immediately (cookery's 1.21.1
     * logic), skipping the mature stage the crop should sit in. With bone meal in hand, fall
     * through to the item so it can act on the crop (no-op once mature); harvest only happens
     * on a separate right-click (empty hand / other items), matching the 1.21.1 behavior.
     */
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getItemInHand(hand).getItem() == Items.BONE_MEAL) {
            return InteractionResult.PASS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}