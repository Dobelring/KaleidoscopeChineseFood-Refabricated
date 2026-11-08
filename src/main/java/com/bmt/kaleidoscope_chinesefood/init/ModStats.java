package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

/**
 * 自定义统计。1.2.0 新增 open_freezer（开冰箱计数，进度用）。
 * sleep_in_straw_bed 为本支（26.2）自有长凳睡觉统计，保留。
 */
public class ModStats {
    public static final Identifier SLEEP_IN_STRAW_BED = KaleidoscopeChineseFood.id("sleep_in_straw_bed");
    public static final Identifier OPEN_FREEZER = KaleidoscopeChineseFood.id("open_freezer");

    public static void register() {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, SLEEP_IN_STRAW_BED, SLEEP_IN_STRAW_BED);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, OPEN_FREEZER, OPEN_FREEZER);
    }

    public static void awardSleepStat(ServerPlayer player) {
        player.awardStat(Stats.CUSTOM.get(SLEEP_IN_STRAW_BED, StatFormatter.DEFAULT));
    }

    public static void awardOpenFreezerStat(ServerPlayer player) {
        player.awardStat(Stats.CUSTOM.get(OPEN_FREEZER, StatFormatter.DEFAULT));
    }
}
