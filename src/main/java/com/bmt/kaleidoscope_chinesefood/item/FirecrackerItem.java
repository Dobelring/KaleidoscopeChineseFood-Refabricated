package com.bmt.kaleidoscope_chinesefood.item;

import com.bmt.kaleidoscope_chinesefood.block.FirecrackerBlock;
import com.bmt.kaleidoscope_chinesefood.entity.FirecrackerEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class FirecrackerItem extends Item {
   public FirecrackerItem(Properties pProperties) {
      super(pProperties);
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      if (!pLevel.isClientSide) {
         FirecrackerEntity firecracker = new FirecrackerEntity(pLevel, pPlayer);
         firecracker.setItem(itemstack);
         firecracker.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
         pLevel.addFreshEntity(firecracker);
      }

      if (!pPlayer.getAbilities().instabuild) {
         itemstack.shrink(1);
      }

      return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide);
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

            return InteractionResult.sidedSuccess(level.isClientSide);
         }
      }

      return super.useOn(pContext);
   }

   /** tooltip 内联(1.2.0 删除 TooltipEvents,对齐官方在物品类自绘) */
   public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(
         Component.translatable("item.kaleidoscope_chinesefood.firecracker.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
      );
   }
}
