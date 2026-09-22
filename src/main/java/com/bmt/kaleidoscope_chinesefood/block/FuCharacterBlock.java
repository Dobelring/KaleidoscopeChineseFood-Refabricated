package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.mixins.accessor.TrapDoorBlockAccessor;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FuCharacterBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<FuCharacterBlock.FuState> FU_STATE = EnumProperty.create("fu_state", FuCharacterBlock.FuState.class);

    private static VoxelShape makeShape(Direction facing, FuCharacterBlock.FuState fuState) {
        return switch (facing) {
            case NORTH -> {
                switch (fuState) {
                    case NORMAL:
                    case DOOR_BACK:
                        yield Block.box(0.0, 0.0, 15.9, 16.0, 16.0, 16.0);
                    case LEFT_OPEN:
                        yield Block.box(13.0, 0.0, 15.9, 16.0, 16.0, 16.0);
                    case RIGHT_OPEN:
                        yield Block.box(0.0, 0.0, 15.9, 3.0, 16.0, 16.0);
                    case TRAPDOOR_BOTTOM_OPEN:
                        yield Block.box(0.0, 0.0, 15.9, 16.0, 3.0, 16.0);
                    case TRAPDOOR_TOP_OPEN:
                        yield Block.box(0.0, 13.0, 15.9, 16.0, 16.0, 16.0);
                    default:
                        throw new IncompatibleClassChangeError();
                }
            }
            case SOUTH -> {
                switch (fuState) {
                    case NORMAL:
                    case DOOR_BACK:
                        yield Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 0.1);
                    case LEFT_OPEN:
                        yield Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 0.1);
                    case RIGHT_OPEN:
                        yield Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 0.1);
                    case TRAPDOOR_BOTTOM_OPEN:
                        yield Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 0.1);
                    case TRAPDOOR_TOP_OPEN:
                        yield Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 0.1);
                    default:
                        throw new IncompatibleClassChangeError();
                }
            }
            case EAST -> {
                switch (fuState) {
                    case NORMAL:
                    case DOOR_BACK:
                        yield Block.box(0.0, 0.0, 0.0, 0.1, 16.0, 16.0);
                    case LEFT_OPEN:
                        yield Block.box(0.0, 0.0, 13.0, 0.1, 16.0, 16.0);
                    case RIGHT_OPEN:
                        yield Block.box(0.0, 0.0, 0.0, 0.1, 16.0, 3.0);
                    case TRAPDOOR_BOTTOM_OPEN:
                        yield Block.box(0.0, 0.0, 0.0, 0.1, 3.0, 16.0);
                    case TRAPDOOR_TOP_OPEN:
                        yield Block.box(0.0, 13.0, 0.0, 0.1, 16.0, 16.0);
                    default:
                        throw new IncompatibleClassChangeError();
                }
            }
            case WEST -> {
                switch (fuState) {
                    case NORMAL:
                    case DOOR_BACK:
                        yield Block.box(15.9, 0.0, 0.0, 16.0, 16.0, 16.0);
                    case LEFT_OPEN:
                        yield Block.box(15.9, 0.0, 0.0, 16.0, 16.0, 3.0);
                    case RIGHT_OPEN:
                        yield Block.box(15.9, 0.0, 13.0, 16.0, 16.0, 16.0);
                    case TRAPDOOR_BOTTOM_OPEN:
                        yield Block.box(15.9, 0.0, 0.0, 16.0, 3.0, 16.0);
                    case TRAPDOOR_TOP_OPEN:
                        yield Block.box(15.9, 13.0, 0.0, 16.0, 16.0, 16.0);
                    default:
                        throw new IncompatibleClassChangeError();
                }
            }
            default -> Block.box(0.0, 0.0, 15.9, 16.0, 16.0, 16.0);
        };
    }

    public FuCharacterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(WATERLOGGED, false))
                .setValue(FU_STATE, FuCharacterBlock.FuState.NORMAL)
        );
    }

    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING, WATERLOGGED, FU_STATE});
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return makeShape((Direction)state.getValue(FACING), (FuCharacterBlock.FuState)state.getValue(FU_STATE));
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return this.handleInteraction(state, level, pos, player, hand, hit);
    }

    private InteractionResult handleInteraction(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Direction facing = (Direction)state.getValue(FACING);
        BlockPos behindPos = pos.relative(facing.getOpposite());
        BlockState behindState = level.getBlockState(behindPos);
        Block behindBlock = behindState.getBlock();
        if (behindBlock instanceof DoorBlock doorBlock) {
            if (!doorBlock.type().canOpenByHand()) {
                return InteractionResult.PASS;
            } else {
                doorBlock.setOpen(player, level, behindState, behindPos, !(Boolean)behindState.getValue(DoorBlock.OPEN));
                this.refreshFuModelState(state, level, pos, facing);
                player.swing(hand);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else if (behindBlock instanceof TrapDoorBlock) {
            BlockSetType trapDoorType = ((TrapDoorBlockAccessor)behindBlock).kaleidoscope_chinesefood$getType();
            if (!trapDoorType.canOpenByHand()) {
                return InteractionResult.PASS;
            } else {
                BlockState toggled = (BlockState)behindState.cycle(TrapDoorBlock.OPEN);
                level.setBlock(behindPos, toggled, 2);
                if ((Boolean)toggled.getValue(TrapDoorBlock.WATERLOGGED)) {
                    level.scheduleTick(behindPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
                }

                boolean opened = (Boolean)toggled.getValue(TrapDoorBlock.OPEN);
                level.playSound(
                    player,
                    behindPos,
                    opened ? trapDoorType.trapdoorOpen() : trapDoorType.trapdoorClose(),
                    SoundSource.BLOCKS,
                    1.0F,
                    level.getRandom().nextFloat() * 0.1F + 0.9F
                );
                level.gameEvent(player, opened ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, behindPos);
                this.refreshFuModelState(state, level, pos, facing);
                player.swing(hand);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    private void refreshFuModelState(BlockState state, Level level, BlockPos pos, Direction facing) {
        BlockState newState = this.updateFuModelState(state, level, pos, facing);
        if (newState != state) {
            level.setBlock(pos, newState, 3);
        }
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
            BlockState state = (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, clickedFace))
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            state = this.updateFuModelState(state, level, pos, clickedFace);
            return state.canSurvive(level, pos) ? state : null;
        }
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if ((Boolean)state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        Direction facing = (Direction)state.getValue(FACING);
        if (direction == facing.getOpposite()) {
            if (neighborState.isAir()) {
                return Blocks.AIR.defaultBlockState();
            }

            state = this.updateFuModelState(state, level, pos, facing);
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private BlockState updateFuModelState(BlockState state, LevelAccessor level, BlockPos pos, Direction facing) {
        BlockPos behindPos = pos.relative(facing.getOpposite());
        BlockState behindState = level.getBlockState(behindPos);
        if (behindState.getBlock() instanceof DoorBlock) {
            boolean isDoorOpen = (Boolean)behindState.getValue(DoorBlock.OPEN);
            DoorHingeSide doorHinge = (DoorHingeSide)behindState.getValue(DoorBlock.HINGE);
            Direction doorFacing = (Direction)behindState.getValue(DoorBlock.FACING);
            boolean isDoorFront = facing == doorFacing.getOpposite();
            boolean isDoorBack = facing == doorFacing;
            if (isDoorOpen) {
                if (isDoorBack) {
                    doorHinge = doorHinge == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
                }

                return doorHinge == DoorHingeSide.LEFT
                    ? (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.LEFT_OPEN)
                    : (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.RIGHT_OPEN);
            } else {
                return isDoorFront
                    ? (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.NORMAL)
                    : (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.DOOR_BACK);
            }
        } else if (behindState.getBlock() instanceof TrapDoorBlock) {
            boolean isTrapDoorOpen = (Boolean)behindState.getValue(TrapDoorBlock.OPEN);
            Half trapDoorHalf = (Half)behindState.getValue(TrapDoorBlock.HALF);
            Direction trapDoorFacing = (Direction)behindState.getValue(TrapDoorBlock.FACING);
            boolean isTrapDoorFront = facing == trapDoorFacing.getOpposite();
            boolean isTrapDoorBack = facing == trapDoorFacing;
            if (isTrapDoorOpen) {
                return isTrapDoorFront
                    ? (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.NORMAL)
                    : (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.DOOR_BACK);
            } else {
                return trapDoorHalf == Half.BOTTOM
                    ? (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.TRAPDOOR_BOTTOM_OPEN)
                    : (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.TRAPDOOR_TOP_OPEN);
            }
        } else {
            return (BlockState)state.setValue(FU_STATE, FuCharacterBlock.FuState.NORMAL);
        }
    }

    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = (Direction)state.getValue(FACING);
        BlockPos wallPos = pos.relative(facing.getOpposite());
        BlockState wallState = level.getBlockState(wallPos);
        return !(wallState.getBlock() instanceof DoorBlock) && !(wallState.getBlock() instanceof TrapDoorBlock)
            ? wallState.isFaceSturdy(level, wallPos, facing)
            : true;
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(
            Component.translatable("block.kaleidoscope_chinesefood.fu_character.desc")
                .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
        );
    }

    public static enum FuState implements StringRepresentable {
        NORMAL("normal"),
        LEFT_OPEN("left_open"),
        RIGHT_OPEN("right_open"),
        DOOR_BACK("door_back"),
        TRAPDOOR_BOTTOM_OPEN("trapdoor_bottom_open"),
        TRAPDOOR_TOP_OPEN("trapdoor_top_open");

        private final String name;

        private FuState(String name) {
            this.name = name;
        }

        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
