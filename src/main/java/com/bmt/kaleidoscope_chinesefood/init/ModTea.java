package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry.TeacupData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public class ModTea {
    public static ResourceLocation DIANHONG_TEA;
    public static ResourceLocation HK_MILK_TEA;

    public ModTea() {
    }

    public static void init() {
        // 注意：厨艺 Fabric 版的 TeacupRegistry 是 final 且构造器 private，无法像 Forge 版那样
        // 匿名子类覆写 registerTeacupData(String, ...) 来改命名空间；这里改用
        // registerTeacupData(ResourceLocation, ...) 直接传本模组的 id，效果与 Forge 版一致
        TeacupRegistry registry = TeacupRegistry.INSTANCE;
        DIANHONG_TEA = registry.registerTeacupData(
            KaleidoscopeChineseFood.id("dianhong_tea"),
            TeacupData.create(4)
                .addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.TUNDRA_STRIDER.get(), 9600), 1.0F)
        );
        HK_MILK_TEA = registry.registerTeacupData(
            KaleidoscopeChineseFood.id("hk_milk_tea"),
            TeacupData.create(4)
                .addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.HINDER.get(), 9600), 1.0F)
        );
    }
}
