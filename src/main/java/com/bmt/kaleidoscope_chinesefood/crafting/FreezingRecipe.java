package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class FreezingRecipe extends BaseProcessingRecipe {
   public FreezingRecipe(Ingredient input, ItemStackTemplate output, int baseTime) {
      super(input, output, baseTime);
   }

   @Override
   protected String bookCategoryName() {
      return "freezing";
   }

   @NotNull
   public RecipeSerializer<? extends Recipe<FreezerInput>> getSerializer() {
      return ModRecipes.FREEZING_SERIALIZER;
   }

   @NotNull
   public RecipeType<? extends Recipe<FreezerInput>> getType() {
      return ModRecipes.FREEZING_TYPE;
   }

   public static RecipeSerializer<FreezingRecipe> makeBaseSerializer() {
      return BaseProcessingRecipe.makeSerializer(FreezingRecipe::new);
   }
}
