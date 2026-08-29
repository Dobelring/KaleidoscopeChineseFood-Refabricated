package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

/**
 * JEI 27.x (MC 1.21.11) 插件。
 * 注意：1.21.2+ 原版不再向客户端同步完整配方列表，
 * 需要在主初始化（服务端）注册 RecipeSynchronization 同步，
 * 客户端这里通过 level.recipeAccess().getSynchronizedRecipes() 读取完整配方。
 */
@JeiPlugin
public class Plugins implements IModPlugin {
   private static final Identifier ID = KaleidoscopeChineseFood.id("jei_plugin");

   @Override
   public @NotNull Identifier getPluginUid() {
      return ID;
   }

   @Override
   public void registerCategories(@NonNull IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new PicklingJarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
      registration.addRecipeCategories(new FreezingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
      registration.addRecipeCategories(new RefrigeratingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
      registration.addRecipeCategories(new MooncakeMoldRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
   }

   @Override
   public void registerRecipes(@NonNull IRecipeRegistration registration) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         SynchronizedRecipes recipes = level.recipeAccess().getSynchronizedRecipes();
         registration.addRecipes(PicklingJarRecipeCategory.TYPE, List.copyOf(recipes.getAllOfType(ModRecipes.PICKLE_JAR_TYPE)));
         registration.addRecipes(FreezingRecipeCategory.TYPE, List.copyOf(recipes.getAllOfType(ModRecipes.FREEZING_TYPE)));
         registration.addRecipes(RefrigeratingRecipeCategory.TYPE, List.copyOf(recipes.getAllOfType(ModRecipes.REFRIGERATING_TYPE)));
      }

      // 月饼模具是纯代码交互（非配方系统），提供静态虚拟展示条目，不依赖世界加载
      registration.addRecipes(MooncakeMoldRecipeCategory.TYPE, List.of(MooncakeMoldRecipeCategory.createDisplay()));
   }

   @Override
   public void registerRecipeCatalysts(@NonNull IRecipeCatalystRegistration registration) {
      registration.addCraftingStation(PicklingJarRecipeCategory.TYPE, ModBlocks.PICKLE_JAR);
      registration.addCraftingStation(MooncakeMoldRecipeCategory.TYPE, ModItems.MOONCAKE_MOLD);
      registration.addCraftingStation(FreezingRecipeCategory.TYPE, ModBlocks.FREEZER);
      registration.addCraftingStation(RefrigeratingRecipeCategory.TYPE, ModBlocks.FREEZER);
      registration.addCraftingStation(FreezingRecipeCategory.TYPE, ModBlocks.FREEZER_GREEN);
      registration.addCraftingStation(RefrigeratingRecipeCategory.TYPE, ModBlocks.FREEZER_GREEN);
      registration.addCraftingStation(FreezingRecipeCategory.TYPE, ModBlocks.FREEZER_ORANGE);
      registration.addCraftingStation(RefrigeratingRecipeCategory.TYPE, ModBlocks.FREEZER_ORANGE);
      registration.addCraftingStation(FreezingRecipeCategory.TYPE, ModBlocks.FREEZER_LIGHT_GRAY);
      registration.addCraftingStation(RefrigeratingRecipeCategory.TYPE, ModBlocks.FREEZER_LIGHT_GRAY);
      registration.addCraftingStation(FreezingRecipeCategory.TYPE, ModBlocks.FREEZER_PINK);
      registration.addCraftingStation(RefrigeratingRecipeCategory.TYPE, ModBlocks.FREEZER_PINK);
      registration.addCraftingStation(FreezingRecipeCategory.TYPE, ModBlocks.FREEZER_LIGHT_BLUE);
      registration.addCraftingStation(RefrigeratingRecipeCategory.TYPE, ModBlocks.FREEZER_LIGHT_BLUE);
      registration.addCraftingStation(FreezingRecipeCategory.TYPE, ModBlocks.FREEZER_YELLOW);
      registration.addCraftingStation(RefrigeratingRecipeCategory.TYPE, ModBlocks.FREEZER_YELLOW);
   }
}
