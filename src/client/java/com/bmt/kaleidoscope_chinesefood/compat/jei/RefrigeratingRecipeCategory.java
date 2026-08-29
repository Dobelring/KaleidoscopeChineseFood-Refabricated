package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingRecipe;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class RefrigeratingRecipeCategory implements IRecipeCategory<RecipeHolder<RefrigeratingRecipe>> {
   public static final IRecipeHolderType<RefrigeratingRecipe> TYPE = IRecipeType.create(ModRecipes.REFRIGERATING_TYPE);

   private static final Identifier BACKGROUND_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/jei/freezer.png");
   private static final MutableComponent TITLE = Component.translatable("jei.kaleidoscope_chinesefood.refrigerating");

   public static final int WIDTH = 116;
   public static final int HEIGHT = 54;

   private final IDrawable background;
   private final IDrawable icon;

   public RefrigeratingRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 0, WIDTH, HEIGHT);
      this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.FREEZER));
   }

   @Override
   public void setRecipe(@NonNull IRecipeLayoutBuilder builder, RecipeHolder<RefrigeratingRecipe> holder, @NonNull IFocusGroup focuses) {
      RefrigeratingRecipe recipe = holder.value();
      builder.addSlot(RecipeIngredientRole.INPUT, 12, 21).add(recipe.getIngredients().get(0));
      builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 21).add(recipe.getOutput());
   }

   @Override
   public void draw(@NonNull RecipeHolder<RefrigeratingRecipe> recipe, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
   }

   @Override
   public @NotNull IRecipeType<RecipeHolder<RefrigeratingRecipe>> getRecipeType() {
      return TYPE;
   }

   @Override
   public @NotNull Component getTitle() {
      return TITLE;
   }

   @Override
   public int getWidth() {
      return WIDTH;
   }

   @Override
   public int getHeight() {
      return HEIGHT;
   }

   @Override
   @Nullable
   public IDrawable getIcon() {
      return this.icon;
   }
}
