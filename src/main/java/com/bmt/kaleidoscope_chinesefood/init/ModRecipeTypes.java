package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.recipe.FreezingRecipe;
import com.bmt.kaleidoscope_chinesefood.recipe.RefrigeratingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipeTypes {
    public static final RecipeType<FreezingRecipe> FREEZING = simple(KaleidoscopeChineseFood.id("freezing"));
    public static final RecipeType<RefrigeratingRecipe> REFRIGERATING = simple(KaleidoscopeChineseFood.id("refrigerating"));

    public ModRecipeTypes() {
    }

    // 注意：原 Forge 代码用的 RecipeType.simple(ResourceLocation) 是 Forge 补丁新增的方法，
    // 原版（Fabric）1.20.1 没有，这里照抄厨艺 Fabric 版 ModRecipes 的做法自建
    private static <T extends Recipe<?>> RecipeType<T> simple(final ResourceLocation id) {
        return new RecipeType<T>() {
            @Override
            public String toString() {
                return id.toString();
            }
        };
    }

    public static void registerRecipeTypes() {
        Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "freezing"), FREEZING);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "refrigerating"), REFRIGERATING);
    }
}
