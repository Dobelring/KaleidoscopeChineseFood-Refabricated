package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.api.blockentity.IPickleJar;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PickleJarBlock extends BaseEntityBlock {
   public static final BooleanProperty OPEN = BooleanProperty.create("open");
   public static final BooleanProperty FERMENTING = BooleanProperty.create("fermenting");
   public static final BooleanProperty DONE = BooleanProperty.create("done");
   private static final VoxelShape JAR_BODY = Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0);
   private static final VoxelShape JAR_TOP = Block.box(5.0, 10.0, 5.0, 11.0, 13.0, 11.0);
   private static final VoxelShape SHAPE = Shapes.or(JAR_BODY, JAR_TOP);
   private static final MapCodec<PickleJarBlock> CODEC = simpleCodec(PickleJarBlock::new);

   public PickleJarBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(OPEN, false)).setValue(FERMENTING, false))
            .setValue(DONE, false)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         IPickleJar jar = this.getPickleJar(level, pos);
         if (jar == null) {
            return InteractionResult.PASS;
         } else if ((Boolean)state.getValue(FERMENTING)) {
            int remainingSeconds = Math.max(0, (jar.getMaxProgress() - jar.getProgress()) / 20);
            player.sendOverlayMessage(Component.translatable("message.kaleidoscope_chinesefood.fermenting_remaining", new Object[]{remainingSeconds}));
            return InteractionResult.CONSUME;
         } else if (player.isShiftKeyDown()) {
            boolean isOpen = (Boolean)state.getValue(OPEN);
            if (isOpen) {
               boolean hasValidRecipe = jar.tryStartFermenting(level);
               BlockState newState = (BlockState)((BlockState)((BlockState)state.setValue(OPEN, false)).setValue(FERMENTING, hasValidRecipe))
                  .setValue(DONE, false);
               level.setBlock(pos, newState, 3);
               if (hasValidRecipe) {
                  player.sendOverlayMessage(Component.translatable("message.kaleidoscope_chinesefood.start_fermenting"));
               }
            } else {
               jar.resetProgress();
               BlockState newState = (BlockState)((BlockState)((BlockState)state.setValue(OPEN, true)).setValue(FERMENTING, false)).setValue(DONE, false);
               level.setBlock(pos, newState, 3);
            }

            level.playSound(null, pos, isOpen ? SoundEvents.BARREL_CLOSE : SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.CONSUME;
         } else if ((Boolean)state.getValue(OPEN)) {
            ItemStack held = player.getMainHandItem();
            if (held.isEmpty()) {
               jar.extractItem(player);
            } else {
               jar.insertItem(held, player);
            }

            level.playSound(null, pos, held.isEmpty() ? SoundEvents.ITEM_FRAME_REMOVE_ITEM : SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8F, 1.1F);
            return InteractionResult.CONSUME;
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   @Override
   protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean moved) {
      if (level.getBlockEntity(pos) instanceof PickleJarBlockEntity be) {
         for (int i = 0; i < 4; i++) {
            ItemStack stack = be.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
               Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
         }

         level.updateNeighbourForOutputSignal(pos, this);
      }
   }

   @Nullable
   private IPickleJar getPickleJar(Level level, BlockPos pos) {
      BlockEntity be = level.getBlockEntity(pos);
      return be instanceof IPickleJar ? (IPickleJar)be : null;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new PickleJarBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return createTickerHelper(type, ModBlockEntities.PICKLE_JAR, PickleJarBlockEntity::tick);
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{OPEN, FERMENTING, DONE});
   }

   public boolean canBeReplaced(BlockState state, Fluid fluid) {
      return false;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }
}
