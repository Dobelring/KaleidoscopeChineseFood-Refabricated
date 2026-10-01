package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class ModStats {
    public static final Identifier SLEEP_IN_STRAW_BED = KaleidoscopeChineseFood.id("sleep_in_straw_bed");

    public static void register() {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, SLEEP_IN_STRAW_BED, SLEEP_IN_STRAW_BED);
    }

    public static void awardSleepStat(ServerPlayer player) {
        player.awardStat(Stats.CUSTOM.get(SLEEP_IN_STRAW_BED, StatFormatter.DEFAULT));
    }
}
