package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class RefrigeratingRecipe extends BaseProcessingRecipe {
   public RefrigeratingRecipe(Ingredient input, ItemStack output, int baseTime) {
      super(input, output, baseTime);
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.REFRIGERATING_SERIALIZER;
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)ModRecipes.REFRIGERATING_TYPE;
   }

   public static class Serializer extends BaseProcessingRecipe.Serializer<RefrigeratingRecipe> {
      public Serializer() {
         super(RefrigeratingRecipe::new);
      }
   }
}
