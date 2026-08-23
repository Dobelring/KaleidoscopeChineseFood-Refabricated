package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class MooncakeBlock extends Block {
   public static final IntegerProperty STACK_COUNT = IntegerProperty.create("stack_count", 0, 4);
   private static final VoxelShape MOONCAKE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0);

   public MooncakeBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STACK_COUNT, 0));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{STACK_COUNT});
   }

   public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
      ItemStack heldItem = context.getItemInHand();
      return heldItem.getItem() == ModItems.MOONCAKE && (Integer)state.getValue(STACK_COUNT) < 4;
   }

   public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
      BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
      return clickedState.is(this)
         ? (BlockState)clickedState.setValue(STACK_COUNT, (Integer)clickedState.getValue(STACK_COUNT) + 1)
         : Objects.requireNonNull(super.getStateForPlacement(context));
   }

   @NotNull
   protected InteractionResult useWithoutItem(
      @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit
   ) {
      if (player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()) {
         if (!level.isClientSide()) {
            int currentStack = (Integer)state.getValue(STACK_COUNT);
            ItemStack mooncake = new ItemStack(ModItems.MOONCAKE);
            if (currentStack > 0) {
               level.setBlock(pos, (BlockState)state.setValue(STACK_COUNT, currentStack - 1), 3);
            } else {
               level.removeBlock(pos, false);
            }

            if (!player.getInventory().add(mooncake)) {
               player.drop(mooncake, false);
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public void spawnAfterBreak(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ItemStack tool, boolean dropExperience) {
      super.spawnAfterBreak(state, level, pos, tool, dropExperience);
      int count = (Integer)state.getValue(STACK_COUNT) + 1;
      popResource(level, pos, new ItemStack(ModItems.MOONCAKE, count));
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
