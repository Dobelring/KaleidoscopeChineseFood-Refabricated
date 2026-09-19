package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.network.TextEditOpenS2CPayload;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
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
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<CoupletBlock.CoupletPart> PART = EnumProperty.create("part", CoupletBlock.CoupletPart.class);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final int MAX_COUPLET_HEIGHT = 3;
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
      BlockState clickedBlock = level.getBlockState(pos.relative(clickedFace.getOpposite()));
      Direction facing;
      if (clickedBlock.getBlock() instanceof CoupletBlock) {
         facing = (Direction)clickedBlock.getValue(FACING);
      } else {
         if (!clickedFace.getAxis().isHorizontal()) {
            return null;
         }

         facing = clickedFace;
      }

      BlockPos belowOwner = ownerOf(level, pos.below(), facing);
      BlockPos aboveOwner = ownerOf(level, pos.above(), facing);
      boolean joinBelow = belowOwner != null && groupSize(level, belowOwner, facing) < 3;
      boolean joinAbove = aboveOwner != null && groupSize(level, aboveOwner, facing) < 3;
      if (joinBelow && joinAbove && groupSize(level, belowOwner, facing) + 1 + groupSize(level, aboveOwner, facing) > 3) {
         return null;
      } else {
         boolean fresh = !joinBelow && !joinAbove;
         if (fresh) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (!aboveState.canBeReplaced()) {
               return null;
            }

            BlockState upper = this.createState(facing, CoupletBlock.CoupletPart.UPPER, level.getFluidState(abovePos));
            if (!upper.canSurvive(level, abovePos)) {
               return null;
            }
         }

         FluidState fluidState = level.getFluidState(pos);
         BlockState state = this.createState(facing, CoupletBlock.CoupletPart.LOWER, fluidState);
         return state.canSurvive(level, pos) ? state : null;
      }
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(level, pos, state, placer, stack);
      Direction facing = (Direction)state.getValue(FACING);
      BlockPos belowOwner = ownerOf(level, pos.below(), facing);
      BlockPos aboveOwner = ownerOf(level, pos.above(), facing);
      boolean joinBelow = belowOwner != null && groupSize(level, belowOwner, facing) < 3;
      boolean joinAbove = aboveOwner != null && groupSize(level, aboveOwner, facing) < 3;
      if (joinBelow) {
         if (level.getBlockEntity(pos) instanceof CoupletBlockEntity be) {
            be.setOwnerPos(belowOwner);
         }
      } else if (joinAbove) {
         if (level.getBlockEntity(aboveOwner) instanceof CoupletBlockEntity oldMaster) {
            migrateText(level, aboveOwner, pos);
            oldMaster.setText("");
         }

         reassignOwner(level, pos, aboveOwner, pos, facing);
      } else {
         BlockPos abovePos = pos.above();
         BlockState upper = this.createState(facing, CoupletBlock.CoupletPart.UPPER, level.getFluidState(abovePos));
         level.setBlock(abovePos, upper, 3);
         if (level.getBlockEntity(abovePos) instanceof CoupletBlockEntity upperBe) {
            upperBe.setOwnerPos(pos);
         }
      }

      updateGroupParts(level, pos, facing);
   }

   /**
    * 1.21.1 原版没有 {@code Block#onDestroyedByPlayer}（那是 NeoForge 追加的钩子），
    * 玩家破坏的唯一入口就是 playerWillDestroy。在这里就地拆掉整组并掉落；
    * 之后原版会自行把 pos 置空（此时已是空气，不会重复处理），掉落也由本方法负责，
    * 所以 loot table 是空 pools，playerDestroy 保持空实现避免双掉。
    */
   @NotNull
   public BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
      if (!level.isClientSide) {
         this.performBreak(level, pos, state, !player.isCreative(), false);
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack tool) {
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!this.canSurvive(state, level, pos)) {
         this.performBreak(level, pos, state, true, true);
      }
   }

   private boolean performBreak(Level level, BlockPos pos, BlockState state, boolean dropItems, boolean eventAtClicked) {
      if (!(level.getBlockEntity(pos) instanceof CoupletBlockEntity be)) {
         return false;
      } else {
         Direction facing = (Direction)state.getValue(FACING);
         BlockPos owner = be.getOwnerPos();
         ArrayList<BlockPos> members = new ArrayList<>();
         BlockPos cur = owner;

         for (int size = 0; size < 4 && isOwner(level, cur, owner, facing); size++) {
            members.add(cur);
            cur = cur.above();
         }

         int size = members.size();
         int index = members.indexOf(pos);
         if (index < 0) {
            return false;
         } else {
            boolean destroyAll = size <= 2 || size == 3 && index == 1;
            if (destroyAll) {
               for (BlockPos m : members) {
                  boolean play = m.equals(pos) ? eventAtClicked : true;
                  this.removeBlock(level, m, play);
               }

               if (dropItems) {
                  int drops = Math.max(1, size - 1);

                  for (int k = 0; k < drops; k++) {
                     popResource(level, pos, new ItemStack(this.asItem()));
                  }
               }
            } else {
               if (index == 0) {
                  BlockPos newMaster = members.get(1);
                  migrateText(level, pos, newMaster);
                  reassignOwner(level, newMaster, owner, newMaster, facing);
               }

               this.removeBlock(level, pos, eventAtClicked);
               if (dropItems) {
                  popResource(level, pos, new ItemStack(this.asItem()));
               }

               updateGroupParts(level, members.get(Math.min(index == 0 ? 1 : 0, size - 1)), facing);
            }

            return true;
         }
      }
   }

   private void removeBlock(Level level, BlockPos m, boolean playEvent) {
      if (playEvent) {
         level.destroyBlock(m, false);
      } else {
         level.setBlock(m, Blocks.AIR.defaultBlockState(), 35);
      }
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      Direction facing = (Direction)state.getValue(FACING);
      if (direction == facing.getOpposite() && !this.canSurvive(state, level, pos)) {
         level.scheduleTick(pos, this, 1);
      }

      return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (!level.isClientSide && level.getBlockEntity(pos) instanceof CoupletBlockEntity be) {
         BlockPos owner = be.getOwnerPos();
         if (level.getBlockEntity(owner) instanceof CoupletBlockEntity master) {
            if (stack.is(Items.GLOW_INK_SAC)) {
               master.setGlowing(true);
               level.playSound(null, owner, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS);
               if (!player.isCreative()) {
                  stack.shrink(1);
               }

               return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }

            if (stack.is(Items.INK_SAC)) {
               master.setGlowing(false);
               level.playSound(null, owner, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS);
               if (!player.isCreative()) {
                  stack.shrink(1);
               }

               return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
         }
      }

      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (!level.isClientSide && level.getBlockEntity(pos) instanceof CoupletBlockEntity be && player instanceof ServerPlayer serverPlayer) {
         BlockPos owner = be.getOwnerPos();
         if (level.getBlockEntity(owner) instanceof CoupletBlockEntity master) {
            ServerPlayNetworking.send(
               serverPlayer, new TextEditOpenS2CPayload(owner, master.getText(), master.getMaxChars(), master.getSegmentCount(), true)
            );
         }
      }

      return InteractionResult.SUCCESS;
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
      return new CoupletBlockEntity(pos, state);
   }

   public void appendHoverText(ItemStack stack, @NotNull TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(
         Component.translatable("block.kaleidoscope_chinesefood.couplet.desc").withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
      );
   }

   @Nullable
   private static BlockPos ownerOf(Level level, BlockPos p, Direction facing) {
      BlockState s = level.getBlockState(p);
      return s.getBlock() instanceof CoupletBlock && s.getValue(FACING) == facing && level.getBlockEntity(p) instanceof CoupletBlockEntity be
         ? be.getOwnerPos()
         : null;
   }

   private static boolean isOwner(Level level, BlockPos p, BlockPos owner, Direction facing) {
      BlockState s = level.getBlockState(p);
      return s.getBlock() instanceof CoupletBlock
         && s.getValue(FACING) == facing
         && level.getBlockEntity(p) instanceof CoupletBlockEntity be
         && be.getOwnerPos().equals(owner);
   }

   private static int groupSize(Level level, BlockPos owner, Direction facing) {
      int count = 0;
      BlockPos cur = owner;

      for (int i = 0; i < 4 && isOwner(level, cur, owner, facing); i++) {
         count++;
         cur = cur.above();
      }

      return count;
   }

   private static void reassignOwner(Level level, BlockPos around, BlockPos oldOwner, BlockPos newOwner, Direction facing) {
      for (int i = -3; i <= 3; i++) {
         BlockPos p = around.above(i);
         if (isOwner(level, p, oldOwner, facing) && level.getBlockEntity(p) instanceof CoupletBlockEntity be) {
            be.setOwnerPos(newOwner);
         }
      }
   }

   private static void updateGroupParts(Level level, BlockPos around, Direction facing) {
      for (int i = -3; i <= 3; i++) {
         BlockPos p = around.above(i);
         BlockState s = level.getBlockState(p);
         if (s.getBlock() instanceof CoupletBlock && s.getValue(FACING) == facing && level.getBlockEntity(p) instanceof CoupletBlockEntity be) {
            BlockPos owner = be.getOwnerPos();
            boolean below = isOwner(level, p.below(), owner, facing);
            boolean above = isOwner(level, p.above(), owner, facing);
            CoupletBlock.CoupletPart part = below && above
               ? CoupletBlock.CoupletPart.MIDDLE
               : (below ? CoupletBlock.CoupletPart.UPPER : CoupletBlock.CoupletPart.LOWER);
            if (s.getValue(PART) != part) {
               level.setBlock(p, (BlockState)s.setValue(PART, part), 3);
            }
         }
      }
   }

   private static void migrateText(Level level, BlockPos from, BlockPos to) {
      if (level.getBlockEntity(from) instanceof CoupletBlockEntity fromBe && level.getBlockEntity(to) instanceof CoupletBlockEntity toBe) {
         CompoundTag data = fromBe.saveWithFullMetadata(level.registryAccess());
         data.remove("x");
         data.remove("y");
         data.remove("z");
         toBe.loadCustomOnly(data, level.registryAccess());
         toBe.setChanged();
         level.sendBlockUpdated(to, toBe.getBlockState(), toBe.getBlockState(), 3);
      }
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
