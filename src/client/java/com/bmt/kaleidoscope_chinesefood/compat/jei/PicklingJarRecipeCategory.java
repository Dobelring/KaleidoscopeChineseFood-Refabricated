package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import java.util.Arrays;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class PicklingJarRecipeCategory implements IRecipeCategory<PickleJarRecipe> {
   public static final RecipeType<PickleJarRecipe> TYPE = RecipeType.create("kaleidoscope_chinesefood", "pickle_jar", PickleJarRecipe.class);
   private static final ResourceLocation BACKGROUND_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/jei/pickle_jar.png");
   private final IDrawable background;
   private final IDrawable icon;

   public PicklingJarRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 9, 19, 140, 60);
      this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ModBlocks.PICKLE_JAR));
   }

   public RecipeType<PickleJarRecipe> getRecipeType() {
      return TYPE;
   }

   public Component getTitle() {
      return Component.translatable("jei.kaleidoscope_chinesefood.pickle_jar");
   }

   public IDrawable getBackground() {
      return this.background;
   }

   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, PickleJarRecipe recipe, IFocusGroup focuses) {
      NonNullList<Ingredient> ingredients = recipe.getIngredients();

      for (int i = 0; i < 4; i++) {
         int x = i % 2 * 17 + 12;
         int y = i / 2 * 17 + 14;
         Ingredient ing = i < ingredients.size() ? (Ingredient)ingredients.get(i) : Ingredient.EMPTY;
         ItemStack[] items = ing.getItems();
         if (items.length <= 0) {
            builder.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStack(ItemStack.EMPTY);
         } else {
            ItemStack[] displayItems = new ItemStack[items.length];

            for (int j = 0; j < items.length; j++) {
               displayItems[j] = items[j].copy();
               displayItems[j].setCount(4);
            }

            builder.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStacks(Arrays.asList(displayItems));
         }
      }

      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.level != null) {
         Provider registries = minecraft.level.registryAccess();
         ItemStack result = recipe.getResultItem(registries).copy();
         int originalCount = result.getCount();
         result.setCount(originalCount * 4);
         int fermentTime = recipe.getFermentTime();
         int seconds = fermentTime / 20;
         ((IRecipeSlotBuilder)builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 23).addItemStack(result))
            .addTooltipCallback(
               (slotView, tooltip) -> tooltip.add(Component.translatable("jei.kaleidoscope_chinesefood.ferment_time", new Object[]{seconds + "秒"}))
            );
      }
   }
}
