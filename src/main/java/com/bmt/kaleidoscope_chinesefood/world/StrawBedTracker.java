package com.bmt.kaleidoscope_chinesefood.world;

import com.bmt.kaleidoscope_chinesefood.block.StrawBedBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModStats;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

/**
 * 稻草床睡眠状态跟踪：PENDING 记录进行中的入睡尝试（用于拦截重生点写入），
 * ACTIVE 记录睡成功的床（醒来时销毁两半并记统计）。
 */
public final class StrawBedTracker {
    private static final Map<UUID, BlockPos> PENDING_BEDS = new ConcurrentHashMap<>();
    private static final Map<UUID, BlockPos> ACTIVE_BEDS = new ConcurrentHashMap<>();

    private StrawBedTracker() {
    }

    public static void beginSleepAttempt(ServerPlayer player, BlockPos headPos) {
        PENDING_BEDS.put(player.getUUID(), headPos.immutable());
    }

    public static void finishSleepAttempt(ServerPlayer player, boolean success) {
        BlockPos pos = PENDING_BEDS.remove(player.getUUID());
        if (success && pos != null) {
            ACTIVE_BEDS.put(player.getUUID(), pos);
        }
    }

    public static boolean shouldCancelSpawnSet(Player player) {
        return PENDING_BEDS.containsKey(player.getUUID());
    }

    public static void clear(Player player) {
        PENDING_BEDS.remove(player.getUUID());
        ACTIVE_BEDS.remove(player.getUUID());
    }

    public static void onWake(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            BlockPos pos = ACTIVE_BEDS.remove(serverPlayer.getUUID());
            if (pos != null) {
                Level level = serverPlayer.level();
                BlockState state = level.getBlockState(pos);
                if (state.is(ModBlocks.STRAW_BED)) {
                    if (state.getValue(StrawBedBlock.PART) == BedPart.FOOT) {
                        pos = pos.relative(state.getValue(StrawBedBlock.FACING));
                        state = level.getBlockState(pos);
                    }
                    if (state.is(ModBlocks.STRAW_BED)) {
                        StrawBedBlock.removeBothHalves(level, pos, state, SoundEvents.GRASS_BREAK);
                        ModStats.awardSleepStat(serverPlayer);
                    }
                }
            }
        }
    }
}
