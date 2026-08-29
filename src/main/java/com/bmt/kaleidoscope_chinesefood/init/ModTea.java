package com.bmt.kaleidoscope_chinesefood.init;

import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry.TeacupData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;

public class ModTea {
    public static Identifier LAPSANG;
    public static Identifier HK_MILK_TEA;

    public static void init() {
        LAPSANG = id("lapsang");
        TeacupRegistry.TEACUP_DATA_MAP.put(
                LAPSANG,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.TUNDRA_STRIDER, 9600), 1.0F)
        );
        HK_MILK_TEA = id("hk_milk_tea");
        TeacupRegistry.TEACUP_DATA_MAP.put(
                HK_MILK_TEA,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SULFUR, 9600), 1.0F)
        );
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", name);
    }
}
