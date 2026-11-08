package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.FreezingRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingBrewLevelRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * 1.2.0:合并原 ModRecipeTypes / ModRecipeSerializers / recipe.ModRecipes 三个类(对齐官方 init.ModRecipes)。
 */
public class ModRecipes {
    public static final RecipeSerializer<PickleJarRecipe> PICKLE_JAR_SERIALIZER = new PickleJarRecipe.Serializer();
    public static final RecipeSerializer<FreezingRecipe> FREEZING_SERIALIZER = new FreezingRecipe.Serializer();
    public static final RecipeSerializer<RefrigeratingRecipe> REFRIGERATING_SERIALIZER = new RefrigeratingRecipe.Serializer();
    public static final RecipeSerializer<RefrigeratingBrewLevelRecipe> REFRIGERATING_BREW_LEVEL_SERIALIZER = new RefrigeratingBrewLevelRecipe.Serializer();

    public static final RecipeType<PickleJarRecipe> PICKLE_JAR_TYPE = simple(KaleidoscopeChineseFood.id("pickle_jar"));
    public static final RecipeType<FreezingRecipe> FREEZING = simple(KaleidoscopeChineseFood.id("freezing"));
    public static final RecipeType<RefrigeratingRecipe> REFRIGERATING = simple(KaleidoscopeChineseFood.id("refrigerating"));

    public ModRecipes() {
    }

    // Forge 补丁的 RecipeType.simple 在 Fabric 原版不存在，照抄厨艺 Fabric 版做法自建
    private static <T extends Recipe<?>> RecipeType<T> simple(final ResourceLocation id) {
        return new RecipeType<T>() {
            @Override
            public String toString() {
                return id.toString();
            }
        };
    }

    public static void registerAll() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation("kaleidoscope_chinesefood", "pickle_jar"), PICKLE_JAR_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation("kaleidoscope_chinesefood", "freezing"), FREEZING_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation("kaleidoscope_chinesefood", "refrigerating"), REFRIGERATING_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation("kaleidoscope_chinesefood", "refrigerating_brew_level"), REFRIGERATING_BREW_LEVEL_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "pickle_jar"), PICKLE_JAR_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "freezing"), FREEZING);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "refrigerating"), REFRIGERATING);
    }
}
