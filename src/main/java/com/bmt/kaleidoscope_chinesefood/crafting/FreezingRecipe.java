package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class FreezingRecipe extends BaseProcessingRecipe {
   public FreezingRecipe(Ingredient input, ItemStackTemplate output, int baseTime) {
      super(input, output, baseTime);
   }

   @NotNull
   public RecipeSerializer<FreezingRecipe> getSerializer() {
      return ModRecipes.FREEZING_SERIALIZER;
   }

   @NotNull
   public RecipeType<FreezingRecipe> getType() {
      return ModRecipes.FREEZING_TYPE;
   }

   public RecipeBookCategory recipeBookCategory() {
      return ModRecipes.FREEZING_CATEGORY;
   }
}
