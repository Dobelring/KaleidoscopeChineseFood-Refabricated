package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.FreezingRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

public class FreezingRecipeCategory implements IRecipeCategory<RecipeHolder<FreezingRecipe>> {
   public static final IRecipeHolderType<FreezingRecipe> TYPE = IRecipeType.create(ModRecipes.FREEZING_TYPE);
   private static final Identifier BACKGROUND_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/jei/freezer.png");
   private static final int WIDTH = 116;
   private static final int HEIGHT = 54;
   private final IDrawable background;
   private final IDrawable icon;

   public FreezingRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, WIDTH, HEIGHT);
      this.icon = guiHelper.createDrawableItemLike(ModBlocks.FREEZER);
   }

   /** JEI 30.x 移除了 getBackground()，背景贴图改为每帧在 draw 中自行绘制。 */
   public void draw(RecipeHolder<FreezingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FreezingRecipe> holder, IFocusGroup focuses) {
      FreezingRecipe recipe = holder.value();
      builder.addSlot(RecipeIngredientRole.INPUT, 12, 21).add(recipe.getIngredients().get(0));
      builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 21).add(recipe.getOutput());
   }

   public IRecipeHolderType<FreezingRecipe> getRecipeType() {
      return TYPE;
   }

   public Component getTitle() {
      return Component.translatable("jei.kaleidoscope_chinesefood.freezing");
   }

   public int getWidth() {
      return WIDTH;
   }

   public int getHeight() {
      return HEIGHT;
   }

   public IDrawable getIcon() {
      return this.icon;
   }
}
