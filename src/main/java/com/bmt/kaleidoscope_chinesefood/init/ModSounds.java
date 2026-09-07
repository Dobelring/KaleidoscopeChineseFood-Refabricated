package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static SoundEvent FREEZER_OPEN;
    public static SoundEvent FREEZER_CLOSE;
    public static SoundEvent DUCK_TOY;

    public static void register() {
        FREEZER_OPEN = register("freezer_open");
        FREEZER_CLOSE = register("freezer_close");
        DUCK_TOY = register("duck_toy");
    }

    private static SoundEvent register(String name) {
        Identifier id = KaleidoscopeChineseFood.id(name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
}
