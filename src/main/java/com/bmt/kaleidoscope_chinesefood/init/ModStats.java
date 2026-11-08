package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class ModStats {
    public static final ResourceLocation SLEEP_IN_STRAW_BED = KaleidoscopeChineseFood.id("sleep_in_straw_bed");
    public static final ResourceLocation OPEN_FREEZER = KaleidoscopeChineseFood.id("open_freezer");

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
