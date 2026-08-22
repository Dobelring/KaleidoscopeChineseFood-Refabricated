package com.bmt.kaleidoscope_chinesefood.item;

import com.bmt.kaleidoscope_chinesefood.block.MooncakeBlock;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MooncakeItem extends BlockItem {
   public MooncakeItem(Block block, Properties properties) {
      super(block, properties);
   }

   @NotNull
   public InteractionResult place(@NotNull BlockPlaceContext context) {
      Level level = context.getLevel();
      BlockPos clickedPos = context.getClickedPos();
      BlockState clickedState = level.getBlockState(clickedPos);
      if (clickedState.getBlock() instanceof MooncakeBlock) {
         int currentStack = (Integer)clickedState.getValue(MooncakeBlock.STACK_COUNT);
         if (currentStack >= 4) {
            return InteractionResult.FAIL;
         } else {
            if (!level.isClientSide) {
               level.setBlock(clickedPos, (BlockState)clickedState.setValue(MooncakeBlock.STACK_COUNT, currentStack + 1), 3);
               if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
                  context.getItemInHand().shrink(1);
               }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
         }
      } else {
         return context.getPlayer() != null && context.getPlayer().isCrouching() ? super.place(context) : InteractionResult.PASS;
      }
   }

   @NotNull
   public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
      ItemStack result = super.finishUsingItem(stack, level, entity);
      if (!level.isClientSide && entity instanceof Player player) {
         long dayTime = level.getDayTime() % 24000L;
         if (dayTime >= 0L && dayTime < 12000L) {
            this.applyRandomPositiveEffect(player, level);
         } else {
            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 0));
         }
      }

      return result;
   }

   private void applyRandomPositiveEffect(Player player, Level level) {
      RegistryLookup<MobEffect> effectRegistry = level.registryAccess().lookupOrThrow(Registries.MOB_EFFECT);
      List<Holder<MobEffect>> positiveEffectHolders = new ArrayList<>();
      effectRegistry.listElements().forEach(effectHolder -> {
         MobEffect effect = (MobEffect)effectHolder.value();
         if (effect.isBeneficial()) {
            positiveEffectHolders.add(effectHolder);
         }
      });
      if (!positiveEffectHolders.isEmpty()) {
         Holder<MobEffect> randomEffectHolder = positiveEffectHolders.get(level.getRandom().nextInt(positiveEffectHolders.size()));
         int duration = 300 + level.getRandom().nextInt(201);
         player.addEffect(new MobEffectInstance(randomEffectHolder, duration, 0));
      }
   }
}
