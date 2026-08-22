package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.FreezingRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {
    public static RecipeType<PickleJarRecipe> PICKLE_JAR_TYPE;
    public static RecipeSerializer<PickleJarRecipe> PICKLE_JAR_SERIALIZER;
    public static RecipeType<RefrigeratingRecipe> REFRIGERATING_TYPE;
    public static RecipeSerializer<RefrigeratingRecipe> REFRIGERATING_SERIALIZER;
    public static RecipeType<FreezingRecipe> FREEZING_TYPE;
    public static RecipeSerializer<FreezingRecipe> FREEZING_SERIALIZER;

    public static void register() {
        PICKLE_JAR_TYPE = registerType("pickle_jar");
        PICKLE_JAR_SERIALIZER = registerSerializer("pickle_jar", new PickleJarRecipe.Serializer());
        REFRIGERATING_TYPE = registerType("refrigerating");
        REFRIGERATING_SERIALIZER = registerSerializer("refrigerating", new RefrigeratingRecipe.Serializer());
        FREEZING_TYPE = registerType("freezing");
        FREEZING_SERIALIZER = registerSerializer("freezing", new FreezingRecipe.Serializer());
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(String name) {
        ResourceLocation id = KaleidoscopeChineseFood.id(name);
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, id, new RecipeType<T>() {
            @Override
            public String toString() {
                return id.toString();
            }
        });
    }

    private static <T extends Recipe<?>> RecipeSerializer<T> registerSerializer(String name, RecipeSerializer<T> serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, KaleidoscopeChineseFood.id(name), serializer);
    }
}
