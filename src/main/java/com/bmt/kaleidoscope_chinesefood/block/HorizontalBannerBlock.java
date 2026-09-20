package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalBannerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.network.TextEditOpenS2CPayload;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 横批（1.1.11 重构版）。
 * <p>
 * 与对联同构，只是分组沿水平方向展开：锚点是最左那一格，成员沿 {@code facing.getCounterClockWise()} 排列。
 * 掉落由本类负责（战利品表 pools 为空，playerDestroy 空实现避免双掉）。
 */
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
      return SHAPES[state.getValue(FACING).get2DDataValue()];
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return Shapes.empty();
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      Direction clickedFace = context.getClickedFace();
      BlockState clickedBlock = level.getBlockState(pos.relative(clickedFace.getOpposite()));
      Direction facing;
      if (clickedBlock.getBlock() instanceof HorizontalBannerBlock) {
         facing = clickedBlock.getValue(FACING);
      } else {
         if (!clickedFace.getAxis().isHorizontal()) {
            return null;
         }

         facing = clickedFace;
      }

      BlockPos leftOwner = ownerOf(level, pos.relative(facing.getClockWise()), facing);
      BlockPos rightOwner = ownerOf(level, pos.relative(facing.getCounterClockWise()), facing);
      boolean joinLeft = leftOwner != null && groupSize(level, leftOwner, facing) < MAX_BANNER_WIDTH;
      boolean joinRight = rightOwner != null && groupSize(level, rightOwner, facing) < MAX_BANNER_WIDTH;
      if (joinLeft && joinRight && groupSize(level, leftOwner, facing) + 1 + groupSize(level, rightOwner, facing) > MAX_BANNER_WIDTH) {
         return null;
      }

      BlockState state = this.createState(facing, level.getFluidState(pos));
      return state.canSurvive(level, pos) ? state : null;
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      Direction facing = state.getValue(FACING);
      BlockPos leftOwner = ownerOf(level, pos.relative(facing.getClockWise()), facing);
      BlockPos rightOwner = ownerOf(level, pos.relative(facing.getCounterClockWise()), facing);
      boolean joinLeft = leftOwner != null && groupSize(level, leftOwner, facing) < MAX_BANNER_WIDTH;
      boolean joinRight = rightOwner != null && groupSize(level, rightOwner, facing) < MAX_BANNER_WIDTH;
      if (joinLeft && joinRight) {
         // 插在两组中间：并入左边那组，右侧整组改挂到新锚点
         if (level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity be) {
            be.setOwnerPos(leftOwner);
         }

         if (level.getBlockEntity(rightOwner) instanceof HorizontalBannerBlockEntity rightBe) {
            rightBe.setText("");
         }

         reassignOwner(level, pos, rightOwner, leftOwner, facing);
      } else if (joinRight) {
         if (level.getBlockEntity(rightOwner) instanceof HorizontalBannerBlockEntity rightBe) {
            migrateText(level, rightOwner, pos);
            rightBe.setText("");
         }

         reassignOwner(level, pos, rightOwner, pos, facing);
      } else if (joinLeft && level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity be) {
         be.setOwnerPos(leftOwner);
      }

      updateGroupParts(level, pos, facing);
   }

   /**
    * 原版没有 {@code Block#onDestroyedByPlayer}（那是 NeoForge 追加的钩子），玩家破坏的唯一入口就是
    * playerWillDestroy。在这里就地拆掉整组并掉落；掉落由本方法负责，所以战利品表是空 pools，
    * playerDestroy 保持空实现避免双掉。
    */
   @NotNull
   public BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
      if (!level.isClientSide()) {
         this.performBreak(level, pos, state, !player.isCreative(), false);
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack tool) {
   }

   protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!this.canSurvive(state, level, pos)) {
         this.performBreak(level, pos, state, true, true);
      }
   }

   private boolean performBreak(Level level, BlockPos pos, BlockState state, boolean dropItems, boolean eventAtClicked) {
      if (!(level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity be)) {
         return false;
      }

      Direction facing = state.getValue(FACING);
      BlockPos owner = be.getOwnerPos();
      ArrayList<BlockPos> members = new ArrayList<>();
      BlockPos cur = owner;

      for (int size = 0; size < 4 && isOwner(level, cur, owner, facing); size++) {
         members.add(cur);
         cur = cur.relative(facing.getCounterClockWise());
      }

      int size = members.size();
      int index = members.indexOf(pos);
      if (index < 0) {
         return false;
      }

      // 三格横批只拆中间那格时保留分组
      boolean destroyAll = size == 3 && index == 1;
      if (destroyAll) {
         for (BlockPos m : members) {
            boolean play = m.equals(pos) ? eventAtClicked : true;
            this.removeBlock(level, m, play);
         }

         if (dropItems) {
            for (int k = 0; k < size; k++) {
               popResource(level, pos, new ItemStack(this.asItem()));
            }
         }
      } else {
         if (index == 0 && size > 1) {
            BlockPos newMaster = members.get(1);
            migrateText(level, pos, newMaster);
            reassignOwner(level, newMaster, owner, newMaster, facing);
         }

         this.removeBlock(level, pos, eventAtClicked);
         if (dropItems) {
            popResource(level, pos, new ItemStack(this.asItem()));
         }

         updateGroupParts(level, size > 1 ? members.get(1) : pos, facing);
      }

      return true;
   }

   private void removeBlock(Level level, BlockPos m, boolean playEvent) {
      if (playEvent) {
         level.destroyBlock(m, false);
      } else {
         level.setBlock(m, Blocks.AIR.defaultBlockState(), 35);
      }
   }

   public BlockState updateShape(
      BlockState state,
      LevelReader level,
      ScheduledTickAccess tickAccess,
      BlockPos pos,
      Direction direction,
      BlockPos neighborPos,
      BlockState neighborState,
      RandomSource random
   ) {
      if (state.getValue(WATERLOGGED)) {
         tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      Direction facing = state.getValue(FACING);
      if (direction == facing.getOpposite() && !this.canSurvive(state, level, pos)) {
         tickAccess.scheduleTick(pos, this, 1);
         return state;
      }

      if ((direction == facing.getClockWise() || direction == facing.getCounterClockWise())
         && level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity be) {
         BlockPos owner = be.getOwnerPos();
         boolean left = isOwner(level, pos.relative(facing.getClockWise()), owner, facing);
         boolean right = isOwner(level, pos.relative(facing.getCounterClockWise()), owner, facing);
         HorizontalBannerBlock.BannerPart part = left && right
            ? HorizontalBannerBlock.BannerPart.MIDDLE
            : (left ? HorizontalBannerBlock.BannerPart.RIGHT : (right ? HorizontalBannerBlock.BannerPart.LEFT : HorizontalBannerBlock.BannerPart.SINGLE));
         return state.setValue(PART, part);
      }

      return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      Direction facing = state.getValue(FACING);
      BlockPos wallPos = pos.relative(facing.getOpposite());
      return level.getBlockState(wallPos).isFaceSturdy(level, wallPos, facing);
   }

   /** 萤石粉 / 墨囊切换整组文字的发光；其余情况交还给空手交互（开编辑界面） */
   protected InteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (!level.isClientSide() && level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity be) {
         BlockPos owner = be.getOwnerPos();
         if (level.getBlockEntity(owner) instanceof HorizontalBannerBlockEntity master) {
            if (stack.is(Items.GLOW_INK_SAC)) {
               master.setGlowing(true);
               level.playSound(null, owner, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS);
               if (!player.isCreative()) {
                  stack.shrink(1);
               }

               return InteractionResult.SUCCESS;
            }

            if (stack.is(Items.INK_SAC)) {
               master.setGlowing(false);
               level.playSound(null, owner, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS);
               if (!player.isCreative()) {
                  stack.shrink(1);
               }

               return InteractionResult.SUCCESS;
            }
         }
      }

      return InteractionResult.TRY_WITH_EMPTY_HAND;
   }

   /** 空手右键：把权威文字/长度上限/格数下发给客户端开编辑界面（横批为横排） */
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (!level.isClientSide() && level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity be && player instanceof ServerPlayer serverPlayer) {
         BlockPos owner = be.getOwnerPos();
         if (level.getBlockEntity(owner) instanceof HorizontalBannerBlockEntity master) {
            ServerPlayNetworking.send(
               serverPlayer, new TextEditOpenS2CPayload(owner, master.getText(), master.getMaxChars(), master.getSegmentCount(), false)
            );
         }
      }

      return InteractionResult.SUCCESS;
   }

   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new HorizontalBannerBlockEntity(pos, state);
   }

   @Nullable
   private static BlockPos ownerOf(Level level, BlockPos p, Direction facing) {
      BlockState s = level.getBlockState(p);
      return s.getBlock() instanceof HorizontalBannerBlock && s.getValue(FACING) == facing && level.getBlockEntity(p) instanceof HorizontalBannerBlockEntity be
         ? be.getOwnerPos()
         : null;
   }

   private static boolean isOwner(BlockGetter level, BlockPos p, BlockPos owner, Direction facing) {
      BlockState s = level.getBlockState(p);
      return s.getBlock() instanceof HorizontalBannerBlock
         && s.getValue(FACING) == facing
         && level.getBlockEntity(p) instanceof HorizontalBannerBlockEntity be
         && be.getOwnerPos().equals(owner);
   }

   private static int groupSize(Level level, BlockPos owner, Direction facing) {
      int count = 0;
      BlockPos cur = owner;

      for (int i = 0; i < 4 && isOwner(level, cur, owner, facing); i++) {
         count++;
         cur = cur.relative(facing.getCounterClockWise());
      }

      return count;
   }

   private static void reassignOwner(Level level, BlockPos around, BlockPos oldOwner, BlockPos newOwner, Direction facing) {
      for (int i = -3; i <= 3; i++) {
         BlockPos p = around.relative(facing.getClockWise(), i);
         if (isOwner(level, p, oldOwner, facing) && level.getBlockEntity(p) instanceof HorizontalBannerBlockEntity be) {
            be.setOwnerPos(newOwner);
         }
      }
   }

   private static void updateGroupParts(Level level, BlockPos around, Direction facing) {
      for (int i = -3; i <= 3; i++) {
         BlockPos p = around.relative(facing.getClockWise(), i);
         BlockState s = level.getBlockState(p);
         if (s.getBlock() instanceof HorizontalBannerBlock
            && s.getValue(FACING) == facing
            && level.getBlockEntity(p) instanceof HorizontalBannerBlockEntity be) {
            BlockPos owner = be.getOwnerPos();
            boolean left = isOwner(level, p.relative(facing.getClockWise()), owner, facing);
            boolean right = isOwner(level, p.relative(facing.getCounterClockWise()), owner, facing);
            HorizontalBannerBlock.BannerPart part = left && right
               ? HorizontalBannerBlock.BannerPart.MIDDLE
               : (left ? HorizontalBannerBlock.BannerPart.RIGHT : (right ? HorizontalBannerBlock.BannerPart.LEFT : HorizontalBannerBlock.BannerPart.SINGLE));
            if (s.getValue(PART) != part) {
               level.setBlock(p, s.setValue(PART, part), 3);
            }
         }
      }
   }

   /** 把一格 BE 的文字/发光迁到另一格；26.x 的 BE 数据走 ValueInput，故经 TagValueInput 还原 */
   private static void migrateText(Level level, BlockPos from, BlockPos to) {
      if (level.getBlockEntity(from) instanceof HorizontalBannerBlockEntity fromBe
         && level.getBlockEntity(to) instanceof HorizontalBannerBlockEntity toBe) {
         CompoundTag data = fromBe.saveWithFullMetadata(level.registryAccess());
         data.remove("x");
         data.remove("y");
         data.remove("z");
         toBe.loadCustomOnly(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data));
         toBe.setChanged();
         level.sendBlockUpdated(to, toBe.getBlockState(), toBe.getBlockState(), 3);
      }
   }

   private BlockState createState(Direction facing, FluidState fluidState) {
      return (BlockState)this.defaultBlockState().setValue(FACING, facing).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
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
