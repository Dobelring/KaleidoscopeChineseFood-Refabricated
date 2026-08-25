package com.bmt.kaleidoscope_chinesefood.item;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MooncakeMoldItem extends Item {
   private static final int CRAFTING_TIME = 20;
   public static final String STUFFED_DOUGH_FOOD_ID = "kaleidoscope_cookery:stuffed_dough_food";

   public MooncakeMoldItem(Properties properties) {
      super(properties);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
      InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
      ItemStack doughStack = player.getItemInHand(otherHand);
      if (this.isStuffedDoughFood(doughStack)) {
         player.startUsingItem(hand);
         return InteractionResultHolder.consume(player.getItemInHand(hand));
      } else {
         return InteractionResultHolder.pass(player.getItemInHand(hand));
      }
   }

   @NotNull
   public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
      if (livingEntity instanceof Player player) {
         InteractionHand usedHand = player.getUsedItemHand();
         InteractionHand otherHand = usedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
         ItemStack doughStack = player.getItemInHand(otherHand);
         if (this.isStuffedDoughFood(doughStack)) {
            doughStack.shrink(1);
            ItemStack rawMooncake = new ItemStack(ModItems.RAW_MOONCAKE);
            if (!player.getInventory().add(rawMooncake)) {
               player.drop(rawMooncake, false);
            }

            level.playSound(null, player.blockPosition(), SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1.0F, 1.2F);
         }

         return stack;
      } else {
         return stack;
      }
   }

   public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
      return 20;
   }

   @NotNull
   public UseAnim getUseAnimation(@NotNull ItemStack stack) {
      return UseAnim.BOW;
   }

   private boolean isStuffedDoughFood(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem().builtInRegistryHolder().key().location().toString().equals("kaleidoscope_cookery:stuffed_dough_food");
   }
}
