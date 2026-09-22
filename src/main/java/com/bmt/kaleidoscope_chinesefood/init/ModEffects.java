package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.effect.LavaSwimEffect;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {
    public static final MobEffect LAVA_SWIM = new LavaSwimEffect();

    public ModEffects() {
    }

    public static void registerEffects() {
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation("kaleidoscope_chinesefood", "lava_swim"), LAVA_SWIM);
    }
}
