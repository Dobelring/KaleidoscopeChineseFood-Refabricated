package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class RefrigeratingRecipe extends BaseProcessingRecipe {
   public RefrigeratingRecipe(Ingredient input, ItemStack output, int baseTime) {
      super(input, output, baseTime);
   }

   @Override
   protected String bookCategoryName() {
      return "refrigerating";
   }

   @NotNull
   public RecipeSerializer<? extends Recipe<FreezerInput>> getSerializer() {
      return ModRecipes.REFRIGERATING_SERIALIZER;
   }

   @NotNull
   public RecipeType<? extends Recipe<FreezerInput>> getType() {
      return ModRecipes.REFRIGERATING_TYPE;
   }

   public static RecipeSerializer<RefrigeratingRecipe> makeBaseSerializer() {
      return BaseProcessingRecipe.makeSerializer(RefrigeratingRecipe::new);
   }
}
