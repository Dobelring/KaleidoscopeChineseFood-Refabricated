package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class PicklingJarRecipeCategory implements IRecipeCategory<RecipeHolder<PickleJarRecipe>> {
   public static final IRecipeHolderType<PickleJarRecipe> TYPE = IRecipeType.create(ModRecipes.PICKLE_JAR_TYPE);
   private static final Identifier BACKGROUND_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/jei/pickle_jar.png");
   private static final int WIDTH = 140;
   private static final int HEIGHT = 60;
   private final IDrawable background;
   private final IDrawable icon;

   public PicklingJarRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 9, 19, WIDTH, HEIGHT);
      this.icon = guiHelper.createDrawableItemLike(ModBlocks.PICKLE_JAR);
   }

   /** JEI 30.x 移除了 getBackground()，背景贴图改为每帧在 draw 中自行绘制。 */
   public void draw(RecipeHolder<PickleJarRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<PickleJarRecipe> holder, IFocusGroup focuses) {
      PickleJarRecipe recipe = holder.value();
      NonNullList<Ingredient> ingredients = recipe.getIngredients();

      for (int i = 0; i < 4; i++) {
         int x = i % 2 * 17 + 12;
         int y = i / 2 * 17 + 14;
         if (i >= ingredients.size()) {
            builder.addSlot(RecipeIngredientRole.INPUT, x, y);
            continue;
         }

         Ingredient ing = ingredients.get(i);
         // 配方 matches 强制每格正好 4 个，展示层同样 ×4；同一槽位内多个 .add 为可选物品
         mezz.jei.api.gui.builder.IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x, y);
         for (Holder<Item> itemHolder : ing.items().toList()) {
            ItemStack displayStack = new ItemStack(itemHolder);
            displayStack.setCount(4);
            slot.add(displayStack);
         }
      }

      // 与 1.21.1 版一致：输出也按一批展示为 ×4
      ItemStack result = recipe.getOutput().copy();
      result.setCount(result.getCount() * 4);
      int seconds = recipe.getFermentTime() / 20;
      builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 23)
         .add(result)
         .addRichTooltipCallback((slotView, tooltip) ->
            tooltip.add(Component.translatable("jei.kaleidoscope_chinesefood.ferment_time", seconds + "秒")));
   }

   public IRecipeHolderType<PickleJarRecipe> getRecipeType() {
      return TYPE;
   }

   public Component getTitle() {
      return Component.translatable("jei.kaleidoscope_chinesefood.pickle_jar");
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
