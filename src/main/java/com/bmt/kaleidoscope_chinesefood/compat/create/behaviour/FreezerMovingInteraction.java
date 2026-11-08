package com.bmt.kaleidoscope_chinesefood.compat.create.behaviour;

import com.bmt.kaleidoscope_chinesefood.block.FreezerBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.FreezerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionBlocks;
import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionVirtualBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModStats;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;

/**
 * 机械上冰箱可被玩家打开（对齐官方 FreezerMovingInteraction）。
 */
public class FreezerMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand hand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        StructureTemplate.StructureBlockInfo info = ContraptionBlocks.getInfo(contraptionEntity, localPos);
        if (info == null || !(info.state().getBlock() instanceof FreezerBlock)) {
            return false;
        }
        if (contraptionEntity.level().isClientSide) {
            return true;
        }
        BlockEntity be = ContraptionVirtualBlockEntities.getOrCreateBlockEntity(contraptionEntity, localPos, info);
        if (!(be instanceof FreezerBlockEntity freezer)) {
            return false;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            ModStats.awardOpenFreezerStat(serverPlayer);
            // FreezerBlockEntity 已是 ExtendedScreenHandlerFactory，Fabric 侧自动写额外数据
            serverPlayer.openMenu(freezer);
        }
        return true;
    }
}
