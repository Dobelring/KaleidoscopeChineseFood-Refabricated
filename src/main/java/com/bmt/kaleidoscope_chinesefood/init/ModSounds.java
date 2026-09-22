package com.bmt.kaleidoscope_chinesefood.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static final SoundEvent FREEZER_OPEN = SoundEvent.createVariableRangeEvent(
        new ResourceLocation("kaleidoscope_chinesefood", "freezer_open")
    );
    public static final SoundEvent FREEZER_CLOSE = SoundEvent.createVariableRangeEvent(
        new ResourceLocation("kaleidoscope_chinesefood", "freezer_close")
    );

    public ModSounds() {
    }

    public static void registerSounds() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation("kaleidoscope_chinesefood", "freezer_open"), FREEZER_OPEN);
        Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation("kaleidoscope_chinesefood", "freezer_close"), FREEZER_CLOSE);
    }
}
