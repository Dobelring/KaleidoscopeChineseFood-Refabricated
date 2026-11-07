package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.FirecrackerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.entity.FirecrackerEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
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

   /** 1.2.0 新功能:发射器射出鞭炮投掷物。 */
   public static void registerDispenserBehavior() {
      DispenseItemBehavior behavior = new DefaultDispenseItemBehavior() {
         @NotNull
         @Override
         protected ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
            ServerLevel level = source.level();
            Position position = DispenserBlock.getDispensePosition(source);
            Direction direction = source.state().getValue(DispenserBlock.FACING);
            FirecrackerEntity firecracker = Util.make(new FirecrackerEntity(level, position.x(), position.y(), position.z()), e -> e.setItem(stack));
            firecracker.shoot(direction.getStepX(), direction.getStepY(), direction.getStepZ(), 1.1F, 6.0F);
            level.addFreshEntity(firecracker);
            stack.shrink(1);
            return stack;
         }
      };
      DispenserBlock.registerBehavior(ModItems.FIRECRACKER, behavior);
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

   @NotNull
   public ItemStack getCloneItemStack(@NotNull LevelReader pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState) {
      return new ItemStack((ItemLike)ModItems.FIRECRACKER);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      // 官方 1.1.16：火焰弹也可点燃（消耗 1 个，创造免耗，音效用火弹），打火石照旧扣耐久
      if (!stack.is(Items.FLINT_AND_STEEL) && !stack.is(Items.FIRE_CHARGE)) {
         return super.useItemOn(stack, state, level, pos, player, hand, hit);
      } else {
         level.playSound(player, pos, stack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
         if (!level.isClientSide && level.getBlockEntity(pos) instanceof FirecrackerBlockEntity firecrackerBE) {
            firecrackerBE.ignite();
         }

         if (stack.is(Items.FIRE_CHARGE)) {
            if (!player.getAbilities().instabuild) {
               stack.shrink(1);
            }
         } else if (!player.getAbilities().instabuild) {
            EquipmentSlot slot = hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            stack.hurtAndBreak(1, player, slot);
         }

         return ItemInteractionResult.sidedSuccess(level.isClientSide);
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
