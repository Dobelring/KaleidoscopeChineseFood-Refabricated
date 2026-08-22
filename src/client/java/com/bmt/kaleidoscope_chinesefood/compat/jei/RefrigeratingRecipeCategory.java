package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class RefrigeratingRecipeCategory implements IRecipeCategory<RefrigeratingRecipe> {
   public static final RecipeType<RefrigeratingRecipe> TYPE = RecipeType.create("kaleidoscope_chinesefood", "refrigerating", RefrigeratingRecipe.class);
   private static final ResourceLocation BACKGROUND_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/jei/freezer.png");
   private final IDrawable background;
   private final IDrawable icon;

   public RefrigeratingRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, 116, 54);
      this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ModBlocks.FREEZER));
   }

   public RecipeType<RefrigeratingRecipe> getRecipeType() {
      return TYPE;
   }

   public Component getTitle() {
      return Component.translatable("jei.kaleidoscope_chinesefood.refrigerating");
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, RefrigeratingRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 12, 21).addIngredients((Ingredient)recipe.getIngredients().get(0));
      ClientLevel level = Minecraft.getInstance().level;
      if (level != null) {
         Provider registries = level.registryAccess();
         builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 21).addItemStack(recipe.getResultItem(registries));
      }
   }
}
