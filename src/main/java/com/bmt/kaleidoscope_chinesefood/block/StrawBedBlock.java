package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.world.StrawBedTracker;
import com.github.ysbbbbbb.kaleidoscopecookery.util.VoxelShapeUtils;
import com.mojang.datafixers.util.Either;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 稻草床（官方 1.1.14 新增）：一次性床，睡醒即消失，且不设置重生点。
 */
public class StrawBedBlock extends BedBlock {
    private static final VoxelShape FOOT_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    private static final VoxelShape HEAD_NORTH = Shapes.or(Block.box(0.0, 0.0, 8.0, 16.0, 4.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 8.0));
    private static final EnumMap<Direction, VoxelShape> HEAD_SHAPES = VoxelShapeUtils.horizontalShapes(HEAD_NORTH);

    public StrawBedBlock(Properties properties) {
        super(DyeColor.BROWN, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == BedPart.FOOT ? FOOT_SHAPE : HEAD_SHAPES.get(state.getValue(FACING));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.CONSUME;
        }
        if (state.getValue(PART) != BedPart.HEAD) {
            pos = pos.relative(state.getValue(FACING));
            state = level.getBlockState(pos);
            if (!state.is(this)) {
                return InteractionResult.CONSUME;
            }
        }
        if (!level.dimensionType().bedWorks()) {
            // 非床维度（下界/末地）使用直接消耗掉床
            removeBothHalves(level, pos, state, SoundEvents.GRASS_BREAK);
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(OCCUPIED)) {
            player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
            return InteractionResult.SUCCESS;
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        StrawBedTracker.beginSleepAttempt(serverPlayer, pos);
        Either<BedSleepingProblem, Unit> result = serverPlayer.startSleepInBed(pos);
        if (result.left().isPresent()) {
            StrawBedTracker.finishSleepAttempt(serverPlayer, false);
            Component message = result.left().get().getMessage();
            if (message != null) {
                player.displayClientMessage(message, true);
            }
        } else {
            level.setBlock(pos, state.setValue(OCCUPIED, true), 3);
            serverPlayer.awardStat(Stats.SLEEP_IN_BED);
            StrawBedTracker.finishSleepAttempt(serverPlayer, true);
        }
        return InteractionResult.SUCCESS;
    }

    public static void removeBothHalves(Level level, BlockPos headPos, BlockState headState, SoundEvent sound) {
        BlockPos footPos = headPos.relative(headState.getValue(FACING).getOpposite());
        level.destroyBlock(headPos, false);
        BlockState footState = level.getBlockState(footPos);
        if (footState.is(headState.getBlock()) && footState.hasProperty(BlockStateProperties.BED_PART)) {
            level.destroyBlock(footPos, false);
        }
        level.playSound(null, headPos, sound, SoundSource.BLOCKS, 0.9F, 1.0F);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.kaleidoscope_chinesefood.straw_bed.tooltip")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
