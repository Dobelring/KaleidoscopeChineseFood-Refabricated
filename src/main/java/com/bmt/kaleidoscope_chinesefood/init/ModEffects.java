package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.effect.LavaSwimEffect;
import com.bmt.kaleidoscope_chinesefood.effect.SaturationShieldEffect;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;

public class ModEffects {
    public static Holder<MobEffect> LAVA_SWIM;
    public static Holder<MobEffect> SATURATION_SHIELD;

    public static void register() {
        LAVA_SWIM = register("lava_swim", LavaSwimEffect::new);
        SATURATION_SHIELD = register("saturation_shield", SaturationShieldEffect::new);
    }

    private static Holder<MobEffect> register(String name, Supplier<MobEffect> supplier) {
        MobEffect effect = Registry.register(BuiltInRegistries.MOB_EFFECT, KaleidoscopeChineseFood.id(name), supplier.get());
        return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
    }
}
