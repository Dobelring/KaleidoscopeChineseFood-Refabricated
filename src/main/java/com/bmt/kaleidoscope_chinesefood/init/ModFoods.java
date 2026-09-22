package com.bmt.kaleidoscope_chinesefood.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;

public class ModFoods {
    public static final FoodProperties SICHUAN_WONTON = new Builder()
        .nutrition(14)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(ModEffects.LAVA_SWIM, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties WONTON_NOODLES = new Builder()
        .nutrition(14)
        .saturationMod(0.66F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.WARMTH.get(), 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties YANGROU_PAOMO = new Builder()
        .nutrition(14)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.WARMTH.get(), 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties MAOCAI = new Builder()
        .nutrition(6)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(ModEffects.LAVA_SWIM, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties SEAWEED_EGG_DROP_SOUP = new Builder()
        .nutrition(6)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.WARMTH.get(), 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties DOUZHI = new Builder()
        .nutrition(6)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.WARMTH.get(), 9600), 1.0F)
        .effect(new MobEffectInstance(MobEffects.CONFUSION, 100), 1.0F)
        .effect(new MobEffectInstance(MobEffects.WEAKNESS, 100), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties TOMATO_EGG_DROP_SOUP = new Builder()
        .nutrition(6)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.WARMTH.get(), 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties CENTURY_EGG_CONGEE = new Builder()
        .nutrition(6)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.WARMTH.get(), 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties PUMPKIN_PORRIDGE = new Builder()
        .nutrition(6)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.WARMTH.get(), 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties SICHUAN_BOILED_PORK_SLICES_ITEM = new Builder()
        .nutrition(13)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(ModEffects.LAVA_SWIM, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties SICHUAN_BOILED_PORK_SLICES_BLOCK = new Builder()
        .nutrition(3)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(ModEffects.LAVA_SWIM, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties SICHUAN_BOILED_FISH_ITEM = new Builder()
        .nutrition(13)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(ModEffects.LAVA_SWIM, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties SICHUAN_BOILED_FISH_BLOCK = new Builder()
        .nutrition(3)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(ModEffects.LAVA_SWIM, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties TWICE_COOKED_PORK = new Builder()
        .nutrition(9)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties TWICE_COOKED_PORK_RICE = new Builder()
        .nutrition(14)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SATIATED_SHIELD.get(), 3600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties STIR_FRIED_YELLOW_BEEF = new Builder()
        .nutrition(9)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties STIR_FRIED_YELLOW_BEEF_RICE = new Builder()
        .nutrition(14)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SATIATED_SHIELD.get(), 3600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties BEEF_WITH_SCRAMBLED_EGGS = new Builder()
        .nutrition(9)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties BEEF_WITH_SCRAMBLED_EGGS_RICE = new Builder()
        .nutrition(14)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SATIATED_SHIELD.get(), 3600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties STIR_FRIED_THREE_FRESH_VEGETABLES = new Builder()
        .nutrition(9)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties STIR_FRIED_THREE_FRESH_VEGETABLES_RICE = new Builder()
        .nutrition(14)
        .saturationMod(0.7F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SATIATED_SHIELD.get(), 3600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties BIG_PLATE_CHICKEN = new Builder()
        .nutrition(9)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties BIG_PLATE_CHICKEN_NOODLES = new Builder()
        .nutrition(14)
        .saturationMod(0.7F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SATIATED_SHIELD.get(), 3600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties TOMATO_EGG_NOODLES = new Builder()
        .nutrition(14)
        .saturationMod(0.7F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SATIATED_SHIELD.get(), 3600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties PORK_CHILI_NOODLES = new Builder()
        .nutrition(14)
        .saturationMod(0.7F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SATIATED_SHIELD.get(), 3600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties FOUR_JOY_MEATBALLS_ITEM = new Builder()
        .nutrition(13)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties FOUR_JOY_MEATBALLS_BLOCK = new Builder()
        .nutrition(3)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties DRY_POT_POTATOES = new Builder()
        .nutrition(13)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(MobEffects.DIG_SPEED, 6000, 1), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties DRY_POT_CHICKEN = new Builder()
        .nutrition(13)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 1), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties DRY_POT_SPARE_RIBS = new Builder()
        .nutrition(13)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 6000, 1), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties YANGZHOU_FRIED_RICE = new Builder()
        .nutrition(12)
        .saturationMod(0.66F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SATIATED_SHIELD.get(), 1600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties LAMB_PILAF = new Builder()
        .nutrition(14)
        .saturationMod(0.66F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SATIATED_SHIELD.get(), 1600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties STEAMED_RICE_ROLLS_ITEM = new Builder()
        .nutrition(14)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties STEAMED_RICE_ROLLS_BLOCK = new Builder()
        .nutrition(7)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties RED_RICE_ROLL_ITEM = new Builder()
        .nutrition(14)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties RED_RICE_ROLL_BLOCK = new Builder()
        .nutrition(4)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties SAUERKRAUT_BEEF_NOODLES = new Builder()
        .nutrition(14)
        .saturationMod(0.64F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.WARMTH.get(), 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties YELLOW_CROAKER_SOUP_ITEM = new Builder()
        .nutrition(13)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties YELLOW_CROAKER_SOUP_BLOCK = new Builder()
        .nutrition(3)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties YELLOW_CROAKER_TOFU_SOUP_ITEM = new Builder()
        .nutrition(14)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties YELLOW_CROAKER_TOFU_SOUP_BLOCK = new Builder()
        .nutrition(4)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 9600), 1.0F)
        .alwaysEat()
        .build();
    public static final FoodProperties SALTED_EGG = new Builder().nutrition(4).saturationMod(0.5F).alwaysEat().build();
    public static final FoodProperties CENTURY_EGG = new Builder().nutrition(4).saturationMod(0.5F).alwaysEat().build();
    public static final FoodProperties CHINESE_SAUERKRAUT = new Builder().nutrition(2).saturationMod(0.0F).alwaysEat().build();
    public static final FoodProperties YELLOW_CROAKER = new Builder().nutrition(2).saturationMod(0.4F).alwaysEat().build();
    public static final FoodProperties MOONCAKE = new Builder().nutrition(8).saturationMod(1.0F).alwaysEat().build();
    public static final FoodProperties FROZEN_BUN = new Builder()
        .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600), 1.0F)
        .nutrition(6)
        .saturationMod(0.6F)
        .alwaysEat()
        .build();
    public static final FoodProperties BAMBOO_STEAMED_EGG = new Builder().nutrition(6).saturationMod(0.61F).alwaysEat().build();
    public static final FoodProperties CORN = new Builder().nutrition(2).saturationMod(0.4F).alwaysEat().build();
    public static final FoodProperties EGGPLANT = new Builder().nutrition(2).saturationMod(0.5F).alwaysEat().build();
    public static final FoodProperties STUFFED_EGGPLANT = new Builder()
        .nutrition(13)
        .saturationMod(0.61F)
        .effect(new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.VIGOR.get(), 1800), 1.0F)
        .alwaysEat()
        .build();

    public ModFoods() {
    }
}
