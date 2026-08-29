package com.bmt.kaleidoscope_chinesefood.item;

import com.bmt.kaleidoscope_chinesefood.block.FirecrackerBlock;
import com.bmt.kaleidoscope_chinesefood.entity.FirecrackerEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
            if (!player.getAbilities().instabuild) {
               itemstack.shrink(1);
            }

            return InteractionResult.SUCCESS;
         }
      }

      return super.useOn(pContext);
   }
}
