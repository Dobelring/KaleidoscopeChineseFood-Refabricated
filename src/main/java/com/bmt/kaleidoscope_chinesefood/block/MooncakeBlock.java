package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import net.minecraft.util.Prediction;

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

   public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
      return Objects.requireNonNull(super.getStateForPlacement(context));
   }

   /**
    * 手持月饼右键叠放。
    * <p>
    * 叠放只走这一条路径：{@code canBeReplaced} 与 {@code getStateForPlacement} 里的叠放逻辑已移除，
    * 否则右键会同时命中"替换放置"和"物品交互"两条路径，计数出错。
    */
   @NotNull
   protected InteractionResult useItemOn(
      @NotNull ItemStack stack,
      @NotNull BlockState state,
      @NotNull Level level,
      @NotNull BlockPos pos,
      @NotNull Player player,
      @NotNull InteractionHand hand,
      @NotNull BlockHitResult hit
   ) {
      if (hand != InteractionHand.MAIN_HAND) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      }

      if (stack.is(ModItems.MOONCAKE)) {
         int count = (Integer)state.getValue(STACK_COUNT);
         if (count < 4) {
            if (!player.isCreative()) {
               stack.shrink(1);
            }

            level.setBlockAndUpdate(pos, (BlockState)state.setValue(STACK_COUNT, count + 1));
            if (!level.isClientSide()) {
               level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.TRY_WITH_EMPTY_HAND;
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

            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.9F, 1.0F);
            if (!player.getInventory().add(mooncake)) {
               player.drop(mooncake, false, Prediction.PREDICTED);
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   /**
    * 掉落按堆叠数给足。
    * <p>
    * 26.x 的 {@code dropResources} 先取 {@code getDrops} 再调 {@code spawnAfterBreak}，
    * 覆盖本方法可以同时覆盖玩家破坏、爆炸、活塞等全部掉落来源。
    */
   @Override
   protected List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder params) {
      return List.of(new ItemStack(ModItems.MOONCAKE, (Integer)state.getValue(STACK_COUNT) + 1));
   }

   @Override
   protected ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean includeData) {
      return new ItemStack(ModItems.MOONCAKE);
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
