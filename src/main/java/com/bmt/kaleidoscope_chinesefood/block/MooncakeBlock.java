package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.util.forge.ItemHandlerHelper;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MooncakeBlock extends Block {
    public static final IntegerProperty STACK_COUNT = IntegerProperty.create("stack_count", 0, 4);
    private static final VoxelShape MOONCAKE_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0);

    public MooncakeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STACK_COUNT, 0));
    }

    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STACK_COUNT});
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (hand == InteractionHand.MAIN_HAND && heldItem.is(ModItems.MOONCAKE)) {
            int count = (Integer)state.getValue(STACK_COUNT);
            if (count < 4) {
                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }

                level.setBlockAndUpdate(pos, (BlockState)state.setValue(STACK_COUNT, count + 1));
                level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.PASS;
            }
        } else if (hand == InteractionHand.MAIN_HAND && heldItem.isEmpty() && player.getOffhandItem().isEmpty()) {
            if (!level.isClientSide) {
                int currentStack = (Integer)state.getValue(STACK_COUNT);
                ItemStack mooncake = new ItemStack(ModItems.MOONCAKE);
                if (currentStack > 0) {
                    level.setBlock(pos, (BlockState)state.setValue(STACK_COUNT, currentStack - 1), 3);
                } else {
                    level.removeBlock(pos, false);
                }

                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.9F, 1.0F);
                ItemHandlerHelper.giveItemToPlayer(player, mooncake);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        List<ItemStack> drops = Lists.newArrayList();
        int realAmount = (Integer)state.getValue(STACK_COUNT) + 1;
        drops.add(new ItemStack(ModItems.MOONCAKE, realAmount));
        return drops;
    }

    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.MOONCAKE);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return MOONCAKE_SHAPE;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getShape(state, level, pos, context);
    }
}
