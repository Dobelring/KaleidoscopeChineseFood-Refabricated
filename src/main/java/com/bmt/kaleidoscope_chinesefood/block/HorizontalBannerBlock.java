package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalBannerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.network.ModNetwork;
import com.bmt.kaleidoscope_chinesefood.network.TextEditOpenS2CPacket;
import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
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
import org.jetbrains.annotations.Nullable;

public class HorizontalBannerBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<HorizontalBannerBlock.BannerPart> PART = EnumProperty.create("part", HorizontalBannerBlock.BannerPart.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int MAX_BANNER_WIDTH = 3;
    private static final VoxelShape[] SHAPES = new VoxelShape[4];

    public HorizontalBannerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                    .setValue(PART, HorizontalBannerBlock.BannerPart.SINGLE))
                .setValue(WATERLOGGED, false)
        );
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
        if (clickedBlock.getBlock() instanceof HorizontalBannerBlock) {
            facing = (Direction)clickedBlock.getValue(FACING);
        } else {
            if (!clickedFace.getAxis().isHorizontal()) {
                return null;
            }

            facing = clickedFace;
        }

        BlockPos leftOwner = ownerOf(level, pos.relative(facing.getClockWise()), facing);
        BlockPos rightOwner = ownerOf(level, pos.relative(facing.getCounterClockWise()), facing);
        boolean joinLeft = leftOwner != null && groupSize(level, leftOwner, facing) < 3;
        boolean joinRight = rightOwner != null && groupSize(level, rightOwner, facing) < 3;
        if (joinLeft && joinRight && groupSize(level, leftOwner, facing) + 1 + groupSize(level, rightOwner, facing) > 3) {
            return null;
        } else {
            BlockState state = this.createState(facing, level.getFluidState(pos));
            return state.canSurvive(level, pos) ? state : null;
        }
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        Direction facing = (Direction)state.getValue(FACING);
        BlockPos leftOwner = ownerOf(level, pos.relative(facing.getClockWise()), facing);
        BlockPos rightOwner = ownerOf(level, pos.relative(facing.getCounterClockWise()), facing);
        boolean joinLeft = leftOwner != null && groupSize(level, leftOwner, facing) < 3;
        boolean joinRight = rightOwner != null && groupSize(level, rightOwner, facing) < 3;
        if (joinLeft && joinRight) {
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
     * 原 Forge 版把整套拆除/掉落逻辑挂在 {@code onDestroyedByPlayer} 上——那是 Forge/NeoForge 专有钩子，
     * 1.20.1 原版 {@code Block} 里没有该方法，所以转换后那段逻辑在 Fabric 上是死代码。
     * <p>
     * 这里挂到原版钩子 playerWillDestroy：它在原版移除方块**之前**、方块实体还在时调用，
     * 这一点是必须的——performBreak 需要读 {@code CoupletBlockEntity#getOwnerPos()}；
     * 放到 playerDestroy 会失败，因为那时方块实体已经被移除，performBreak 直接 return false。
     * <p>
     * eventAtClicked 传 true：让被点的那一格也由 destroyBlock 播放破坏音效与粒子，
     * 其余成员同样逐个带音效拆除。掉落全部由 performBreak 的 popResource 提供，
     * 因此 playerDestroy 保持空覆写以抑制原版战利品表掉落（避免双倍）。
     */
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            boolean handled = this.performBreak(level, pos, state, !player.isCreative(), true);
            if (!handled) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }

    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack tool) {
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!this.canSurvive(state, level, pos)) {
            this.performBreak(level, pos, state, true, true);
        }
    }

    private boolean performBreak(Level level, BlockPos pos, BlockState state, boolean dropItems, boolean eventAtClicked) {
        if (!(level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity be)) {
            return false;
        } else {
            Direction var17 = (Direction)state.getValue(FACING);
            BlockPos owner = be.getOwnerPos();
            ArrayList<BlockPos> members = new ArrayList<>();
            BlockPos cur = owner;

            for (int size = 0; size < 4 && isOwner(level, cur, owner, var17); size++) {
                members.add(cur);
                cur = cur.relative(var17.getCounterClockWise());
            }

            int size = members.size();
            int index = members.indexOf(pos);
            if (index < 0) {
                return false;
            } else {
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
                        BlockPos newMaster = (BlockPos)members.get(1);
                        migrateText(level, pos, newMaster);
                        reassignOwner(level, newMaster, owner, newMaster, var17);
                    }

                    this.removeBlock(level, pos, eventAtClicked);
                    if (dropItems) {
                        popResource(level, pos, new ItemStack(this.asItem()));
                    }

                    updateGroupParts(level, size > 1 ? (BlockPos)members.get(1) : pos, var17);
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
            return state;
        } else if ((direction == facing.getClockWise() || direction == facing.getCounterClockWise()) && level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity be) {
            BlockPos owner = be.getOwnerPos();
            boolean left = isOwner(level, pos.relative(facing.getClockWise()), owner, facing);
            boolean right = isOwner(level, pos.relative(facing.getCounterClockWise()), owner, facing);
            HorizontalBannerBlock.BannerPart part = left && right
                ? HorizontalBannerBlock.BannerPart.MIDDLE
                : (left ? HorizontalBannerBlock.BannerPart.RIGHT : (right ? HorizontalBannerBlock.BannerPart.LEFT : HorizontalBannerBlock.BannerPart.SINGLE));
            return (BlockState)state.setValue(PART, part);
        } else {
            return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        }
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = (Direction)state.getValue(FACING);
        BlockPos wallPos = pos.relative(facing.getOpposite());
        return level.getBlockState(wallPos).isFaceSturdy(level, wallPos, facing);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (level.getBlockEntity(pos) instanceof HorizontalBannerBlockEntity be) {
            ItemStack stack = player.getItemInHand(hand);
            BlockPos owner = be.getOwnerPos();
            if (level.getBlockEntity(owner) instanceof HorizontalBannerBlockEntity master) {
                if (stack.is(Items.GLOW_INK_SAC)) {
                    master.setGlowing(true);
                    level.playSound(null, owner, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }

                    return InteractionResult.CONSUME;
                }

                if (stack.is(Items.INK_SAC)) {
                    master.setGlowing(false);
                    level.playSound(null, owner, SoundEvents.INK_SAC_USE, SoundSource.BLOCKS);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }

                    return InteractionResult.CONSUME;
                }
            }

            ModNetwork.sendToPlayer(new TextEditOpenS2CPacket(be.getOwnerPos()), (ServerPlayer)player);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HorizontalBannerBlockEntity(pos, state);
    }

    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(
            Component.translatable("block.kaleidoscope_chinesefood.horizontal_scroll.desc")
                .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
        );
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
            if (s.getBlock() instanceof HorizontalBannerBlock && s.getValue(FACING) == facing && level.getBlockEntity(p) instanceof HorizontalBannerBlockEntity be) {
                BlockPos owner = be.getOwnerPos();
                boolean left = isOwner(level, p.relative(facing.getClockWise()), owner, facing);
                boolean right = isOwner(level, p.relative(facing.getCounterClockWise()), owner, facing);
                HorizontalBannerBlock.BannerPart part = left && right
                    ? HorizontalBannerBlock.BannerPart.MIDDLE
                    : (left ? HorizontalBannerBlock.BannerPart.RIGHT : (right ? HorizontalBannerBlock.BannerPart.LEFT : HorizontalBannerBlock.BannerPart.SINGLE));
                if (s.getValue(PART) != part) {
                    level.setBlock(p, (BlockState)s.setValue(PART, part), 3);
                }
            }
        }
    }

    private static void migrateText(Level level, BlockPos from, BlockPos to) {
        if (level.getBlockEntity(from) instanceof HorizontalBannerBlockEntity fromBe && level.getBlockEntity(to) instanceof HorizontalBannerBlockEntity toBe) {
            CompoundTag data = fromBe.saveWithFullMetadata();
            data.remove("x");
            data.remove("y");
            data.remove("z");
            toBe.load(data);
            toBe.setChanged();
            level.sendBlockUpdated(to, toBe.getBlockState(), toBe.getBlockState(), 3);
        }
    }

    private BlockState createState(Direction facing, FluidState fluidState) {
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facing)).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
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
