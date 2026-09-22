package com.bmt.kaleidoscope_chinesefood.block;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StrippedBambooBenchBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 3);
    public static final BooleanProperty LOCKED = BooleanProperty.create("locked");
    public static final int SINGLE = 0;
    public static final int LEFT = 1;
    public static final int MIDDLE = 2;
    public static final int RIGHT = 3;
    private static final double[][] SINGLE_BOXES = new double[][]{{2.0, 0.0, 2.0, 14.0, 8.0, 14.0}, {2.0, 8.0, 2.0, 3.0, 19.0, 14.0}};
    private static final double[][] LEFT_BOXES = new double[][]{{2.0, 0.0, 0.0, 14.0, 8.0, 14.0}, {2.0, 8.0, 0.0, 3.0, 19.0, 14.0}};
    private static final double[][] MIDDLE_BOXES = new double[][]{{2.0, 0.0, 0.0, 14.0, 8.0, 16.0}, {2.0, 8.0, 0.0, 3.0, 19.0, 16.0}};
    private static final double[][] RIGHT_BOXES = new double[][]{{2.0, 0.0, 2.0, 14.0, 8.0, 16.0}, {2.0, 8.0, 2.0, 3.0, 19.0, 16.0}};

    public StrippedBambooBenchBlock() {
        super(
            Properties.of()
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .sound(SoundType.WOOD)
                .noOcclusion()
                .ignitedByLava()
        );
        this.registerDefaultState(
            (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POSITION, 0))
                .setValue(LOCKED, false)
        );
    }

    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, POSITION, LOCKED});
    }

    private Axis getBenchAxis(Direction facing) {
        return facing.getClockWise().getAxis();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockState state = (BlockState)this.defaultBlockState().setValue(FACING, facing);
        return context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
            ? (BlockState)((BlockState)state.setValue(POSITION, 0)).setValue(LOCKED, true)
            : this.updateConnections(context.getLevel(), context.getClickedPos(), state);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if ((Boolean)state.getValue(LOCKED)) {
            return state;
        } else {
            return direction.getAxis() == this.getBenchAxis((Direction)state.getValue(FACING)) ? this.updateConnections(level, pos, state) : state;
        }
    }

    private BlockState updateConnections(LevelAccessor level, BlockPos pos, BlockState state) {
        Axis axis = this.getBenchAxis((Direction)state.getValue(FACING));
        Direction dir = axis == Axis.X ? Direction.WEST : Direction.NORTH;
        Direction opposite = dir.getOpposite();
        boolean connect1 = this.connectsTo(level.getBlockState(pos.relative(dir)), axis);
        boolean connect2 = this.connectsTo(level.getBlockState(pos.relative(opposite)), axis);
        if (connect1 && connect2) {
            return (BlockState)state.setValue(POSITION, 2);
        } else if (connect1) {
            return (BlockState)state.setValue(POSITION, 1);
        } else {
            return connect2 ? (BlockState)state.setValue(POSITION, 3) : (BlockState)state.setValue(POSITION, 0);
        }
    }

    private boolean connectsTo(BlockState state, Axis axis) {
        return state.is(this) && !(Boolean)state.getValue(LOCKED) && this.getBenchAxis((Direction)state.getValue(FACING)) == axis;
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else if (!level.isClientSide) {
            List<SitEntity> list = level.getEntitiesOfClass(SitEntity.class, new AABB(pos));
            if (list.isEmpty()) {
                SitEntity sit = new SitEntity(level, pos, 0.5);
                sit.setYRot(((Direction)state.getValue(FACING)).toYRot());
                level.addFreshEntity(sit);
                player.startRiding(sit, true);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        } else {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        level.getEntitiesOfClass(SitEntity.class, new AABB(pos)).forEach(Entity::kill);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = (Direction)state.getValue(FACING);
        int position = (Integer)state.getValue(POSITION);
        boolean swapLeftRight = facing == Direction.SOUTH || facing == Direction.WEST;

        double[][] baseBoxes = switch (position) {
            case 1 -> swapLeftRight ? RIGHT_BOXES : LEFT_BOXES;
            case 2 -> MIDDLE_BOXES;
            case 3 -> swapLeftRight ? LEFT_BOXES : RIGHT_BOXES;
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
