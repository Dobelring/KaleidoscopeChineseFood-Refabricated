package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class MooncakeBlock extends Block {
   public static final IntegerProperty STACK_COUNT = IntegerProperty.create("stack_count", 0, 4);
   // 1.2.0:碰撞/交互箱 2..14 → 1..15
   private static final VoxelShape MOONCAKE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);

   public MooncakeBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STACK_COUNT, 0));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{STACK_COUNT});
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      @NotNull ItemStack stack,
      @NotNull BlockState state,
      @NotNull Level level,
      @NotNull BlockPos pos,
      @NotNull Player player,
      @NotNull InteractionHand hand,
      @NotNull BlockHitResult hit
   ) {
      if (hand != InteractionHand.MAIN_HAND) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      } else {
         // 1.2.0:仅顶面区域响应,其余穿透
         if (!isClickOnCake(pos, hit.getLocation())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         if (stack.is(ModItems.MOONCAKE)) {
            int count = (Integer)state.getValue(STACK_COUNT);
            if (count < 4) {
               if (!player.isCreative()) {
                  stack.shrink(1);
               }

               level.setBlockAndUpdate(pos, (BlockState)state.setValue(STACK_COUNT, count + 1));
               level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
               return ItemInteractionResult.SUCCESS;
            }
         }

         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @NotNull
   protected InteractionResult useWithoutItem(
      @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit
   ) {
      // 1.2.0:仅顶面区域响应,其余穿透;取回判据副手空 → 主手空
      if (!isClickOnCake(pos, hit.getLocation())) {
         return InteractionResult.PASS;
      }

      if (!player.getMainHandItem().isEmpty()) {
         return InteractionResult.PASS;
      }

      if (!level.isClientSide) {
         int currentStack = (Integer)state.getValue(STACK_COUNT);
         ItemStack mooncake = new ItemStack(ModItems.MOONCAKE);
         if (currentStack > 0) {
            level.setBlock(pos, (BlockState)state.setValue(STACK_COUNT, currentStack - 1), 3);
         } else {
            level.removeBlock(pos, false);
         }

         level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.9F, 1.0F);
         if (!player.getInventory().add(mooncake)) {
            player.drop(mooncake, false);
         }
      }

      return InteractionResult.SUCCESS;
   }

   /** 1.2.0:点击是否落在月饼顶面区域(0.124~0.876 × 高 0~0.0635)。 */
   public static boolean isClickOnCake(BlockPos pos, Vec3 clickLoc) {
      double x = clickLoc.x - (double)pos.getX();
      double y = clickLoc.y - (double)pos.getY();
      double z = clickLoc.z - (double)pos.getZ();
      return x >= 0.124 && x <= 0.876 && y >= -0.001 && y <= 0.0635 && z >= 0.124 && z <= 0.876;
   }

   @NotNull
   public VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
      return MOONCAKE_SHAPE;
   }

   public void appendHoverText(ItemStack stack, @NotNull Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(
         Component.translatable("block.kaleidoscope_chinesefood.mooncake.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
      );
   }

   public List<ItemStack> getDrops(@NotNull BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder paramsBuilder) {
      List<ItemStack> drops = Lists.newArrayList();
      int realAmount = (Integer)state.getValue(STACK_COUNT) + 1;
      drops.add(new ItemStack((ItemLike)ModItems.MOONCAKE, realAmount));
      return drops;
   }

   @NotNull
   public ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
      return new ItemStack((ItemLike)ModItems.MOONCAKE);
   }

   @NotNull
   public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return MOONCAKE_SHAPE;
   }

   @NotNull
   public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return this.getShape(state, level, pos, context);
   }
}
