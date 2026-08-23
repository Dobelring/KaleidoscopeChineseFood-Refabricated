package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class SaltBlock extends Block {
   public static final IntegerProperty STACK_COUNT = IntegerProperty.create("stack_count", 0, 3);
   public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape SHAPE_SINGLE = Block.box(6.0, 0.0, 6.0, 10.0, 7.0, 10.0);
   private static final VoxelShape SHAPE_DOUBLE_NS = Block.box(3.0, 0.0, 6.0, 13.0, 7.0, 10.0);
   private static final VoxelShape SHAPE_DOUBLE_EW = Block.box(6.0, 0.0, 3.0, 10.0, 7.0, 13.0);
   private static final VoxelShape SHAPE_TRIPLE_NORTH = Shapes.or(Block.box(3.0, 0.0, 3.0, 13.0, 7.0, 7.0), Block.box(6.0, 0.0, 7.0, 10.0, 7.0, 13.0));
   private static final VoxelShape SHAPE_TRIPLE_EAST = Shapes.or(Block.box(9.0, 0.0, 3.0, 13.0, 7.0, 13.0), Block.box(3.0, 0.0, 6.0, 9.0, 7.0, 10.0));
   private static final VoxelShape SHAPE_TRIPLE_SOUTH = Shapes.or(Block.box(3.0, 0.0, 9.0, 13.0, 7.0, 13.0), Block.box(6.0, 0.0, 3.0, 10.0, 7.0, 9.0));
   private static final VoxelShape SHAPE_TRIPLE_WEST = Shapes.or(Block.box(3.0, 0.0, 3.0, 7.0, 7.0, 13.0), Block.box(7.0, 0.0, 6.0, 13.0, 7.0, 10.0));
   private static final VoxelShape SHAPE_FULL = Block.box(3.0, 0.0, 3.0, 13.0, 7.0, 13.0);

   public SaltBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(STACK_COUNT, 0)).setValue(FACING, Direction.SOUTH));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{STACK_COUNT, FACING});
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
      return context.getItemInHand().is(ModItems.SALT) && (Integer)state.getValue(STACK_COUNT) < 3;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
      return clickedState.is(this)
         ? (BlockState)clickedState.setValue(STACK_COUNT, (Integer)clickedState.getValue(STACK_COUNT) + 1)
         : (BlockState)super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (!player.getMainHandItem().isEmpty()) {
         return InteractionResult.PASS;
      } else {
         if (!level.isClientSide()) {
            int currentStack = (Integer)state.getValue(STACK_COUNT);
            ItemStack salt = new ItemStack(ModItems.SALT);
            if (currentStack > 0) {
               level.setBlock(pos, (BlockState)state.setValue(STACK_COUNT, currentStack - 1), 3);
            } else {
               level.removeBlock(pos, false);
            }

            if (!player.getInventory().add(salt)) {
               player.drop(salt, false);
            }
         }

         return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
      }
   }

   public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack tool, boolean dropExperience) {
      super.spawnAfterBreak(state, level, pos, tool, dropExperience);
      popResource(level, pos, new ItemStack(ModItems.SALT, (Integer)state.getValue(STACK_COUNT) + 1));
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      int stack = (Integer)state.getValue(STACK_COUNT);
      Direction facing = (Direction)state.getValue(FACING);

      return switch (stack) {
         case 0 -> SHAPE_SINGLE;
         case 1 -> facing.getAxis() == Axis.X ? SHAPE_DOUBLE_EW : SHAPE_DOUBLE_NS;
         case 2 -> {
            switch (facing) {
               case NORTH:
                  yield SHAPE_TRIPLE_NORTH;
               case EAST:
                  yield SHAPE_TRIPLE_EAST;
               case SOUTH:
                  yield SHAPE_TRIPLE_SOUTH;
               case WEST:
                  yield SHAPE_TRIPLE_WEST;
               default:
                  yield SHAPE_TRIPLE_SOUTH;
            }
         }
         case 3 -> SHAPE_FULL;
         default -> Shapes.empty();
      };
   }

   @NotNull
   public BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return this.getShape(state, level, pos, context);
   }
}
