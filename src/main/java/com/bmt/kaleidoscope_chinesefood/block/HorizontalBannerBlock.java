package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalBannerBlockEntity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
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

public class HorizontalBannerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<HorizontalBannerBlock.BannerPart> PART = EnumProperty.create("part", HorizontalBannerBlock.BannerPart.class);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final int MAX_BANNER_WIDTH = 3;
   private static final MapCodec<HorizontalBannerBlock> CODEC = simpleCodec(HorizontalBannerBlock::new);
   private static final VoxelShape[] SHAPES = new VoxelShape[4];

   public HorizontalBannerBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
               .setValue(PART, HorizontalBannerBlock.BannerPart.SINGLE))
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
         int leftCount = this.countAdjacentBanners(level, pos, clickedFace, true);
         int rightCount = this.countAdjacentBanners(level, pos, clickedFace, false);
         if (leftCount + 1 + rightCount > 3) {
            return null;
         } else {
            BlockState state = this.createState(clickedFace, level.getFluidState(pos));
            return state.canSurvive(level, pos) ? state : null;
         }
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      Direction facing = (Direction)state.getValue(FACING);
      BlockPos rightPos = pos.relative(facing.getCounterClockWise());
      CompoundTag savedData = null;
      if (this.isBannerBlockWithPart(level.getBlockState(rightPos), facing, HorizontalBannerBlock.BannerPart.LEFT)
         && level.getBlockEntity(rightPos) instanceof HorizontalBannerBlockEntity oldBannerBE) {
         savedData = oldBannerBE.saveCustomOnly(level.registryAccess());
      }

      this.updateNeighbors(level, pos, state);
      if (savedData != null && level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity newBannerBE) {
         newBannerBE.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), savedData));
         newBannerBE.setChanged();
      }
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      if (!level.isClientSide()) {
         Direction facing = (Direction)state.getValue(FACING);
         CompoundTag savedData = null;
         if (state.getValue(PART) == HorizontalBannerBlock.BannerPart.LEFT && level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity oldBannerBE) {
            savedData = oldBannerBE.saveCustomOnly(level.registryAccess());
         }

         if (!player.isCreative()) {
            popResource(level, pos, new ItemStack(this.asItem()));
         }

         level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
         this.updateNeighbors(level, pos, state);
         if (savedData != null) {
            BlockPos newLeftPos = this.findNewLeftPosition(level, pos, facing);
            if (newLeftPos != null && level.getBlockEntity(newLeftPos) instanceof HorizontalBannerBlockEntity newBannerBE) {
               newBannerBE.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), savedData));
               newBannerBE.setChanged();
            }
         }
      }

      return true;
   }

   @NotNull
   public BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
      return state;
   }

   public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      Direction facing = (Direction)state.getValue(FACING);
      if (direction == facing.getClockWise() || direction == facing.getCounterClockWise()) {
         this.enforceMaxBannerLength(level, pos, facing);
         return this.calculateSimplePart(level, pos, facing);
      } else {
         return direction == facing.getOpposite() && !this.canSurvive(state, level, pos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
      }
   }

   private void enforceMaxBannerLength(LevelReader level, BlockPos pos, Direction facing) {
      BlockPos leftmost = pos;

      while (true) {
         BlockPos next = leftmost.relative(facing.getClockWise());
         if (!this.isBannerBlockWithFacing(level.getBlockState(next), facing)) {
            int length = 0;
            BlockPos current = leftmost;

            while (true) {
               BlockState state = level.getBlockState(current);
               if (!this.isBannerBlockWithFacing(state, facing)) {
                  return;
               }

               if (++length > 3) {
                  ((Level)level).destroyBlock(current, true);
               }

               current = current.relative(facing.getCounterClockWise());
            }
         }

         leftmost = next;
      }
   }

   private BlockState calculateSimplePart(LevelReader level, BlockPos pos, Direction facing) {
      boolean hasLeft = this.isBannerBlockWithFacing(level.getBlockState(pos.relative(facing.getClockWise())), facing);
      boolean hasRight = this.isBannerBlockWithFacing(level.getBlockState(pos.relative(facing.getCounterClockWise())), facing);
      if (hasLeft && hasRight) {
         return (BlockState)level.getBlockState(pos).setValue(PART, HorizontalBannerBlock.BannerPart.MIDDLE);
      } else if (hasLeft) {
         return (BlockState)level.getBlockState(pos).setValue(PART, HorizontalBannerBlock.BannerPart.RIGHT);
      } else {
         return hasRight
            ? (BlockState)level.getBlockState(pos).setValue(PART, HorizontalBannerBlock.BannerPart.LEFT)
            : (BlockState)level.getBlockState(pos).setValue(PART, HorizontalBannerBlock.BannerPart.SINGLE);
      }
   }

   private void updateNeighbors(Level level, BlockPos pos, BlockState state) {
      Direction facing = (Direction)state.getValue(FACING);
      Direction sideDir = facing.getClockWise();

      for (int i = -2; i <= 2; i++) {
         BlockPos updatePos = pos.offset(sideDir.getStepX() * i, sideDir.getStepY() * i, sideDir.getStepZ() * i);
         if (this.isBannerBlockWithFacing(level.getBlockState(updatePos), facing)) {
            level.setBlock(updatePos, this.calculateSimplePart(level, updatePos, facing), 3);
         }
      }
   }

   @Nullable
   private BlockPos findNewLeftPosition(Level level, BlockPos oldLeftPos, Direction facing) {
      BlockPos current = oldLeftPos.relative(facing.getCounterClockWise());

      for (int i = 0; i < 2; i++) {
         if (this.isBannerBlockWithFacing(level.getBlockState(current), facing)) {
            return current;
         }

         current = current.relative(facing.getCounterClockWise());
      }

      return null;
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction facing = (Direction)state.getValue(FACING);
      BlockPos wallPos = pos.relative(facing.getOpposite());
      return level.getBlockState(wallPos).isFaceSturdy(level, wallPos, facing);
   }

   protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      return this.tryWriteText(state, level, pos, stack);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      return this.tryWriteText(state, level, pos, player.getMainHandItem());
   }

   private InteractionResult tryWriteText(BlockState state, Level level, BlockPos pos, ItemStack heldItem) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         BlockPos entityPos = pos;
         Direction facing = (Direction)state.getValue(FACING);

         for (int i = 0; i < 2; i++) {
            BlockPos left = entityPos.relative(facing.getClockWise());
            if (!this.isBannerBlockWithFacing(level.getBlockState(left), facing)) {
               break;
            }

            entityPos = left;
         }

         if (level.getBlockEntity(entityPos) instanceof HorizontalBannerBlockEntity bannerEntity) {
            String targetText = this.getTextFromBook(heldItem);
            if (targetText != null && !targetText.isBlank()) {
               bannerEntity.setText(targetText);
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

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(PART) != HorizontalBannerBlock.BannerPart.SINGLE && state.getValue(PART) != HorizontalBannerBlock.BannerPart.LEFT
         ? null
         : new HorizontalBannerBlockEntity(pos, state);
   }

   private boolean isBannerBlockWithFacing(BlockState state, Direction facing) {
      return state.is(this) && state.getValue(FACING) == facing;
   }

   private boolean isBannerBlockWithPart(BlockState state, Direction facing, HorizontalBannerBlock.BannerPart part) {
      return this.isBannerBlockWithFacing(state, facing) && state.getValue(PART) == part;
   }

   private BlockState createState(Direction facing, FluidState fluidState) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facing)).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
   }

   private int countAdjacentBanners(Level level, BlockPos pos, Direction facing, boolean countLeft) {
      int count = 0;

      for (BlockPos current = pos.relative(countLeft ? facing.getClockWise() : facing.getCounterClockWise());
         this.isBannerBlockWithFacing(level.getBlockState(current), facing);
         current = current.relative(countLeft ? facing.getClockWise() : facing.getCounterClockWise())
      ) {
         count++;
      }

      return count;
   }

   static {
      SHAPES[Direction.NORTH.get2DDataValue()] = Block.box(0.0, 2.0, 15.9, 16.0, 10.0, 16.0);
      SHAPES[Direction.SOUTH.get2DDataValue()] = Block.box(0.0, 2.0, 0.0, 16.0, 10.0, 0.1);
      SHAPES[Direction.EAST.get2DDataValue()] = Block.box(0.0, 2.0, 0.0, 0.1, 10.0, 16.0);
      SHAPES[Direction.WEST.get2DDataValue()] = Block.box(15.9, 2.0, 0.0, 16.0, 10.0, 16.0);
   }

   public static enum BannerPart implements StringRepresentable {
      SINGLE("single"),
      LEFT("left"),
      MIDDLE("middle"),
      RIGHT("right");

      private final String name;

      private BannerPart(String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
