package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.BowlStackBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BowlStackBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {
    public static final IntegerProperty BOWL_COUNT = IntegerProperty.create("bowl_count", 0, 3);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty IS_WOODEN = BooleanProperty.create("is_wooden");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(2.0, 0.0, 1.0, 14.0, 5.0, 15.0);
    private static final VoxelShape SHAPE_EAST_WEST = Block.box(1.0, 0.0, 2.0, 15.0, 5.0, 14.0);

    public BowlStackBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
            (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(BOWL_COUNT, 0)).setValue(FACING, Direction.NORTH))
                    .setValue(IS_WOODEN, false))
                .setValue(WATERLOGGED, false)
        );
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BowlStackBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BowlStackBlockEntity bowlStackBE) {
                BowlStackBlockEntity.tick(level1, pos, state1, bowlStackBE);
            }
        };
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof BowlStackBlockEntity bowlStackBE) {
            bowlStackBE.initializeFromBlockState(state);
        }
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()))
            .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if ((Boolean)state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = (Direction)state.getValue(FACING);
        return facing != Direction.NORTH && facing != Direction.SOUTH ? SHAPE_EAST_WEST : SHAPE_NORTH_SOUTH;
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        } else {
            ItemStack heldItem = player.getItemInHand(hand);
            int bowlCount = (Integer)state.getValue(BOWL_COUNT);
            if (!level.isClientSide) {
                BowlStackBlockEntity be = this.getBlockEntity(level, pos);
                if (be == null) {
                    return InteractionResult.PASS;
                } else if (heldItem.isEmpty() && player.isCrouching()) {
                    level.setBlock(pos, (BlockState)state.setValue(IS_WOODEN, !(Boolean)state.getValue(IS_WOODEN)), 3);
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.CONSUME;
                } else if (heldItem.is(Items.BOWL) && bowlCount < 3) {
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    be.addBowl();
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return InteractionResult.CONSUME;
                } else if (heldItem.isEmpty() && bowlCount > 0) {
                    ItemStack bowl = be.removeBowl();
                    if (!bowl.isEmpty()) {
                        player.addItem(bowl);
                    }

                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.8F, 1.0F);
                    return InteractionResult.CONSUME;
                } else {
                    return InteractionResult.PASS;
                }
            } else {
                return (!heldItem.isEmpty() || !player.isCrouching())
                        && (!heldItem.is(Items.BOWL) || bowlCount >= 3)
                        && (!heldItem.isEmpty() || bowlCount <= 0)
                    ? InteractionResult.PASS
                    : InteractionResult.SUCCESS;
            }
        }
    }

    public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder paramsBuilder) {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this));
        int bowlCount = (Integer)state.getValue(BOWL_COUNT);
        if (bowlCount > 0) {
            drops.add(new ItemStack(Items.BOWL, bowlCount));
        }

        return drops;
    }

    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (player.isCreative() && !level.isClientSide) {
            int bowlCount = (Integer)state.getValue(BOWL_COUNT);
            if (bowlCount > 0) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.BOWL, bowlCount));
            }
        }

        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(
            Component.translatable("block.kaleidoscope_chinesefood.bowl_stack.desc").withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
        );
    }

    @Nullable
    private BowlStackBlockEntity getBlockEntity(Level level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof BowlStackBlockEntity ? (BowlStackBlockEntity)be : null;
    }

    public boolean canBeReplaced(BlockState state, Fluid fluid) {
        return !(Boolean)state.getValue(WATERLOGGED) && fluid == Fluids.WATER;
    }

    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(new Property[]{BOWL_COUNT, FACING, IS_WOODEN, WATERLOGGED});
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return (Integer)state.getValue(BOWL_COUNT) * 5;
    }

    @NotNull
    public BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot) {
        return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
    }

    @NotNull
    public BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
    }
}
