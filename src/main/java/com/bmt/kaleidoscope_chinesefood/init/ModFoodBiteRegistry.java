package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry.FoodData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class ModFoodBiteRegistry {
   public static ResourceLocation SICHUAN_BOILED_PORK_SLICES;
   public static ResourceLocation SICHUAN_BOILED_FISH;
   public static ResourceLocation YELLOW_CROAKER_SOUP;
   public static ResourceLocation RED_RICE_ROLL;
   public static ResourceLocation YELLOW_CROAKER_TOFU_SOUP;

   public static void init() {
      YELLOW_CROAKER_TOFU_SOUP = FoodBiteRegistry.INSTANCE.registerFoodData(
         KaleidoscopeChineseFood.id("yellow_croaker_tofu_soup"),
         FoodData.create(3, ModFoods.YELLOW_CROAKER_TOFU_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_TOFU_SOUP_ITEM).bowlAABB()
      );
      RED_RICE_ROLL = FoodBiteRegistry.INSTANCE.registerFoodData(
         KaleidoscopeChineseFood.id("red_rice_roll"), FoodData.create(3, ModFoods.RED_RICE_ROLL_BLOCK, ModFoods.RED_RICE_ROLL_ITEM)
      );
      SICHUAN_BOILED_PORK_SLICES = FoodBiteRegistry.INSTANCE.registerFoodData(
         KaleidoscopeChineseFood.id("sichuan_boiled_pork_slices"),
         FoodData.create(3, ModFoods.SICHUAN_BOILED_PORK_SLICES_BLOCK, ModFoods.SICHUAN_BOILED_PORK_SLICES_ITEM).bowlAABB()
      );
      SICHUAN_BOILED_FISH = FoodBiteRegistry.INSTANCE.registerFoodData(
         KaleidoscopeChineseFood.id("sichuan_boiled_fish"),
         FoodData.create(4, ModFoods.SICHUAN_BOILED_FISH_BLOCK, ModFoods.SICHUAN_BOILED_FISH_ITEM).bowlAABB()
      );
      YELLOW_CROAKER_SOUP = FoodBiteRegistry.INSTANCE.registerFoodData(
         KaleidoscopeChineseFood.id("yellow_croaker_soup"),
         FoodData.create(3, ModFoods.YELLOW_CROAKER_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_SOUP_ITEM)
            .setLootItem(Items.FLOWER_POT)
            .soupPotAABB()
            .potSoupAnimateTick()
      );
   }
}
