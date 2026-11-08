package com.bmt.kaleidoscope_chinesefood.compat.create.behaviour;

import com.bmt.kaleidoscope_chinesefood.block.BowlStackBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.BowlStackBlockEntity;
import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionBlocks;
import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionVirtualBlockEntities;
import com.bmt.kaleidoscope_chinesefood.util.forge.ItemHandlerHelper;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * 机械上碗堆的交互（对齐官方 BowlStackMovingInteraction）：翻木/加碗/取碗。
 */
public class BowlStackMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand hand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        StructureTemplate.StructureBlockInfo info = ContraptionBlocks.getInfo(contraptionEntity, localPos);
        if (info == null || !(info.state().getBlock() instanceof BowlStackBlock)) {
            return false;
        }
        Level level = contraptionEntity.level();
        if (level.isClientSide) {
            return true;
        }
        BlockEntity be = ContraptionVirtualBlockEntities.getOrCreateBlockEntity(contraptionEntity, localPos, info);
        if (!(be instanceof BowlStackBlockEntity stack)) {
            return false;
        }
        BlockState state = info.state();
        ItemStack held = player.getItemInHand(hand);
        int count = state.getValue(BowlStackBlock.BOWL_COUNT);
        if (held.isEmpty() && player.isShiftKeyDown()) {
            ContraptionBlocks.update(contraptionEntity, localPos, state.setValue(BowlStackBlock.IS_WOODEN, !state.getValue(BowlStackBlock.IS_WOODEN)), ContraptionBlocks.save(stack, level));
            ContraptionBlocks.playSound(contraptionEntity, localPos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        if (held.is(Items.BOWL) && count < 3 && stack.addBowl()) {
            if (!player.isCreative()) {
                held.shrink(1);
            }
            ContraptionBlocks.update(contraptionEntity, localPos, state.setValue(BowlStackBlock.BOWL_COUNT, stack.getBowlCount()), ContraptionBlocks.save(stack, level));
            ContraptionBlocks.playSound(contraptionEntity, localPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        if (held.isEmpty() && count > 0) {
            ItemStack bowl = stack.removeBowl();
            if (!bowl.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(player, bowl);
            }
            ContraptionBlocks.update(contraptionEntity, localPos, state.setValue(BowlStackBlock.BOWL_COUNT, stack.getBowlCount()), ContraptionBlocks.save(stack, level));
            ContraptionBlocks.playSound(contraptionEntity, localPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.8F, 1.0F);
            return true;
        }
        return false;
    }
}
