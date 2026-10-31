package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.recipe.FreezingRecipe;
import com.bmt.kaleidoscope_chinesefood.recipe.RefrigeratingBrewLevelRecipe;
import com.bmt.kaleidoscope_chinesefood.recipe.RefrigeratingRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ModRecipeSerializers {
    public static final RecipeSerializer<FreezingRecipe> FREEZING_SERIALIZER = new FreezingRecipe.Serializer();
    public static final RecipeSerializer<RefrigeratingRecipe> REFRIGERATING_SERIALIZER = new RefrigeratingRecipe.Serializer();
    public static final RecipeSerializer<RefrigeratingBrewLevelRecipe> REFRIGERATING_BREW_LEVEL_SERIALIZER = new RefrigeratingBrewLevelRecipe.Serializer();

    public ModRecipeSerializers() {
    }

    public static void registerRecipeSerializers() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation("kaleidoscope_chinesefood", "freezing"), FREEZING_SERIALIZER);
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation("kaleidoscope_chinesefood", "refrigerating"), REFRIGERATING_SERIALIZER
        );
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            new ResourceLocation("kaleidoscope_chinesefood", "refrigerating_brew_level"),
            REFRIGERATING_BREW_LEVEL_SERIALIZER
        );
    }
}
