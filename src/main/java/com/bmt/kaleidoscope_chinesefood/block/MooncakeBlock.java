package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.util.forge.ItemHandlerHelper;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;
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
    private static final VoxelShape MOONCAKE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0);

    public MooncakeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STACK_COUNT, 0));
    }

    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STACK_COUNT});
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 1.2.0：只响应顶面蛋糕区域的点击，其余位置穿透交互
        if (!isClickOnCake(pos, hit.getLocation())) {
            return InteractionResult.PASS;
        }
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
        } else if (hand == InteractionHand.MAIN_HAND && heldItem.isEmpty()) {
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, java.util.List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("block.kaleidoscope_chinesefood.mooncake.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    /** 1.2.0：点击是否落在月饼顶面（x/z ∈ [0.124,0.876]，y ≤ 0.0635）。 */
    public static boolean isClickOnCake(BlockPos pos, net.minecraft.world.phys.Vec3 clickLoc) {
        double x = clickLoc.x - pos.getX();
        double y = clickLoc.y - pos.getY();
        double z = clickLoc.z - pos.getZ();
        double eps = 0.001;
        return x >= 0.124 && x <= 0.876 && y >= -eps && y <= 0.0635 && z >= 0.124 && z <= 0.876;
    }
}
