package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.BowlStackBlockEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BowlStackBlock extends BaseEntityBlock {
   public static final IntegerProperty BOWL_COUNT = IntegerProperty.create("bowl_count", 0, 3);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final BooleanProperty IS_WOODEN = BooleanProperty.create("is_wooden");
   private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(2.0, 0.0, 1.0, 14.0, 5.0, 15.0);
   private static final VoxelShape SHAPE_EAST_WEST = Block.box(1.0, 0.0, 2.0, 15.0, 5.0, 14.0);
   private static final SoundEvent PLACE_BOWL_SOUND = SoundEvents.ITEM_FRAME_ADD_ITEM;
   private static final SoundEvent TAKE_BOWL_SOUND = SoundEvents.ITEM_FRAME_REMOVE_ITEM;
   private static final SoundEvent SWITCH_SOUND = SoundEvents.ITEM_FRAME_ROTATE_ITEM;
   private static final MapCodec<BowlStackBlock> CODEC = simpleCodec(BowlStackBlock::new);

   public BowlStackBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(BOWL_COUNT, 0)).setValue(FACING, Direction.NORTH))
            .setValue(IS_WOODEN, false)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new BowlStackBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return createTickerHelper(type, ModBlockEntities.BOWL_STACK, BowlStackBlockEntity::tick);
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
      super.onPlace(state, level, pos, oldState, isMoving);
      if (!level.isClientSide) {
         this.getBlockEntity(level, pos).ifPresent(be -> be.initializeFromBlockState(state));
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      Direction facing = (Direction)state.getValue(FACING);
      return facing != Direction.NORTH && facing != Direction.SOUTH ? SHAPE_EAST_WEST : SHAPE_NORTH_SOUTH;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      return level.isClientSide ? InteractionResult.SUCCESS : this.getBlockEntity(level, pos).map(be -> {
         ItemStack heldItem = player.getMainHandItem();
         int bowlCount = (Integer)state.getValue(BOWL_COUNT);
         if (heldItem.isEmpty() && player.isShiftKeyDown()) {
            level.setBlock(pos, (BlockState)state.setValue(IS_WOODEN, !(Boolean)state.getValue(IS_WOODEN)), 3);
            level.playSound(null, pos, SWITCH_SOUND, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.CONSUME;
         } else if (heldItem.is(Items.BOWL) && bowlCount < 3) {
            if (!player.getAbilities().instabuild) {
               heldItem.shrink(1);
            }

            be.addBowl();
            level.playSound(null, pos, PLACE_BOWL_SOUND, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.CONSUME;
         } else if (heldItem.isEmpty() && bowlCount > 0) {
            ItemStack bowl = be.removeBowl();
            if (!bowl.isEmpty()) {
               player.addItem(bowl);
            }

            level.playSound(null, pos, TAKE_BOWL_SOUND, SoundSource.BLOCKS, 0.8F, 1.0F);
            return InteractionResult.CONSUME;
         } else {
            return InteractionResult.PASS;
         }
      }).orElse(InteractionResult.PASS);
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder paramsBuilder) {
      List<ItemStack> drops = super.getDrops(state, paramsBuilder);
      int bowlCount = (Integer)state.getValue(BOWL_COUNT);
      if (bowlCount > 0) {
         drops.add(new ItemStack(Items.BOWL, bowlCount));
      }

      return drops;
   }

   public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
      if (player.isCreative() && !level.isClientSide) {
         int bowlCount = (Integer)state.getValue(BOWL_COUNT);
         if (bowlCount > 0) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BOWL, bowlCount));
         }
      }

      super.playerDestroy(level, player, pos, state, blockEntity, tool);
   }

   private Optional<BowlStackBlockEntity> getBlockEntity(Level level, BlockPos pos) {
      return level.getBlockEntity(pos, ModBlockEntities.BOWL_STACK);
   }

   public boolean canBeReplaced(BlockState state, Fluid fluid) {
      return false;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{BOWL_COUNT, FACING, IS_WOODEN});
   }

   @NotNull
   public BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }
}
