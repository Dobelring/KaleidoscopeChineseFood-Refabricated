package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CoupletBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<CoupletBlock.CoupletPart> PART = EnumProperty.create("part", CoupletBlock.CoupletPart.class);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final int MAX_COUPLET_HEIGHT = 3;
   public static final int MIN_GAP_BETWEEN_GROUPS = 1;
   public static final int MIN_GAP_BELOW_EXISTING = 3;
   private static final MapCodec<CoupletBlock> CODEC = simpleCodec(CoupletBlock::new);
   private static final VoxelShape[] SHAPES = new VoxelShape[4];

   public CoupletBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
               .setValue(PART, CoupletBlock.CoupletPart.LOWER))
            .setValue(WATERLOGGED, false)
      );
   }

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @NotNull
   public RenderShape getRenderShape(@NotNull BlockState state) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, PART, WATERLOGGED});
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPES[((Direction)state.getValue(FACING)).get2DDataValue()];
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      Direction clickedFace = context.getClickedFace();
      if (!clickedFace.getAxis().isHorizontal()) {
         return null;
      } else {
         FluidState fluidState = level.getFluidState(pos);
         BlockPos belowPos = pos.below();
         BlockState belowState = level.getBlockState(belowPos);
         BlockPos abovePos = pos.above();
         BlockState aboveState = level.getBlockState(abovePos);
         boolean hasBelowCouplet = this.isCoupletBlockWithFacing(belowState, clickedFace);
         boolean hasAboveCouplet = this.isCoupletBlockWithFacing(aboveState, clickedFace);
         if (hasBelowCouplet && hasAboveCouplet) {
            return null;
         } else if (this.isCoupletBlockWithPart(aboveState, clickedFace, CoupletBlock.CoupletPart.LOWER)) {
            BlockPos aboveAbove = abovePos.above();
            if (this.isCoupletBlockWithPart(level.getBlockState(aboveAbove), clickedFace, CoupletBlock.CoupletPart.MIDDLE)) {
               return null;
            } else {
               BlockPos belowGroupTop = this.findNearestCoupletEnd(level, pos, clickedFace, false, true);
               return belowGroupTop != null && pos.getY() < belowGroupTop.getY() + 1 + 1
                  ? null
                  : this.createState((Direction)aboveState.getValue(FACING), CoupletBlock.CoupletPart.LOWER, fluidState);
            }
         } else if (this.isCoupletBlockWithPart(belowState, clickedFace, CoupletBlock.CoupletPart.UPPER)) {
            BlockPos belowBelow = belowPos.below();
            if (this.isCoupletBlockWithPart(level.getBlockState(belowBelow), clickedFace, CoupletBlock.CoupletPart.MIDDLE)) {
               return null;
            } else {
               BlockPos aboveGroupBottom = this.findNearestCoupletEnd(level, pos, clickedFace, true, false);
               return aboveGroupBottom != null && pos.getY() > aboveGroupBottom.getY() - 3
                  ? null
                  : this.createState((Direction)belowState.getValue(FACING), CoupletBlock.CoupletPart.UPPER, fluidState);
            }
         } else {
            BlockPos aboveGroupBottom = this.findNearestCoupletEnd(level, pos, clickedFace, true, false);
            BlockPos belowGroupTop = this.findNearestCoupletEnd(level, pos, clickedFace, false, true);
            if (aboveGroupBottom != null && belowGroupTop != null && aboveGroupBottom.getY() - belowGroupTop.getY() == 2) {
               return null;
            } else if (aboveGroupBottom != null && pos.getY() > aboveGroupBottom.getY() - 3) {
               return null;
            } else if (belowGroupTop != null && pos.getY() < belowGroupTop.getY() + 1 + 1) {
               return null;
            } else {
               boolean hasUpperSpace = pos.getY() < level.getMaxY() - 1 && level.getBlockState(pos.above()).canBeReplaced(context);
               if (hasUpperSpace) {
                  BlockState state = this.createState(clickedFace, CoupletBlock.CoupletPart.LOWER, fluidState);
                  if (state.canSurvive(level, pos)) {
                     return state;
                  }
               }

               return null;
            }
         }
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      if (state.getValue(PART) == CoupletBlock.CoupletPart.LOWER) {
         BlockPos above = pos.above();
         BlockState aboveState = level.getBlockState(above);
         if (this.isCoupletBlockWithPart(aboveState, (Direction)state.getValue(FACING), CoupletBlock.CoupletPart.LOWER)) {
            if (level.getBlockEntity(above) instanceof CoupletBlockEntity oldCoupletBE) {
               String coupletText = oldCoupletBE.getText();
               if (level.getBlockEntity(pos) instanceof CoupletBlockEntity newCoupletBE) {
                  newCoupletBE.setText(coupletText);
                  newCoupletBE.setChanged();
               }
            }

            level.setBlock(above, (BlockState)aboveState.setValue(PART, CoupletBlock.CoupletPart.MIDDLE), 3);
         } else {
            level.setBlock(
               above,
               (BlockState)((BlockState)state.setValue(PART, CoupletBlock.CoupletPart.UPPER))
                  .setValue(WATERLOGGED, level.getFluidState(above).getType() == Fluids.WATER),
               3
            );
         }
      } else if (state.getValue(PART) == CoupletBlock.CoupletPart.UPPER) {
         BlockPos below = pos.below();
         BlockState belowState = level.getBlockState(below);
         if (this.isCoupletBlockWithPart(belowState, (Direction)state.getValue(FACING), CoupletBlock.CoupletPart.UPPER)) {
            BlockPos check = below.below();
            if (!this.isCoupletBlockWithPart(level.getBlockState(check), (Direction)state.getValue(FACING), CoupletBlock.CoupletPart.MIDDLE)) {
               level.setBlock(below, (BlockState)belowState.setValue(PART, CoupletBlock.CoupletPart.MIDDLE), 3);
            }
         }
      }
   }

   @NotNull
   public BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
      if (!level.isClientSide() && !player.isCreative()) {
         this.destroyAndDrop(level, pos);
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction dir, BlockPos nPos, BlockState neighbor, RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      CoupletBlock.CoupletPart part = (CoupletBlock.CoupletPart)state.getValue(PART);
      boolean shouldBreak = false;
      if (dir.getAxis() == Axis.Y) {
         if (part == CoupletBlock.CoupletPart.LOWER && dir == Direction.UP && !neighbor.is(this)) {
            shouldBreak = true;
         }

         if (part == CoupletBlock.CoupletPart.MIDDLE && (!level.getBlockState(pos.below()).is(this) || !level.getBlockState(pos.above()).is(this))) {
            shouldBreak = true;
         }

         if (part == CoupletBlock.CoupletPart.UPPER && dir == Direction.DOWN && !neighbor.is(this)) {
            shouldBreak = true;
         }
      }

      Direction facing = (Direction)state.getValue(FACING);
      if (dir == facing.getOpposite() && !this.canSurvive(state, level, pos)) {
         shouldBreak = true;
      }

      if (shouldBreak) {
         if (!level.isClientSide()) {
            this.destroyAndDrop((Level)level, pos);
         }

         return Blocks.AIR.defaultBlockState();
      } else {
         return super.updateShape(state, level, tickAccess, pos, dir, nPos, neighbor, random);
      }
   }

   private void destroyAndDrop(Level level, BlockPos pos) {
      BlockPos masterPos = this.getMasterBlockPos(level, pos);
      if (masterPos != null) {
         int height = this.getCoupletHeight(level, masterPos);
         popResource(level, masterPos, new ItemStack(this.asItem(), height - 1));
         this.clearCoupletGroup(level, masterPos);
      }
   }

   @Nullable
   private BlockPos findNearestCoupletEnd(Level level, BlockPos pos, Direction facing, boolean searchUp, boolean findTop) {
      int startY = searchUp ? pos.getY() + 1 : pos.getY() - 1;
      int endY = searchUp ? level.getMaxY() : level.getMinY();
      int step = searchUp ? 1 : -1;

      for (int y = startY; searchUp ? y <= endY : y >= endY; y += step) {
         BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());
         BlockState checkState = level.getBlockState(checkPos);
         if (this.isCoupletBlockWithFacing(checkState, facing)) {
            return findTop ? this.getCoupletTop(level, checkPos) : this.getCoupletBottom(level, checkPos);
         }
      }

      return null;
   }

   private BlockPos getCoupletBottom(Level level, BlockPos pos) {
      BlockPos current = pos;

      for (int i = 0; i < 3; i++) {
         BlockPos below = current.below();
         if (!level.getBlockState(below).is(this)) {
            break;
         }

         current = below;
      }

      return current;
   }

   private BlockPos getCoupletTop(Level level, BlockPos pos) {
      BlockPos current = pos;

      for (int i = 0; i < 3; i++) {
         BlockPos above = current.above();
         if (!level.getBlockState(above).is(this)) {
            break;
         }

         current = above;
      }

      return current;
   }

   @Nullable
   private BlockPos getMasterBlockPos(Level level, BlockPos pos) {
      BlockPos current = pos;

      for (int i = 0; i < 3; i++) {
         BlockState currentState = level.getBlockState(current);
         if (!currentState.is(this)) {
            break;
         }

         if (currentState.getValue(PART) == CoupletBlock.CoupletPart.LOWER) {
            return current;
         }

         current = current.below();
         if (current.getY() < level.getMinY()) {
            break;
         }
      }

      return null;
   }

   private int getCoupletHeight(Level level, BlockPos masterPos) {
      int height = 1;
      BlockPos current = masterPos.above();

      for (int i = 0; i < 2 && level.getBlockState(current).is(this); i++) {
         height++;
         current = current.above();
      }

      return height;
   }

   private void clearCoupletGroup(Level level, BlockPos masterPos) {
      BlockPos current = masterPos.above();

      for (int i = 0; i < 2 && level.getBlockState(current).is(this); i++) {
         level.setBlock(current, Blocks.AIR.defaultBlockState(), 35);
         current = current.above();
      }

      level.setBlock(masterPos, Blocks.AIR.defaultBlockState(), 35);
   }

   // 1.21.2+ 手持物品时走 useItemOn：书与笔写入对联的逻辑在此实现
   protected InteractionResult useItemOn(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         BlockPos targetPos = pos;

         for (int i = 0; i < 3; i++) {
            BlockState currentState = level.getBlockState(targetPos);
            if (!currentState.is(this) || currentState.getValue(PART) == CoupletBlock.CoupletPart.LOWER) {
               break;
            }

            targetPos = targetPos.below();
         }

         if (level.getBlockEntity(targetPos) instanceof CoupletBlockEntity coupletEntity) {
            ItemStack heldItem = player.getItemInHand(hand);
            String targetText = this.getTextFromBook(heldItem);
            if (targetText != null && !targetText.isBlank()) {
               coupletEntity.setText(targetText);
               return InteractionResult.CONSUME;
            }
         }

         return InteractionResult.PASS;
      }
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         BlockPos targetPos = pos;

         for (int i = 0; i < 3; i++) {
            BlockState currentState = level.getBlockState(targetPos);
            if (!currentState.is(this) || currentState.getValue(PART) == CoupletBlock.CoupletPart.LOWER) {
               break;
            }

            targetPos = targetPos.below();
         }

         if (level.getBlockEntity(targetPos) instanceof CoupletBlockEntity coupletEntity) {
            ItemStack heldItem = player.getMainHandItem();
            String targetText = this.getTextFromBook(heldItem);
            if (targetText != null && !targetText.isBlank()) {
               coupletEntity.setText(targetText);
               return InteractionResult.CONSUME;
            }
         }

         return InteractionResult.PASS;
      }
   }

   @Nullable
   private String getTextFromBook(ItemStack stack) {
      WritableBookContent bookContent = (WritableBookContent)stack.get(DataComponents.WRITABLE_BOOK_CONTENT);
      if (bookContent != null && !bookContent.pages().isEmpty()) {
         String firstPage = (String)((Filterable)bookContent.pages().get(0)).raw();
         if (firstPage.isBlank()) {
            return null;
         } else {
            try {
               JsonElement element = JsonParser.parseString(firstPage);
               if (element.isJsonObject()) {
                  JsonObject obj = element.getAsJsonObject();
                  if (obj.has("text")) {
                     return obj.get("text").getAsString();
                  }
               } else if (element.isJsonPrimitive()) {
                  return element.getAsString();
               }
            } catch (Exception var6) {
            }

            return firstPage;
         }
      } else {
         return null;
      }
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      BlockPos wall = pos.relative(facing.getOpposite());
      return level.getBlockState(wall).isFaceSturdy(level, wall, facing);
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(PART) == CoupletBlock.CoupletPart.LOWER ? new CoupletBlockEntity(pos, state) : null;
   }

   private boolean isCoupletBlockWithFacing(BlockState state, Direction facing) {
      return state.is(this) && state.getValue(FACING) == facing;
   }

   private boolean isCoupletBlockWithPart(BlockState state, Direction facing, CoupletBlock.CoupletPart part) {
      return this.isCoupletBlockWithFacing(state, facing) && state.getValue(PART) == part;
   }

   private BlockState createState(Direction facing, CoupletBlock.CoupletPart part, FluidState fluidState) {
      return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facing)).setValue(PART, part))
         .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
   }

   static {
      SHAPES[Direction.NORTH.get2DDataValue()] = Block.box(4.0, 0.0, 15.9, 12.0, 16.0, 16.0);
      SHAPES[Direction.SOUTH.get2DDataValue()] = Block.box(4.0, 0.0, 0.0, 12.0, 16.0, 0.1);
      SHAPES[Direction.EAST.get2DDataValue()] = Block.box(0.0, 0.0, 4.0, 0.1, 16.0, 12.0);
      SHAPES[Direction.WEST.get2DDataValue()] = Block.box(15.9, 0.0, 4.0, 16.0, 16.0, 12.0);
   }

   public static enum CoupletPart implements StringRepresentable {
      LOWER("lower"),
      MIDDLE("middle"),
      UPPER("upper");

      private final String name;

      private CoupletPart(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
