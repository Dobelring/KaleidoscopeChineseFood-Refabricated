package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.world.StrawBedTracker;
import com.github.ysbbbbbb.kaleidoscopecookery.util.VoxelShapeUtils;
import com.mojang.datafixers.util.Either;
import java.util.EnumMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Player.BedSleepingProblem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
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
 * 26.x 适配：tooltip 移至物品侧（{@link com.bmt.kaleidoscope_chinesefood.item.StrawBedItem}）、
 * dimensionType().bedWorks() 改为 EnvironmentAttributes.BED_RULE。
 */
public class StrawBedBlock extends BedBlock {
    private static final VoxelShape FOOT_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    private static final VoxelShape HEAD_NORTH = Shapes.or(Block.box(0.0, 0.0, 8.0, 16.0, 4.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 8.0));
    private static final EnumMap<Direction, VoxelShape> HEAD_SHAPES = VoxelShapeUtils.horizontalShapes(HEAD_NORTH);

    public StrawBedBlock(Properties properties) {
        super(DyeColor.BROWN, properties);
    }

    @Override
    @Nullable
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.CONSUME;
        }
        if (state.getValue(PART) != BedPart.HEAD) {
            pos = pos.relative(state.getValue(FACING));
            state = level.getBlockState(pos);
            if (!state.is(this)) {
                return InteractionResult.CONSUME;
            }
        }
        BedRule bedRule = level.environmentAttributes().getValue(EnvironmentAttributes.BED_RULE, pos);
        if (!bedRule.canSleep(level) && !bedRule.canSetSpawn(level)) {
            // 不可睡也不可设重生点的维度（下界/末地，等价旧版 dimensionType().bedWorks()==false）：直接消耗掉床
            removeBothHalves(level, pos, state, SoundEvents.GRASS_BREAK);
            return InteractionResult.SUCCESS;
        }
        if (state.getValue(OCCUPIED)) {
            player.sendOverlayMessage(Component.translatable("block.minecraft.bed.occupied"));
            return InteractionResult.SUCCESS;
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        StrawBedTracker.beginSleepAttempt(serverPlayer, pos);
        Either<BedSleepingProblem, Unit> result = serverPlayer.startSleepInBed(pos);
        if (result.left().isPresent()) {
            StrawBedTracker.finishSleepAttempt(serverPlayer, false);
            Component message = ((BedSleepingProblem) result.left().get()).message();
            if (message != null) {
                player.sendOverlayMessage(message);
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
}
