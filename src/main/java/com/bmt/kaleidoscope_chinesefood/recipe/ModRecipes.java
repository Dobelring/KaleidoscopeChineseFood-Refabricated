package com.bmt.kaleidoscope_chinesefood.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {
    public static final RecipeSerializer<PickleJarRecipe> PICKLE_JAR_SERIALIZER = new PickleJarRecipe.Serializer();
    public static final RecipeType<PickleJarRecipe> PICKLE_JAR_TYPE = new RecipeType<PickleJarRecipe>() {
    };

    public ModRecipes() {
    }

    public static void registerRecipes() {
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation("kaleidoscope_chinesefood", "pickle_jar"), PICKLE_JAR_SERIALIZER
        );
        Registry.register(BuiltInRegistries.RECIPE_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "pickle_jar"), PICKLE_JAR_TYPE);
    }
}
