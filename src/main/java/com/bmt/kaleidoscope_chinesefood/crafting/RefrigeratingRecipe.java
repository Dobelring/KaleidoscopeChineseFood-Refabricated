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
      // 1.21.2+ 原版 RecipeManager 会丢弃 placementInfo 为空的配方（日志 "can't be placed"），
      // 必须返回由真实配料构造的 PlacementInfo
      return PlacementInfo.create(this.input);
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
