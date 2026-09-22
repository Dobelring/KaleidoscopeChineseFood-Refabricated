package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipeTypes;
import com.bmt.kaleidoscope_chinesefood.recipe.ModRecipes;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

// 插件入口点已在 fabric.mod.json 的 jei_mod_plugin 中配置，无需 @JeiPlugin 注解
public class ModPlugin implements IModPlugin {
    private static final ResourceLocation ID = KaleidoscopeChineseFood.id("jei_plugin");

    public ModPlugin() {
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PicklingJarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new FreezingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RefrigeratingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new MooncakeMoldRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        registration.addRecipes(PicklingJarRecipeCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.PICKLE_JAR_TYPE));
        registration.addRecipes(FreezingRecipeCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipeTypes.FREEZING));
        registration.addRecipes(RefrigeratingRecipeCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipeTypes.REFRIGERATING));
        registration.addRecipes(MooncakeMoldRecipeCategory.TYPE, List.of(new MooncakeMoldRecipe()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.PICKLE_JAR, PicklingJarRecipeCategory.TYPE);
        ItemStack[] freezers = new ItemStack[]{
            new ItemStack(ModBlocks.FREEZER),
            new ItemStack(ModBlocks.FREEZER_GREEN),
            new ItemStack(ModBlocks.FREEZER_ORANGE),
            new ItemStack(ModBlocks.FREEZER_PINK),
            new ItemStack(ModBlocks.FREEZER_LIGHT_BLUE),
            new ItemStack(ModBlocks.FREEZER_YELLOW)
        };

        for (ItemStack freezer : freezers) {
            registration.addRecipeCatalyst(freezer, FreezingRecipeCategory.TYPE);
            registration.addRecipeCatalyst(freezer, RefrigeratingRecipeCategory.TYPE);
        }

        registration.addRecipeCatalyst(ModItems.MOONCAKE_MOLD, MooncakeMoldRecipeCategory.TYPE);
    }
}
