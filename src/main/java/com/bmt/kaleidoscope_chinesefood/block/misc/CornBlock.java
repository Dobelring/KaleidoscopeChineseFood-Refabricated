package com.bmt.kaleidoscope_chinesefood.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CornBlock extends Block {
   public static final BooleanProperty IS_HEAD = BooleanProperty.create("is_head");
   public static final BooleanProperty SHEARED = BooleanProperty.create("sheared");
   private static final VoxelShape AABB_HEAD = Block.box(4.0, 2.0, 4.0, 12.0, 16.0, 12.0);
   private static final VoxelShape AABB_BODY = Block.box(3.5, 0.0, 3.5, 12.5, 16.0, 12.5);

   public CornBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(IS_HEAD, true)).setValue(SHEARED, false));
   }

   public InteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
   ) {
      return InteractionResult.PASS;
   }

   public BlockState updateShape(BlockState state, LevelReader levelAccessor, ScheduledTickAccess tickAccess, BlockPos currentPos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
      if (direction == Direction.DOWN.getOpposite() && !state.canSurvive(levelAccessor, currentPos)) {
         tickAccess.scheduleTick(currentPos, this, 1);
      }

      return direction == Direction.DOWN
         ? (BlockState)state.setValue(IS_HEAD, !neighborState.is(this))
         : super.updateShape(state, levelAccessor, tickAccess, currentPos, direction, neighborPos, neighborState, random);
   }

   public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
      BlockPos belowPos = pos.relative(Direction.DOWN.getOpposite());
      BlockState belowState = levelReader.getBlockState(belowPos);
      return belowState.is(this) || belowState.isFaceSturdy(levelReader, belowPos, Direction.DOWN);
   }

   public void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(serverLevel, pos)) {
         serverLevel.destroyBlock(pos, true);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{IS_HEAD, SHEARED});
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
      return state.getValue(IS_HEAD) ? AABB_HEAD : AABB_BODY;
   }

   // 用户反馈玉米串串放置/破坏音效偏小：音效类型仍为 GRASS（与原版一致），音量 1.0 提至 2.0
   protected SoundType getSoundType(BlockState state) {
      return new SoundType(
         2.0F,
         1.0F,
         SoundType.GRASS.getBreakSound(),
         SoundType.GRASS.getStepSound(),
         SoundType.GRASS.getPlaceSound(),
         SoundType.GRASS.getHitSound(),
         SoundType.GRASS.getFallSound()
      );
   }
}
