package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class RefrigeratingRecipe extends BaseProcessingRecipe {
   public RefrigeratingRecipe(Ingredient input, ItemStack output, int baseTime) {
      super(input, output, baseTime);
   }

   @NotNull
   public RecipeSerializer<RefrigeratingRecipe> getSerializer() {
      return ModRecipes.REFRIGERATING_SERIALIZER;
   }

   @NotNull
   public RecipeType<RefrigeratingRecipe> getType() {
      return ModRecipes.REFRIGERATING_TYPE;
   }

   @NotNull
      
   public PlacementInfo placementInfo() {
      return PlacementInfo.NOT_PLACEABLE;
   }

   public RecipeBookCategory recipeBookCategory() {
      return ModRecipes.REFRIGERATING_CATEGORY;
   }

   public static class Serializer extends BaseProcessingRecipe.Serializer<RefrigeratingRecipe> {
      public Serializer() {
         super(RefrigeratingRecipe::new);
      }
   }
}
