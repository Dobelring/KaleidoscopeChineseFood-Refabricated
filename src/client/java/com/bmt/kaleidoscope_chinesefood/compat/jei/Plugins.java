package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.FreezingRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

@JeiPlugin
public class Plugins implements IModPlugin {
   private static final ResourceLocation ID = KaleidoscopeChineseFood.id("jei_plugin");

   public ResourceLocation getPluginUid() {
      return ID;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new PicklingJarRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new FreezingRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new RefrigeratingRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new MooncakeMoldRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         RecipeManager recipeManager = level.getRecipeManager();
         List<PickleJarRecipe> pickleRecipes = recipeManager.getAllRecipesFor(ModRecipes.PICKLE_JAR_TYPE)
            .stream()
            .map(RecipeHolder::value)
            .toList();
         registration.addRecipes(PicklingJarRecipeCategory.TYPE, pickleRecipes);
         List<FreezingRecipe> freezingRecipes = recipeManager.getAllRecipesFor(ModRecipes.FREEZING_TYPE)
            .stream()
            .map(RecipeHolder::value)
            .toList();
         registration.addRecipes(FreezingRecipeCategory.TYPE, freezingRecipes);
         List<RefrigeratingRecipe> refrigeratingRecipes = recipeManager.getAllRecipesFor(ModRecipes.REFRIGERATING_TYPE)
            .stream()
            .map(RecipeHolder::value)
            .toList();
         registration.addRecipes(RefrigeratingRecipeCategory.TYPE, refrigeratingRecipes);
      }

      // 月饼模具是纯代码交互（非配方系统），提供静态虚拟展示条目，不依赖世界加载
      registration.addRecipes(MooncakeMoldRecipeCategory.TYPE, List.of(MooncakeMoldRecipeCategory.createDisplay()));
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.PICKLE_JAR), new mezz.jei.api.recipe.RecipeType[]{PicklingJarRecipeCategory.TYPE});
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModItems.MOONCAKE_MOLD), new mezz.jei.api.recipe.RecipeType[]{MooncakeMoldRecipeCategory.TYPE});
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.FREEZER), new mezz.jei.api.recipe.RecipeType[]{FreezingRecipeCategory.TYPE});
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.FREEZER), new mezz.jei.api.recipe.RecipeType[]{RefrigeratingRecipeCategory.TYPE});
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.FREEZER_GREEN), new mezz.jei.api.recipe.RecipeType[]{FreezingRecipeCategory.TYPE});
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.FREEZER_GREEN), new mezz.jei.api.recipe.RecipeType[]{RefrigeratingRecipeCategory.TYPE}
      );
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.FREEZER_ORANGE), new mezz.jei.api.recipe.RecipeType[]{FreezingRecipeCategory.TYPE});
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.FREEZER_ORANGE), new mezz.jei.api.recipe.RecipeType[]{RefrigeratingRecipeCategory.TYPE}
      );
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.FREEZER_PINK), new mezz.jei.api.recipe.RecipeType[]{FreezingRecipeCategory.TYPE});
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.FREEZER_PINK), new mezz.jei.api.recipe.RecipeType[]{RefrigeratingRecipeCategory.TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.FREEZER_LIGHT_BLUE), new mezz.jei.api.recipe.RecipeType[]{FreezingRecipeCategory.TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.FREEZER_LIGHT_BLUE), new mezz.jei.api.recipe.RecipeType[]{RefrigeratingRecipeCategory.TYPE}
      );
      registration.addRecipeCatalyst(new ItemStack((ItemLike)ModBlocks.FREEZER_YELLOW), new mezz.jei.api.recipe.RecipeType[]{FreezingRecipeCategory.TYPE});
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ModBlocks.FREEZER_YELLOW), new mezz.jei.api.recipe.RecipeType[]{RefrigeratingRecipeCategory.TYPE}
      );
   }
}
