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
 * sleep_in_straw_bed 在 26.x 由厨房（cookery）自带，不再属于本模组。
 */
public class ModStats {
    public static final Identifier OPEN_FREEZER = KaleidoscopeChineseFood.id("open_freezer");

    public static void register() {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, OPEN_FREEZER, OPEN_FREEZER);
    }

    public static void awardOpenFreezerStat(ServerPlayer player) {
        player.awardStat(Stats.CUSTOM.get(OPEN_FREEZER, StatFormatter.DEFAULT));
    }
}
