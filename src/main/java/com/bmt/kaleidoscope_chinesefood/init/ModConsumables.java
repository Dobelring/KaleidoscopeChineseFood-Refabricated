package com.bmt.kaleidoscope_chinesefood.init;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 26.1: food effects moved from FoodProperties to the Consumable component.
 * Each entry mirrors the effect the matching ModFoods property carried in 1.21.1.
 *
 * Effects are resolved through {@link BuiltInRegistries#MOB_EFFECT} by id (mirroring
 * the nether reference's KNConsumables) instead of by static field reference, because
 * a dependency mod's effect holder may still be null while our class is initializing.
 * Missing effects are skipped safely rather than producing a null-effect
 * {@link MobEffectInstance} (which would crash creative-tab item hashing).
 */
public class ModConsumables {
    public static final Consumable SICHUAN_WONTON = build(lavaSwim(9600));
    public static final Consumable WONTON_NOODLES = build(warmth(9600));
    public static final Consumable YANGROU_PAOMO = build(warmth(9600));
    public static final Consumable MAOCAI = build(lavaSwim(9600));
    public static final Consumable SEAWEED_EGG_DROP_SOUP = build(warmth(9600));
    public static final Consumable TOMATO_EGG_DROP_SOUP = build(warmth(9600));
    public static final Consumable DOUZHI = build(
            warmthEffect(9600),
            vanilla(MobEffects.NAUSEA, 100),
            vanilla(MobEffects.WEAKNESS, 100));
    public static final Consumable CENTURY_EGG_CONGEE = build(warmth(9600));
    public static final Consumable PUMPKIN_PORRIDGE = build(warmth(9600));
    public static final Consumable SICHUAN_BOILED_PORK_SLICES_ITEM = build(lavaSwim(9600));
    public static final Consumable SICHUAN_BOILED_PORK_SLICES_BLOCK = build(lavaSwim(9600));
    public static final Consumable SICHUAN_BOILED_FISH_ITEM = build(lavaSwim(9600));
    public static final Consumable SICHUAN_BOILED_FISH_BLOCK = build(lavaSwim(9600));
    public static final Consumable TWICE_COOKED_PORK = build(vigor(1800));
    public static final Consumable TWICE_COOKED_PORK_RICE = build(satiatedShield(3600));
    public static final Consumable STIR_FRIED_YELLOW_BEEF = build(vigor(1800));
    public static final Consumable STIR_FRIED_YELLOW_BEEF_RICE = build(satiatedShield(3600));
    public static final Consumable BEEF_WITH_SCRAMBLED_EGGS = build(vigor(1800));
    public static final Consumable BEEF_WITH_SCRAMBLED_EGGS_RICE = build(satiatedShield(3600));
    public static final Consumable STIR_FRIED_THREE_FRESH_VEGETABLES = build(vigor(1800));
    public static final Consumable STIR_FRIED_THREE_FRESH_VEGETABLES_RICE = build(satiatedShield(3600));
    public static final Consumable BIG_PLATE_CHICKEN = build(vigor(1800));
    public static final Consumable BIG_PLATE_CHICKEN_NOODLES = build(satiatedShield(3600));
    public static final Consumable TOMATO_EGG_NOODLES = build(satiatedShield(3600));
    public static final Consumable PORK_CHILI_NOODLES = build(satiatedShield(3600));
    public static final Consumable FOUR_JOY_MEATBALLS = build(satiatedShield(3600));
    public static final Consumable STUFFED_EGGPLANT = build(warmth(3600));
    public static final Consumable DRY_POT_POTATOES = build(saturationShield(3600));
    public static final Consumable DRY_POT_CHICKEN = build(saturationShield(3600));
    public static final Consumable DRY_POT_SPARE_RIBS = build(saturationShield(3600));
    public static final Consumable YANGZHOU_FRIED_RICE = build(warmth(9600));
    public static final Consumable LAMB_PILAF = build(warmth(9600));
    public static final Consumable STEAMED_RICE_ROLLS = build(vigor(1800));
    public static final Consumable RED_RICE_ROLL_ITEM = build(vigor(2400));
    public static final Consumable RED_RICE_ROLL_BLOCK = build(vigor(1800));
    public static final Consumable SAUERKRAUT_BEEF_NOODLES = build(warmth(9600));
    public static final Consumable YELLOW_CROAKER_TOFU_SOUP_ITEM = build(warmth(9600));
    public static final Consumable YELLOW_CROAKER_TOFU_SOUP_BLOCK = build(warmth(9600));
    public static final Consumable FROZEN_BUN = build(vanilla(MobEffects.FIRE_RESISTANCE, 3600));
    public static final Consumable YELLOW_CROAKER_SOUP_ITEM = build(warmth(9600));
    public static final Consumable YELLOW_CROAKER_SOUP_BLOCK = build(warmth(9600));

    private static Consumable build(MobEffectInstance... effects) {
        Consumable.Builder builder = Consumables.defaultFood();
        List<MobEffectInstance> instances = Arrays.stream(effects)
                .filter(Objects::nonNull)
                .toList();
        if (!instances.isEmpty()) {
            builder.onConsume(new ApplyStatusEffectsConsumeEffect(instances, 1.0F));
        }
        return builder.build();
    }

    @Nullable
    private static MobEffectInstance vanilla(Holder<MobEffect> holder, int duration) {
        if (holder == null) {
            return null;
        }
        return new MobEffectInstance(holder, duration, 0);
    }

    @Nullable
    private static MobEffectInstance lavaSwim(int duration) {
        return createEffect("kaleidoscope_chinesefood", "lava_swim", duration);
    }

    @Nullable
    private static MobEffectInstance saturationShield(int duration) {
        return createEffect("kaleidoscope_chinesefood", "saturation_shield", duration);
    }

    // cookery-provided effects
    @Nullable
    private static MobEffectInstance warmth(int duration) {
        return createEffect("kaleidoscope_cookery", "warmth", duration);
    }

    @Nullable
    private static MobEffectInstance vigor(int duration) {
        return createEffect("kaleidoscope_cookery", "vigor", duration);
    }

    @Nullable
    private static MobEffectInstance satiatedShield(int duration) {
        return createEffect("kaleidoscope_cookery", "satiated_shield", duration);
    }

    @Nullable
    private static MobEffectInstance warmthEffect(int duration) {
        return createEffect("kaleidoscope_cookery", "warmth", duration);
    }

    @Nullable
    private static MobEffectInstance createEffect(String namespace, String path, int duration) {
        Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
        return BuiltInRegistries.MOB_EFFECT.get(id)
                .map(effect -> new MobEffectInstance(effect, duration, 0))
                .orElse(null);
    }
}
