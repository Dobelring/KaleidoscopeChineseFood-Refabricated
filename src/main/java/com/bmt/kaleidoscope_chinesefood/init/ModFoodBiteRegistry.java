package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry.FoodData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class ModFoodBiteRegistry {
    public static ResourceLocation SICHUAN_BOILED_FISH;
    public static ResourceLocation SICHUAN_BOILED_PORK_SLICES;
    public static ResourceLocation YELLOW_CROAKER_SOUP;
    public static ResourceLocation RED_RICE_ROLL;
    public static ResourceLocation YELLOW_CROAKER_TOFU_SOUP;
    public static ResourceLocation STEAMED_RICE_ROLLS;
    public static ResourceLocation FOUR_JOY_MEATBALLS;

    public ModFoodBiteRegistry() {
    }

    public static void init() {
        // 注意：厨艺 Fabric 版的 FoodBiteRegistry 构造器是 private，只能取单例 INSTANCE
        // （Forge 版是 public 无参构造器，两者 registerFoodData 的行为一致）
        FoodBiteRegistry registry = FoodBiteRegistry.INSTANCE;
        STEAMED_RICE_ROLLS = registry.registerFoodData(
            KaleidoscopeChineseFood.id("steamed_rice_rolls"), FoodData.create(2, ModFoods.STEAMED_RICE_ROLLS_BLOCK, ModFoods.STEAMED_RICE_ROLLS_ITEM)
        );
        FOUR_JOY_MEATBALLS = registry.registerFoodData(
            KaleidoscopeChineseFood.id("four_joy_meatballs"), FoodData.create(4, ModFoods.FOUR_JOY_MEATBALLS_BLOCK, ModFoods.FOUR_JOY_MEATBALLS_ITEM)
        );
        YELLOW_CROAKER_TOFU_SOUP = registry.registerFoodData(
            KaleidoscopeChineseFood.id("yellow_croaker_tofu_soup"),
            FoodData.create(3, ModFoods.YELLOW_CROAKER_TOFU_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_TOFU_SOUP_ITEM).bowlAABB()
        );
        RED_RICE_ROLL = registry.registerFoodData(
            KaleidoscopeChineseFood.id("red_rice_roll"), FoodData.create(3, ModFoods.RED_RICE_ROLL_BLOCK, ModFoods.RED_RICE_ROLL_ITEM)
        );
        SICHUAN_BOILED_FISH = registry.registerFoodData(
            KaleidoscopeChineseFood.id("sichuan_boiled_fish"),
            FoodData.create(4, ModFoods.SICHUAN_BOILED_FISH_BLOCK, ModFoods.SICHUAN_BOILED_FISH_ITEM).bowlAABB()
        );
        SICHUAN_BOILED_PORK_SLICES = registry.registerFoodData(
            KaleidoscopeChineseFood.id("sichuan_boiled_pork_slices"),
            FoodData.create(4, ModFoods.SICHUAN_BOILED_PORK_SLICES_BLOCK, ModFoods.SICHUAN_BOILED_PORK_SLICES_ITEM).bowlAABB()
        );
        YELLOW_CROAKER_SOUP = registry.registerFoodData(
            KaleidoscopeChineseFood.id("yellow_croaker_soup"),
            FoodData.create(3, ModFoods.YELLOW_CROAKER_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_SOUP_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick()
        );
    }
}
