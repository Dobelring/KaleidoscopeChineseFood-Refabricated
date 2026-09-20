package com.bmt.kaleidoscope_chinesefood.init;

import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry.TeacupData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public class ModTea {
    public static ResourceLocation DIANHONG_TEA;
    public static ResourceLocation HK_MILK_TEA;

    public static void init() {
        DIANHONG_TEA = id("dianhong_tea");
        TeacupRegistry.TEACUP_DATA_MAP.put(
                DIANHONG_TEA,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.TUNDRA_STRIDER, 9600), 1.0F)
        );
        HK_MILK_TEA = id("hk_milk_tea");
        TeacupRegistry.TEACUP_DATA_MAP.put(
                HK_MILK_TEA,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.HINDER, 9600), 1.0F)
        );
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", name);
    }
}
