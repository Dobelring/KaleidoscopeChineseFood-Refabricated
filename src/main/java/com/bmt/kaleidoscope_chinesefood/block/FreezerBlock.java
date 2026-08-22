package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.FreezerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FreezerBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty TOP = BooleanProperty.create("top");
   public static final BooleanProperty UPPER_OPEN = BooleanProperty.create("upper_open");
   public static final BooleanProperty LOWER_OPEN = BooleanProperty.create("lower_open");
   private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   public static final MapCodec<FreezerBlock> CODEC = simpleCodec(FreezerBlock::new);

   public FreezerBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TOP, false))
               .setValue(UPPER_OPEN, false))
            .setValue(LOWER_OPEN, false)
      );
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, TOP, UPPER_OPEN, LOWER_OPEN});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      BlockPos abovePos = pos.above();
      return !level.getBlockState(abovePos).canBeReplaced(context)
         ? null
         : (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())).setValue(TOP, false);
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      BlockPos abovePos = pos.above();
      BlockState aboveState = (BlockState)state.setValue(TOP, true);
      level.setBlock(abovePos, aboveState, 3);
   }

   @NotNull
   public BlockState updateShape(
      BlockState state,
      @NotNull Direction direction,
      @NotNull BlockState neighborState,
      @NotNull LevelAccessor level,
      @NotNull BlockPos pos,
      @NotNull BlockPos neighborPos
   ) {
      return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         BlockPos otherPos = state.getValue(TOP) ? pos.below() : pos.above();
         if (level.getBlockState(otherPos).getBlock() instanceof FreezerBlock) {
            level.destroyBlock(otherPos, false);
         }

         if (level.getBlockEntity(pos) instanceof FreezerBlockEntity freezerBE) {
            freezerBE.drops();
         }
      }

      super.onRemove(state, level, pos, newState, isMoving);
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
      this.tryOpenMenu(level, pos, player);
      return ItemInteractionResult.sidedSuccess(level.isClientSide);
   }

   @NotNull
   protected InteractionResult useWithoutItem(
      @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit
   ) {
      this.tryOpenMenu(level, pos, player);
      return InteractionResult.sidedSuccess(level.isClientSide);
   }

   private void tryOpenMenu(Level level, BlockPos pos, Player player) {
      if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && level.getBlockEntity(pos) instanceof FreezerBlockEntity freezerBE) {
         serverPlayer.openMenu(freezerBE);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new FreezerBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.FREEZER, FreezerBlockEntity::serverTick);
   }

   @NotNull
   public BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      if (!(Boolean)state.getValue(TOP)) {
         BlockPos abovePos = pos.above();
         return level.getBlockState(abovePos).canBeReplaced();
      } else {
         BlockPos belowPos = pos.below();
         BlockState belowState = level.getBlockState(belowPos);
         return belowState.getBlock() instanceof FreezerBlock && !(Boolean)belowState.getValue(TOP);
      }
   }
}
