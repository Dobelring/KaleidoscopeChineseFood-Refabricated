package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class FreezingRecipe extends BaseProcessingRecipe {
   public FreezingRecipe(Ingredient input, ItemStack output, int baseTime) {
      super(input, output, baseTime);
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.FREEZING_SERIALIZER;
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)ModRecipes.FREEZING_TYPE;
   }

   public static class Serializer extends BaseProcessingRecipe.Serializer<FreezingRecipe> {
      public Serializer() {
         super(FreezingRecipe::new);
      }
   }
}
