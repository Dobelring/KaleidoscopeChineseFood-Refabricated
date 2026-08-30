package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class FreezingRecipe extends BaseProcessingRecipe {
   public FreezingRecipe(Ingredient input, ItemStack output, int baseTime) {
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

   @NotNull
   public PlacementInfo placementInfo() {
      // 1.21.2+ 原版 RecipeManager 会丢弃 placementInfo 为空的配方（日志 "can't be placed"），
      // 必须返回由真实配料构造的 PlacementInfo
      return PlacementInfo.create(this.input);
   }

   public RecipeBookCategory recipeBookCategory() {
      return ModRecipes.FREEZING_CATEGORY;
   }

   public static class Serializer extends BaseProcessingRecipe.Serializer<FreezingRecipe> {
      public Serializer() {
         super(FreezingRecipe::new);
      }
   }
}
