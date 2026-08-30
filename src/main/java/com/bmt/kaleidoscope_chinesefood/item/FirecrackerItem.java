package com.bmt.kaleidoscope_chinesefood.item;

import com.bmt.kaleidoscope_chinesefood.block.FirecrackerBlock;
import com.bmt.kaleidoscope_chinesefood.entity.FirecrackerEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class FirecrackerItem extends Item {
   public FirecrackerItem(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResult use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      if (!pLevel.isClientSide()) {
         FirecrackerEntity firecracker = new FirecrackerEntity(pLevel, pPlayer);
         firecracker.setItem(itemstack);
         firecracker.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
         pLevel.addFreshEntity(firecracker);
      }

      // 修复"鞭炮投掷无音效"：移植时丢失，恢复原版的投掷音效
      pLevel.playSound(
         null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL,
         0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
      );

      if (!pPlayer.getAbilities().instabuild) {
         itemstack.shrink(1);
      }

      return InteractionResult.SUCCESS;
   }

   public InteractionResult useOn(UseOnContext pContext) {
      Level level = pContext.getLevel();
      BlockPos clickedPos = pContext.getClickedPos();
      Player player = pContext.getPlayer();
      ItemStack itemstack = pContext.getItemInHand();
      if (player != null && player.isShiftKeyDown()) {
         BlockPos placePos = level.getBlockState(clickedPos).canBeReplaced() ? clickedPos : clickedPos.relative(pContext.getClickedFace());
         if (itemstack.isEmpty() || !player.mayUseItemAt(placePos, pContext.getClickedFace(), itemstack)) {
            return InteractionResult.FAIL;
         }

         if (level.setBlock(placePos, ((FirecrackerBlock)ModBlocks.FIRECRACKER).defaultBlockState(), 11)) {
            // 修复"鞭炮放置声音不对"：移植时丢失，恢复原版的放置音效（火药引燃声）
            level.playSound(player, placePos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            return InteractionResult.SUCCESS;
         }
      }

      return super.useOn(pContext);
   }
}
