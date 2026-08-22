package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.FirecrackerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FirecrackerBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = DirectionProperty.create("facing", Plane.HORIZONTAL);
   protected static final VoxelShape SHAPE = Block.box(6.0, 0.0, 6.0, 10.0, 8.0, 10.0);
   public static final MapCodec<FirecrackerBlock> CODEC = simpleCodec(FirecrackerBlock::new);

   public FirecrackerBlock(Properties pProperties) {
      super(pProperties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING});
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (stack.is(Items.FLINT_AND_STEEL)) {
         level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!level.isClientSide && level.getBlockEntity(pos) instanceof FirecrackerBlockEntity firecrackerBE) {
            firecrackerBE.ignite();
         }

         if (!player.getAbilities().instabuild) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
         }

         return ItemInteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hit);
      }
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return createTickerHelper(pBlockEntityType, ModBlockEntities.FIRECRACKER, FirecrackerBlockEntity::tick);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new FirecrackerBlockEntity(pPos, pState);
   }

   @NotNull
   public BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }
}
