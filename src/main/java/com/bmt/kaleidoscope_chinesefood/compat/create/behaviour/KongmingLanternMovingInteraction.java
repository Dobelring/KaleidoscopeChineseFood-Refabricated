package com.bmt.kaleidoscope_chinesefood.compat.create.behaviour;

import com.bmt.kaleidoscope_chinesefood.block.KongmingLanternBlock;
import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionBlocks;
import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * 机械上孔明灯的点燃升空（对齐官方 KongmingLanternMovingInteraction）：
 * 用打火石/火 charges 点燃后移除方块并生成灯笼实体。
 */
public class KongmingLanternMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand hand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
        if (!ContraptionBlocks.isKaleidoscopeContraptionLoaded()) {
            return false;
        }
        StructureTemplate.StructureBlockInfo info = ContraptionBlocks.getInfo(contraptionEntity, localPos);
        if (info == null || !(info.state().getBlock() instanceof KongmingLanternBlock)) {
            return false;
        }
        ItemStack held = player.getItemInHand(hand);
        boolean fireCharge = held.is(Items.FIRE_CHARGE);
        if (!fireCharge && !(held.getItem() instanceof FlintAndSteelItem)) {
            return false;
        }
        Level level = contraptionEntity.level();
        if (level.isClientSide) {
            return true;
        }
        Vec3 globalPos = contraptionEntity.toGlobalVector(Vec3.atLowerCornerOf(localPos), 1.0F);
        KongmingLanternEntity lantern = ModEntities.KONGMING_LANTERN.create(level);
        if (lantern == null) {
            return false;
        }
        lantern.setPos(globalPos.x, globalPos.y, globalPos.z);
        lantern.setYRot(contraptionEntity.getYRot());
        lantern.setYHeadRot(contraptionEntity.getYRot());
        level.addFreshEntity(lantern);
        ContraptionBlocks.remove(contraptionEntity, localPos);
        if (!player.getAbilities().instabuild) {
            if (fireCharge) {
                held.shrink(1);
            } else {
                EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(slot));
            }
        }
        ContraptionBlocks.playSound(contraptionEntity, localPos, fireCharge ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }
}
