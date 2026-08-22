package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class KongmingLanternBlock extends Block {
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

   public KongmingLanternBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   public static void registerDispenserBehavior(Item item) {
      DispenserBlock.registerBehavior(item, new DispenseItemBehavior() {
         @NotNull
         public ItemStack dispense(@NotNull BlockSource source, @NotNull ItemStack stack) {
            Level level = source.level();
            Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
            BlockPos pos = source.pos().relative(direction);
            KongmingLanternEntity lantern = (KongmingLanternEntity)(ModEntities.KONGMING_LANTERN).create(level);
            if (lantern != null) {
               lantern.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
               lantern.setDeltaMovement(direction.getStepX() * 0.3, 0.1, direction.getStepZ() * 0.3);
               level.addFreshEntity(lantern);
               level.levelEvent(null, 1009, pos, 0);
               stack.shrink(1);
            }

            return stack;
         }
      });
   }

   @NotNull
   public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
      return SHAPE;
   }

   public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
      return super.getStateForPlacement(context);
   }

   @NotNull
   public BlockState updateShape(
      @NotNull BlockState state,
      @NotNull Direction direction,
      @NotNull BlockState neighborState,
      @NotNull LevelAccessor level,
      @NotNull BlockPos pos,
      @NotNull BlockPos neighborPos
   ) {
      return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
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
      if (level.isClientSide) {
         return ItemInteractionResult.SUCCESS;
      } else if (stack.getItem() instanceof FlintAndSteelItem) {
         KongmingLanternEntity lantern = (KongmingLanternEntity)(ModEntities.KONGMING_LANTERN).create(level);
         if (lantern != null) {
            lantern.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            level.addFreshEntity(lantern);
            level.removeBlock(pos, false);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            level.levelEvent(null, 1009, pos, 0);
         }

         return ItemInteractionResult.CONSUME;
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(
         Component.translatable("block.kaleidoscope_chinesefood.kongming_lantern.desc")
            .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
   }
}
