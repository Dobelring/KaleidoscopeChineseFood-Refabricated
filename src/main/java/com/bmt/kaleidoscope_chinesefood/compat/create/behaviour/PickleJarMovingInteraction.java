package com.bmt.kaleidoscope_chinesefood.compat.create.behaviour;

import com.bmt.kaleidoscope_chinesefood.api.IPickleJar;
import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionBlocks;
import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionVirtualBlockEntities;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * 机械上泡菜坛的完整交互（对齐官方 PickleJarMovingInteraction）：
 * 开关盖/发酵/存取，全部经虚拟 BE 与 ContraptionBlocks.update 写回。
 */
public class PickleJarMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand hand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        StructureTemplate.StructureBlockInfo info = ContraptionBlocks.getInfo(contraptionEntity, localPos);
        if (info == null || !(info.state().getBlock() instanceof PickleJarBlock)) {
            return false;
        }
        if (contraptionEntity.level().isClientSide) {
            return true;
        }
        BlockEntity be = ContraptionVirtualBlockEntities.getOrCreateBlockEntity(contraptionEntity, localPos, info);
        if (!(be instanceof IPickleJar jar)) {
            return false;
        }
        BlockState state = info.state();
        Level level = contraptionEntity.level();
        if (state.getValue(PickleJarBlock.FERMENTING)) {
            int remainingSeconds = Math.max(0, (jar.getMaxProgress() - jar.getProgress()) / 20);
            player.displayClientMessage(Component.translatable("message.kaleidoscope_chinesefood.fermenting_remaining", remainingSeconds), true);
            return true;
        }
        if (player.isShiftKeyDown()) {
            BlockState newState;
            boolean isOpen = state.getValue(PickleJarBlock.OPEN);
            if (isOpen) {
                boolean hasValidRecipe = jar.tryStartFermenting(level);
                newState = state.setValue(PickleJarBlock.OPEN, false).setValue(PickleJarBlock.FERMENTING, hasValidRecipe).setValue(PickleJarBlock.DONE, false);
                if (hasValidRecipe) {
                    player.displayClientMessage(Component.translatable("message.kaleidoscope_chinesefood.start_fermenting"), true);
                }
            } else {
                jar.resetProgress();
                newState = state.setValue(PickleJarBlock.OPEN, true).setValue(PickleJarBlock.FERMENTING, false).setValue(PickleJarBlock.DONE, false);
            }
            ContraptionBlocks.update(contraptionEntity, localPos, newState, ContraptionBlocks.save((BlockEntity)be, level));
            ContraptionBlocks.playSound(contraptionEntity, localPos, isOpen ? SoundEvents.BARREL_CLOSE : SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        if (state.getValue(PickleJarBlock.OPEN)) {
            ItemStack held = player.getItemInHand(hand);
            boolean extracted = jar.extractItem(player, held);
            boolean inserted = !extracted && !held.isEmpty() && jar.insertItem(held, player);
            if (extracted || inserted) {
                ContraptionBlocks.update(contraptionEntity, localPos, state, ContraptionBlocks.save((BlockEntity)be, level));
                ContraptionBlocks.playSound(contraptionEntity, localPos, extracted ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8F, 1.1F);
            }
            return true;
        }
        return false;
    }
}
