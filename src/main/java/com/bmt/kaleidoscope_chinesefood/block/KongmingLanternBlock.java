package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class KongmingLanternBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public KongmingLanternBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.stateDefinition.any());
    }

    // TODO(fabric): Forge 的 FMLCommonSetupEvent 无对应物，改为无参静态方法，需由主类 onInitialize() 调用
    public static void onCommonSetup() {
        Item item = ModBlocks.KONGMING_LANTERN.asItem();
        DispenserBlock.registerBehavior(item, new DispenseItemBehavior() {
            public ItemStack dispense(BlockSource source, ItemStack stack) {
                Level level = source.getLevel();
                Direction direction = (Direction)source.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos pos = source.getPos().relative(direction);
                KongmingLanternEntity lantern = (KongmingLanternEntity)ModEntities.KONGMING_LANTERN.create(level);
                if (lantern != null) {
                    lantern.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    lantern.setDeltaMovement(direction.getStepX() * 0.3, 0.1, direction.getStepZ() * 0.3);
                    level.addFreshEntity(lantern);
                    level.levelEvent(null, 1009, pos, 0);
                    stack.shrink(1);
                }

                return stack;
            }
        });
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof FlintAndSteelItem) {
                KongmingLanternEntity lantern = (KongmingLanternEntity)ModEntities.KONGMING_LANTERN.create(level);
                if (lantern != null) {
                    lantern.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    level.addFreshEntity(lantern);
                    level.removeBlock(pos, false);
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    level.levelEvent(null, 1009, pos, 0);
                }

                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(
            Component.translatable("item.kaleidoscope_chinesefood.kongming_lantern.tooltip")
                .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
        );
    }

    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
    }
}
