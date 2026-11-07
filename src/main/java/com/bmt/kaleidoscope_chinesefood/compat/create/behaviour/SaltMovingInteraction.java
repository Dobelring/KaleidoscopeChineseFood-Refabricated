package com.bmt.kaleidoscope_chinesefood.compat.create.behaviour;

import com.bmt.kaleidoscope_chinesefood.block.SaltBlock;
import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.zurrtum.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import java.util.Collections;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * 机械上盐堆的交互（对齐官方 SaltMovingInteraction）。
 */
public class SaltMovingInteraction extends MovingInteractionBehaviour {
    private static final int MAX_COUNT = Collections.max(SaltBlock.STACK_COUNT.getPossibleValues());

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand hand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        if (!ContraptionBlocks.isKaleidoscopeContraptionLoaded()) {
            return false;
        }
        StructureTemplate.StructureBlockInfo info = ContraptionBlocks.getInfo(contraptionEntity, localPos);
        if (info == null || !(info.state().getBlock() instanceof SaltBlock)) {
            return false;
        }
        Level level = contraptionEntity.level();
        if (level.isClientSide()) {
            return true;
        }
        BlockState state = info.state();
        ItemStack held = player.getItemInHand(hand);
        int count = state.getValue(SaltBlock.STACK_COUNT);
        ItemStack saltItem = new ItemStack(ModBlocks.SALT_BLOCK);
        if (held.is(saltItem.getItem()) && count < MAX_COUNT) {
            if (!player.isCreative()) {
                held.shrink(1);
            }
            ContraptionBlocks.update(contraptionEntity, localPos, state.setValue(SaltBlock.STACK_COUNT, count + 1), info.nbt());
            ContraptionBlocks.playSound(contraptionEntity, localPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        if (!held.isEmpty()) {
            return false;
        }
        if (count > 0) {
            ContraptionBlocks.update(contraptionEntity, localPos, state.setValue(SaltBlock.STACK_COUNT, count - 1), info.nbt());
        } else {
            ContraptionBlocks.remove(contraptionEntity, localPos);
        }
        if (!player.getInventory().add(saltItem)) {
            player.drop(saltItem, false);
        }
        ContraptionBlocks.playSound(contraptionEntity, localPos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.9F, 1.0F);
        return true;
    }
}
