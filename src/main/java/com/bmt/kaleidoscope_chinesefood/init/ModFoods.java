package com.bmt.kaleidoscope_chinesefood.init;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;

/**
 * 26.1: FoodProperties no longer carries effects — they moved to the
 * {@link net.minecraft.world.item.component.Consumable} component (see {@link ModConsumables}).
 */
public class ModFoods {
   public static final FoodProperties SICHUAN_WONTON = food(14, 0.64F);
   public static final FoodProperties WONTON_NOODLES = food(14, 0.66F);
   public static final FoodProperties YANGROU_PAOMO = food(14, 0.64F);
   public static final FoodProperties MAOCAI = food(6, 0.64F);
   public static final FoodProperties SEAWEED_EGG_DROP_SOUP = food(6, 0.64F);
   public static final FoodProperties TOMATO_EGG_DROP_SOUP = food(6, 0.64F);
   public static final FoodProperties DOUZHI = food(6, 0.64F);
   public static final FoodProperties CENTURY_EGG_CONGEE = food(6, 0.64F);
   public static final FoodProperties PUMPKIN_PORRIDGE = food(6, 0.64F);
   public static final FoodProperties SICHUAN_BOILED_PORK_SLICES_ITEM = food(13, 0.61F);
   public static final FoodProperties SICHUAN_BOILED_PORK_SLICES_BLOCK = food(4, 0.61F);
   public static final FoodProperties SICHUAN_BOILED_FISH_ITEM = food(13, 0.61F);
   public static final FoodProperties SICHUAN_BOILED_FISH_BLOCK = food(3, 0.61F);
   public static final FoodProperties TWICE_COOKED_PORK = food(9, 0.61F);
   public static final FoodProperties TWICE_COOKED_PORK_RICE = food(14, 0.64F);
   public static final FoodProperties STIR_FRIED_YELLOW_BEEF = food(9, 0.61F);
   public static final FoodProperties STIR_FRIED_YELLOW_BEEF_RICE = food(14, 0.64F);
   public static final FoodProperties BEEF_WITH_SCRAMBLED_EGGS = food(9, 0.61F);
   public static final FoodProperties BEEF_WITH_SCRAMBLED_EGGS_RICE = food(14, 0.64F);
   public static final FoodProperties STIR_FRIED_THREE_FRESH_VEGETABLES = food(9, 0.61F);
   public static final FoodProperties STIR_FRIED_THREE_FRESH_VEGETABLES_RICE = food(14, 0.7F);
   public static final FoodProperties BIG_PLATE_CHICKEN = food(9, 0.61F);
   public static final FoodProperties BIG_PLATE_CHICKEN_NOODLES = food(14, 0.7F);
   public static final FoodProperties TOMATO_EGG_NOODLES = food(14, 0.7F);
   public static final FoodProperties PORK_CHILI_NOODLES = food(14, 0.7F);
   public static final FoodProperties FOUR_JOY_MEATBALLS = food(13, 0.61F);
   public static final FoodProperties STUFFED_EGGPLANT = food(13, 0.61F);
   public static final FoodProperties DRY_POT_POTATOES = food(13, 0.61F);
   public static final FoodProperties DRY_POT_CHICKEN = food(13, 0.61F);
   public static final FoodProperties DRY_POT_SPARE_RIBS = food(13, 0.61F);
   public static final FoodProperties YANGZHOU_FRIED_RICE = food(12, 0.66F);
   public static final FoodProperties LAMB_PILAF = food(14, 0.66F);
   public static final FoodProperties STEAMED_RICE_ROLLS = food(14, 0.64F);
   public static final FoodProperties RED_RICE_ROLL_ITEM = food(14, 0.64F);
   public static final FoodProperties RED_RICE_ROLL_BLOCK = food(4, 0.64F);
   public static final FoodProperties SAUERKRAUT_BEEF_NOODLES = food(14, 0.64F);
   public static final FoodProperties YELLOW_CROAKER_TOFU_SOUP_ITEM = food(14, 0.61F);
   public static final FoodProperties YELLOW_CROAKER_TOFU_SOUP_BLOCK = food(4, 0.61F);
   public static final FoodProperties FROZEN_BUN = food(6, 0.6F);
   public static final FoodProperties YELLOW_CROAKER_SOUP_ITEM = food(13, 0.61F);
   public static final FoodProperties YELLOW_CROAKER_SOUP_BLOCK = food(3, 0.61F);
   public static final FoodProperties SALTED_EGG = food(4, 0.5F);
   public static final FoodProperties CENTURY_EGG = food(4, 0.5F);
   public static final FoodProperties CHINESE_SAUERKRAUT = food(2, 0.0F);
   public static final FoodProperties YELLOW_CROAKER = food(2, 0.4F);
   public static final FoodProperties MOONCAKE = food(2, 0.4F);
   public static final FoodProperties CORN = food(2, 0.0F);
   public static final FoodProperties EGGPLANT = food(2, 0.5F);

   private static FoodProperties food(int nutrition, float saturation) {
      return new Builder().nutrition(nutrition).saturationModifier(saturation).alwaysEdible().build();
   }
}
