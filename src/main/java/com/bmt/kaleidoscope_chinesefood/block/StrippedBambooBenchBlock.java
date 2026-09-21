package com.bmt.kaleidoscope_chinesefood.block;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 竹躺椅：可横向拼接（单/左/中/右），右键坐上去（生成 cookery 的 {@link SitEntity}）。
 * <p>
 * 注册在 {@code kaleidoscope_cookery} 命名空间（见 {@code ModCookeryBlocks}），与官方一致。
 */
public class StrippedBambooBenchBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 3);
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");

    public static final int SINGLE = 0;
    public static final int LEFT = 1;
    public static final int MIDDLE = 2;
    public static final int RIGHT = 3;

    private static final double[][] SINGLE_BOXES = {{2.0, 0.0, 2.0, 14.0, 8.0, 14.0}, {2.0, 8.0, 2.0, 3.0, 19.0, 14.0}};
    private static final double[][] LEFT_BOXES = {{2.0, 0.0, 0.0, 14.0, 8.0, 14.0}, {2.0, 8.0, 0.0, 3.0, 19.0, 14.0}};
    private static final double[][] MIDDLE_BOXES = {{2.0, 0.0, 0.0, 14.0, 8.0, 16.0}, {2.0, 8.0, 0.0, 3.0, 19.0, 16.0}};
    private static final double[][] RIGHT_BOXES = {{2.0, 0.0, 2.0, 14.0, 8.0, 16.0}, {2.0, 8.0, 2.0, 3.0, 19.0, 16.0}};

    public StrippedBambooBenchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POSITION, SINGLE)
                .setValue(LOCKED, false));
    }

    /** 竹家具的通用属性（与官方一致）。 */
    public static Properties benchProperties() {
        return Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .ignitedByLava();
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(FACING, POSITION, LOCKED);
    }

    /** 躺椅沿朝向的垂直方向拼接。 */
    private Axis getBenchAxis(Direction facing) {
        return facing.getClockWise().getAxis();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockState state = this.defaultBlockState().setValue(FACING, facing);
        Player player = context.getPlayer();
        if (player != null && player.isShiftKeyDown()) {
            return state.setValue(POSITION, SINGLE).setValue(LOCKED, true);
        }
        return this.updateConnections(context.getLevel(), context.getClickedPos(), state);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        if (state.getValue(LOCKED)) {
            return state;
        }
        return direction.getAxis() == this.getBenchAxis(state.getValue(FACING))
                ? this.updateConnections(level, pos, state)
                : state;
    }

    private BlockState updateConnections(BlockGetter level, BlockPos pos, BlockState state) {
        Axis axis = this.getBenchAxis(state.getValue(FACING));
        Direction dir = axis == Axis.X ? Direction.WEST : Direction.NORTH;
        Direction opposite = dir.getOpposite();
        boolean connect1 = this.connectsTo(level.getBlockState(pos.relative(dir)), axis);
        boolean connect2 = this.connectsTo(level.getBlockState(pos.relative(opposite)), axis);
        if (connect1 && connect2) {
            return state.setValue(POSITION, MIDDLE);
        }
        if (connect1) {
            return state.setValue(POSITION, LEFT);
        }
        return connect2 ? state.setValue(POSITION, RIGHT) : state.setValue(POSITION, SINGLE);
    }

    private boolean connectsTo(BlockState state, Axis axis) {
        return state.is(this) && !state.getValue(LOCKED) && this.getBenchAxis(state.getValue(FACING)) == axis;
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    ) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        List<SitEntity> sitting = level.getEntitiesOfClass(SitEntity.class, new AABB(pos));
        if (!sitting.isEmpty()) {
            return InteractionResult.PASS;
        }
        SitEntity sit = new SitEntity(level, pos, 0.5);
        sit.setYRot(state.getValue(FACING).toYRot());
        level.addFreshEntity(sit);
        player.startRiding(sit, true, true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        level.getEntitiesOfClass(SitEntity.class, new AABB(pos)).forEach(Entity::discard);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean swapLeftRight = facing == Direction.SOUTH || facing == Direction.WEST;
        double[][] baseBoxes = switch (state.getValue(POSITION)) {
            case LEFT -> swapLeftRight ? RIGHT_BOXES : LEFT_BOXES;
            case MIDDLE -> MIDDLE_BOXES;
            case RIGHT -> swapLeftRight ? LEFT_BOXES : RIGHT_BOXES;
            default -> SINGLE_BOXES;
        };
        VoxelShape shape = Shapes.empty();
        for (double[] box : baseBoxes) {
            shape = Shapes.or(shape, rotateBox(box[0], box[1], box[2], box[3], box[4], box[5], facing));
        }
        return shape;
    }

    private static VoxelShape rotateBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Direction facing) {
        return switch (facing) {
            case SOUTH -> Block.box(16.0 - maxZ, minY, minX, 16.0 - minZ, maxY, maxX);
            case WEST -> Block.box(16.0 - maxX, minY, 16.0 - maxZ, 16.0 - minX, maxY, 16.0 - minZ);
            case NORTH -> Block.box(minZ, minY, 16.0 - maxX, maxZ, maxY, 16.0 - minX);
            default -> Block.box(minX, minY, minZ, maxX, maxY, maxZ);
        };
    }
}
