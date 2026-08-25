package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * JEI 30.x 插件：注册腌菜罐 / 冷冻 / 冷藏三个自定义配方分类。
 * 配方数据来自 Fabric 配方同步（服务端在 onInitialize 注册 synchronizeRecipeSerializer，
 * 客户端配置阶段收到后可经 level.recipeAccess().getSynchronizedRecipes() 读取）。
 */
@JeiPlugin
public class Plugins implements IModPlugin {
   private static final Identifier UID = KaleidoscopeChineseFood.id("jei_plugin");

   public Identifier getPluginUid() {
      return UID;
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new PicklingJarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
      registration.addRecipeCategories(new FreezingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
      registration.addRecipeCategories(new RefrigeratingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
   }

   public void registerRecipes(IRecipeRegistration registration) {
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         SynchronizedRecipes synced = level.recipeAccess().getSynchronizedRecipes();
         registration.addRecipes(PicklingJarRecipeCategory.TYPE, List.copyOf(synced.getAllOfType(ModRecipes.PICKLE_JAR_TYPE)));
         registration.addRecipes(FreezingRecipeCategory.TYPE, List.copyOf(synced.getAllOfType(ModRecipes.FREEZING_TYPE)));
         registration.addRecipes(RefrigeratingRecipeCategory.TYPE, List.copyOf(synced.getAllOfType(ModRecipes.REFRIGERATING_TYPE)));
      }
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      // 腌菜罐 → 腌菜罐分类；7 种颜色冰箱 → 冷冻 + 冷藏两个分类
      registration.addCraftingStation(PicklingJarRecipeCategory.TYPE, new ItemStack(ModBlocks.PICKLE_JAR));
      for (Block freezer : FREEZERS) {
         registration.addCraftingStation(FreezingRecipeCategory.TYPE, new ItemStack(freezer));
         registration.addCraftingStation(RefrigeratingRecipeCategory.TYPE, new ItemStack(freezer));
      }
   }

   private static final Block[] FREEZERS = {
      ModBlocks.FREEZER,
      ModBlocks.FREEZER_GREEN,
      ModBlocks.FREEZER_ORANGE,
      ModBlocks.FREEZER_LIGHT_GRAY,
      ModBlocks.FREEZER_PINK,
      ModBlocks.FREEZER_LIGHT_BLUE,
      ModBlocks.FREEZER_YELLOW
   };
}
