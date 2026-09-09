package com.bmt.kaleidoscope_chinesefood.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FuCharacterBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<FuCharacterBlock> CODEC = simpleCodec(FuCharacterBlock::new);
   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final EnumProperty<FuCharacterBlock.FuState> FU_STATE = EnumProperty.create("fu_state", FuCharacterBlock.FuState.class);
   private static final VoxelShape SHAPE_NORTH_FULL = Block.box(0.0, 0.0, 15.9, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_NORTH_LEFT = Block.box(13.0, 0.0, 15.9, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_NORTH_RIGHT = Block.box(0.0, 0.0, 15.9, 3.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_NORTH_BOTTOM = Block.box(0.0, 0.0, 15.9, 16.0, 3.0, 16.0);
   private static final VoxelShape SHAPE_NORTH_TOP = Block.box(0.0, 13.0, 15.9, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_SOUTH_FULL = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 0.1);
   private static final VoxelShape SHAPE_SOUTH_LEFT = Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 0.1);
   private static final VoxelShape SHAPE_SOUTH_RIGHT = Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 0.1);
   private static final VoxelShape SHAPE_SOUTH_BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 0.1);
   private static final VoxelShape SHAPE_SOUTH_TOP = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 0.1);
   private static final VoxelShape SHAPE_EAST_FULL = Block.box(0.0, 0.0, 0.0, 0.1, 16.0, 16.0);
   private static final VoxelShape SHAPE_EAST_LEFT = Block.box(0.0, 0.0, 13.0, 0.1, 16.0, 16.0);
   private static final VoxelShape SHAPE_EAST_RIGHT = Block.box(0.0, 0.0, 0.0, 0.1, 16.0, 3.0);
   private static final VoxelShape SHAPE_EAST_BOTTOM = Block.box(0.0, 0.0, 0.0, 0.1, 3.0, 16.0);
   private static final VoxelShape SHAPE_EAST_TOP = Block.box(0.0, 13.0, 0.0, 0.1, 16.0, 16.0);
   private static final VoxelShape SHAPE_WEST_FULL = Block.box(15.9, 0.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_WEST_LEFT = Block.box(15.9, 0.0, 0.0, 16.0, 16.0, 3.0);
   private static final VoxelShape SHAPE_WEST_RIGHT = Block.box(15.9, 0.0, 13.0, 16.0, 16.0, 16.0);
   private static final VoxelShape SHAPE_WEST_BOTTOM = Block.box(15.9, 0.0, 0.0, 16.0, 3.0, 16.0);
   private static final VoxelShape SHAPE_WEST_TOP = Block.box(15.9, 13.0, 0.0, 16.0, 16.0, 16.0);

   public FuCharacterBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false))
            .setValue(FU_STATE, FuCharacterBlock.FuState.NORMAL)
      );
   }

   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, WATERLOGGED, FU_STATE});
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return getVoxelShape((Direction)state.getValue(FACING), (FuCharacterBlock.FuState)state.getValue(FU_STATE));
   }

   private static VoxelShape getVoxelShape(Direction facing, FuCharacterBlock.FuState state) {
      return switch (facing) {
         case NORTH -> {
            switch (state) {
               case NORMAL:
               case DOOR_BACK:
                  yield SHAPE_NORTH_FULL;
               case LEFT_OPEN:
                  yield SHAPE_NORTH_LEFT;
               case RIGHT_OPEN:
                  yield SHAPE_NORTH_RIGHT;
               case TRAPDOOR_BOTTOM_OPEN:
                  yield SHAPE_NORTH_BOTTOM;
               case TRAPDOOR_TOP_OPEN:
                  yield SHAPE_NORTH_TOP;
               default:
                  throw new MatchException(null, null);
            }
         }
         case SOUTH -> {
            switch (state) {
               case NORMAL:
               case DOOR_BACK:
                  yield SHAPE_SOUTH_FULL;
               case LEFT_OPEN:
                  yield SHAPE_SOUTH_LEFT;
               case RIGHT_OPEN:
                  yield SHAPE_SOUTH_RIGHT;
               case TRAPDOOR_BOTTOM_OPEN:
                  yield SHAPE_SOUTH_BOTTOM;
               case TRAPDOOR_TOP_OPEN:
                  yield SHAPE_SOUTH_TOP;
               default:
                  throw new MatchException(null, null);
            }
         }
         case EAST -> {
            switch (state) {
               case NORMAL:
               case DOOR_BACK:
                  yield SHAPE_EAST_FULL;
               case LEFT_OPEN:
                  yield SHAPE_EAST_LEFT;
               case RIGHT_OPEN:
                  yield SHAPE_EAST_RIGHT;
               case TRAPDOOR_BOTTOM_OPEN:
                  yield SHAPE_EAST_BOTTOM;
               case TRAPDOOR_TOP_OPEN:
                  yield SHAPE_EAST_TOP;
               default:
                  throw new MatchException(null, null);
            }
         }
         case WEST -> {
            switch (state) {
               case NORMAL:
               case DOOR_BACK:
                  yield SHAPE_WEST_FULL;
               case LEFT_OPEN:
                  yield SHAPE_WEST_LEFT;
               case RIGHT_OPEN:
                  yield SHAPE_WEST_RIGHT;
               case TRAPDOOR_BOTTOM_OPEN:
                  yield SHAPE_WEST_BOTTOM;
               case TRAPDOOR_TOP_OPEN:
                  yield SHAPE_WEST_TOP;
               default:
                  throw new MatchException(null, null);
            }
         }
         default -> SHAPE_NORTH_FULL;
      };
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   protected InteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      this.handleInteraction(state, level, pos, player, hand);
      return InteractionResult.SUCCESS;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      return this.handleInteraction(state, level, pos, player, InteractionHand.MAIN_HAND);
   }

   private InteractionResult handleInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         Direction facing = (Direction)state.getValue(FACING);
         BlockPos behindPos = pos.relative(facing.getOpposite());
         BlockState behindState = level.getBlockState(behindPos);
         if (behindState.hasProperty(BlockStateProperties.OPEN)) {
            boolean isOpen = (Boolean)behindState.getValue(BlockStateProperties.OPEN);
            level.setBlock(behindPos, (BlockState)behindState.setValue(BlockStateProperties.OPEN, !isOpen), 3);
            this.playDoorSound(level, behindPos, behindState.getBlock(), isOpen);
            player.swing(hand);
            return InteractionResult.CONSUME;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   private void playDoorSound(Level level, BlockPos pos, Block block, boolean isOpen) {
      SoundEvent sound;
      if (block instanceof DoorBlock) {
         sound = isOpen ? SoundEvents.WOODEN_DOOR_CLOSE : SoundEvents.WOODEN_DOOR_OPEN;
      } else {
         if (!(block instanceof TrapDoorBlock)) {
            return;
         }

         sound = isOpen ? SoundEvents.WOODEN_TRAPDOOR_CLOSE : SoundEvents.WOODEN_TRAPDOOR_OPEN;
      }

      level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction clickedFace = context.getClickedFace();
      if (!clickedFace.getAxis().isHorizontal()) {
         return null;
      } else {
         BlockPos pos = context.getClickedPos();
         Level level = context.getLevel();
         FluidState fluidState = level.getFluidState(pos);
         BlockState state = (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, clickedFace))
            .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
         state = this.updateFuModelState(state, level, pos, clickedFace);
         return state.canSurvive(level, pos) ? state : null;
      }
   }

   public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      Direction facing = (Direction)state.getValue(FACING);
      if (direction == facing.getOpposite()) {
         if (neighborState.isAir()) {
            return Blocks.AIR.defaultBlockState();
         }

         state = this.updateFuModelState(state, level, pos, facing);
      }

      return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
   }

   private BlockState updateFuModelState(BlockState state, LevelReader level, BlockPos pos, Direction facing) {
      BlockPos behindPos = pos.relative(facing.getOpposite());
      BlockState behindState = level.getBlockState(behindPos);
      if (behindState.getBlock() instanceof DoorBlock doorBlock) {
         boolean isOpen = (Boolean)behindState.getValue(DoorBlock.OPEN);
         DoorHingeSide hinge = (DoorHingeSide)behindState.getValue(DoorBlock.HINGE);
         Direction doorFacing = (Direction)behindState.getValue(DoorBlock.FACING);
         boolean isBack = facing == doorFacing;
         if (isOpen) {
            if (isBack) {
               hinge = hinge == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
            }

            return (BlockState)state.setValue(FU_STATE, hinge == DoorHingeSide.LEFT ? FuCharacterBlock.FuState.LEFT_OPEN : FuCharacterBlock.FuState.RIGHT_OPEN);
         } else {
            return (BlockState)state.setValue(FU_STATE, isBack ? FuCharacterBlock.FuState.DOOR_BACK : FuCharacterBlock.FuState.NORMAL);
         }
      } else if (behindState.getBlock() instanceof TrapDoorBlock trapDoorBlock) {
         boolean isOpen = (Boolean)behindState.getValue(TrapDoorBlock.OPEN);
         Half half = (Half)behindState.getValue(TrapDoorBlock.HALF);
         Direction trapDoorFacing = (Direction)behindState.getValue(TrapDoorBlock.FACING);
         boolean isBack = facing == trapDoorFacing;
         return isOpen
            ? (BlockState)state.setValue(FU_STATE, isBack ? FuCharacterBlock.FuState.DOOR_BACK : FuCharacterBlock.FuState.NORMAL)
            : (BlockState)state.setValue(
               FU_STATE, half == Half.BOTTOM ? FuCharacterBlock.FuState.TRAPDOOR_BOTTOM_OPEN : FuCharacterBlock.FuState.TRAPDOOR_TOP_OPEN
            );
      } else {
         return (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.NORMAL);
      }
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      BlockPos wallPos = pos.relative(facing.getOpposite());
      BlockState wallState = level.getBlockState(wallPos);
      return wallState.getBlock() instanceof DoorBlock || wallState.getBlock() instanceof TrapDoorBlock || wallState.isFaceSturdy(level, wallPos, facing);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public static enum FuState implements StringRepresentable {
      NORMAL("normal"),
      LEFT_OPEN("left_open"),
      RIGHT_OPEN("right_open"),
      DOOR_BACK("door_back"),
      TRAPDOOR_BOTTOM_OPEN("trapdoor_bottom_open"),
      TRAPDOOR_TOP_OPEN("trapdoor_top_open");

      private final String name;

      private FuState(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
