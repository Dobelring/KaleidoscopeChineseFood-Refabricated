package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.util.forge.ItemHandlerHelper;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SaltBlock extends Block {
    public static final IntegerProperty STACK_COUNT = IntegerProperty.create("stack_count", 0, 3);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE_0_NORTH = Block.box(6.0, 0.0, 6.0, 10.0, 7.0, 10.0);
    private static final VoxelShape SHAPE_0_EAST = SHAPE_0_NORTH;
    private static final VoxelShape SHAPE_0_SOUTH = SHAPE_0_NORTH;
    private static final VoxelShape SHAPE_0_WEST = SHAPE_0_NORTH;
    private static final VoxelShape SHAPE_1_NORTH = Block.box(3.0, 0.0, 6.0, 13.0, 7.0, 10.0);
    private static final VoxelShape SHAPE_1_EAST = Block.box(6.0, 0.0, 3.0, 10.0, 7.0, 13.0);
    private static final VoxelShape SHAPE_1_SOUTH = SHAPE_1_NORTH;
    private static final VoxelShape SHAPE_1_WEST = SHAPE_1_EAST;
    private static final VoxelShape SHAPE_2_PART1_NORTH = Block.box(3.0, 0.0, 3.0, 13.0, 7.0, 7.0);
    private static final VoxelShape SHAPE_2_PART2_NORTH = Block.box(6.0, 0.0, 7.0, 10.0, 7.0, 13.0);
    private static final VoxelShape SHAPE_2_NORTH = Shapes.or(SHAPE_2_PART1_NORTH, SHAPE_2_PART2_NORTH);
    private static final VoxelShape SHAPE_2_PART1_EAST = Block.box(9.0, 0.0, 3.0, 13.0, 7.0, 13.0);
    private static final VoxelShape SHAPE_2_PART2_EAST = Block.box(3.0, 0.0, 6.0, 9.0, 7.0, 10.0);
    private static final VoxelShape SHAPE_2_EAST = Shapes.or(SHAPE_2_PART1_EAST, SHAPE_2_PART2_EAST);
    private static final VoxelShape SHAPE_2_PART1_SOUTH = Block.box(3.0, 0.0, 9.0, 13.0, 7.0, 13.0);
    private static final VoxelShape SHAPE_2_PART2_SOUTH = Block.box(6.0, 0.0, 3.0, 10.0, 7.0, 9.0);
    private static final VoxelShape SHAPE_2_SOUTH = Shapes.or(SHAPE_2_PART1_SOUTH, SHAPE_2_PART2_SOUTH);
    private static final VoxelShape SHAPE_2_PART1_WEST = Block.box(3.0, 0.0, 3.0, 7.0, 7.0, 13.0);
    private static final VoxelShape SHAPE_2_PART2_WEST = Block.box(7.0, 0.0, 6.0, 13.0, 7.0, 10.0);
    private static final VoxelShape SHAPE_2_WEST = Shapes.or(SHAPE_2_PART1_WEST, SHAPE_2_PART2_WEST);
    private static final VoxelShape SHAPE_3_NORTH = Block.box(3.0, 0.0, 3.0, 13.0, 7.0, 13.0);
    private static final VoxelShape SHAPE_3_EAST = SHAPE_3_NORTH;
    private static final VoxelShape SHAPE_3_SOUTH = SHAPE_3_NORTH;
    private static final VoxelShape SHAPE_3_WEST = SHAPE_3_NORTH;

    public SaltBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(STACK_COUNT, 0)).setValue(FACING, Direction.SOUTH));
    }

    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STACK_COUNT, FACING});
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack heldItem = context.getItemInHand();
        return heldItem.getItem() instanceof BlockItem blockItem
            && blockItem.getBlock() == ModBlocks.SALT_BLOCK
            && (Integer)state.getValue(STACK_COUNT) < 3;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState clickedState = context.getLevel().getBlockState(context.getClickedPos());
        return clickedState.is(this)
            ? (BlockState)clickedState.setValue(STACK_COUNT, (Integer)clickedState.getValue(STACK_COUNT) + 1)
            : (BlockState)super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND && heldItem.isEmpty()) {
            if (!level.isClientSide) {
                int currentStack = (Integer)state.getValue(STACK_COUNT);
                ItemStack salt = new ItemStack(ModBlocks.SALT_BLOCK);
                if (currentStack > 0) {
                    level.setBlock(pos, (BlockState)state.setValue(STACK_COUNT, currentStack - 1), 3);
                } else {
                    level.removeBlock(pos, false);
                }

                ItemHandlerHelper.giveItemToPlayer(player, salt);
            }

            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.9F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        List<ItemStack> drops = Lists.newArrayList();
        int realAmount = (Integer)state.getValue(STACK_COUNT) + 1;
        drops.add(new ItemStack(ModBlocks.SALT_BLOCK, realAmount));
        return drops;
    }

    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(
            Component.translatable("item.kaleidoscope_chinesefood.salt.tooltip").withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
        );
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int stack = (Integer)state.getValue(STACK_COUNT);
        Direction facing = (Direction)state.getValue(FACING);

        return switch (stack) {
            case 0 -> {
                switch (facing) {
                    case NORTH:
                        yield SHAPE_0_NORTH;
                    case EAST:
                        yield SHAPE_0_EAST;
                    case SOUTH:
                        yield SHAPE_0_SOUTH;
                    case WEST:
                        yield SHAPE_0_WEST;
                    default:
                        yield SHAPE_0_SOUTH;
                }
            }
            case 1 -> {
                switch (facing) {
                    case NORTH:
                        yield SHAPE_1_NORTH;
                    case EAST:
                        yield SHAPE_1_EAST;
                    case SOUTH:
                        yield SHAPE_1_SOUTH;
                    case WEST:
                        yield SHAPE_1_WEST;
                    default:
                        yield SHAPE_1_SOUTH;
                }
            }
            case 2 -> {
                switch (facing) {
                    case NORTH:
                        yield SHAPE_2_NORTH;
                    case EAST:
                        yield SHAPE_2_EAST;
                    case SOUTH:
                        yield SHAPE_2_SOUTH;
                    case WEST:
                        yield SHAPE_2_WEST;
                    default:
                        yield SHAPE_2_SOUTH;
                }
            }
            case 3 -> {
                switch (facing) {
                    case NORTH:
                        yield SHAPE_3_NORTH;
                    case EAST:
                        yield SHAPE_3_EAST;
                    case SOUTH:
                        yield SHAPE_3_SOUTH;
                    case WEST:
                        yield SHAPE_3_WEST;
                    default:
                        yield SHAPE_3_SOUTH;
                }
            }
            default -> Shapes.empty();
        };
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
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
