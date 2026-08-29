package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry.FoodData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

public class ModFoodBiteRegistry {
   public static Identifier SICHUAN_BOILED_PORK_SLICES;
   public static Identifier SICHUAN_BOILED_FISH;
   public static Identifier YELLOW_CROAKER_SOUP;
   public static Identifier RED_RICE_ROLL;
   public static Identifier YELLOW_CROAKER_TOFU_SOUP;

   public static void init() {
      // 1.21.11 鐨?FoodBiteRegistry 鏀逛负瀹炰緥娉ㄥ唽锛孎oodData.create 闇€瑕佹垚瀵?FoodProperties + Consumable
      FoodBiteRegistry registry = new FoodBiteRegistry();
      YELLOW_CROAKER_TOFU_SOUP = registry.registerFoodData(
         KaleidoscopeChineseFood.id("yellow_croaker_tofu_soup"),
         FoodData.create(3, ModFoods.YELLOW_CROAKER_TOFU_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_TOFU_SOUP_ITEM, ModFoods.YELLOW_CROAKER_TOFU_SOUP_BLOCK_C, ModFoods.YELLOW_CROAKER_TOFU_SOUP_ITEM_C).bowlAABB()
      );
      RED_RICE_ROLL = registry.registerFoodData(
         KaleidoscopeChineseFood.id("red_rice_roll"),
         FoodData.create(3, ModFoods.RED_RICE_ROLL_BLOCK, ModFoods.RED_RICE_ROLL_ITEM, ModFoods.RED_RICE_ROLL_BLOCK_C, ModFoods.RED_RICE_ROLL_ITEM_C)
      );
      SICHUAN_BOILED_PORK_SLICES = registry.registerFoodData(
         KaleidoscopeChineseFood.id("sichuan_boiled_pork_slices"),
         FoodData.create(3, ModFoods.SICHUAN_BOILED_PORK_SLICES_BLOCK, ModFoods.SICHUAN_BOILED_PORK_SLICES_ITEM, ModFoods.SICHUAN_BOILED_PORK_SLICES_BLOCK_C, ModFoods.SICHUAN_BOILED_PORK_SLICES_ITEM_C).bowlAABB()
      );
      SICHUAN_BOILED_FISH = registry.registerFoodData(
         KaleidoscopeChineseFood.id("sichuan_boiled_fish"),
         FoodData.create(4, ModFoods.SICHUAN_BOILED_FISH_BLOCK, ModFoods.SICHUAN_BOILED_FISH_ITEM, ModFoods.SICHUAN_BOILED_FISH_BLOCK_C, ModFoods.SICHUAN_BOILED_FISH_ITEM_C).bowlAABB()
      );
      YELLOW_CROAKER_SOUP = registry.registerFoodData(
         KaleidoscopeChineseFood.id("yellow_croaker_soup"),
         FoodData.create(3, ModFoods.YELLOW_CROAKER_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_SOUP_ITEM, ModFoods.YELLOW_CROAKER_SOUP_BLOCK_C, ModFoods.YELLOW_CROAKER_SOUP_ITEM_C)
            .setLootItem(Items.FLOWER_POT)
            .soupPotAABB()
            .setAnimateTick(FoodBiteAnimateTicks.POT_SOUP_ANIMATE_TICK)
      );
   }
}