package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class PicklingJarRecipeCategory implements IRecipeCategory<RecipeHolder<PickleJarRecipe>> {
   public static final IRecipeHolderType<PickleJarRecipe> TYPE = IRecipeType.create(ModRecipes.PICKLE_JAR_TYPE);

   private static final Identifier BACKGROUND_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/jei/pickle_jar.png");
   private static final MutableComponent TITLE = Component.translatable("jei.kaleidoscope_chinesefood.pickle_jar");

   public static final int WIDTH = 140;
   public static final int HEIGHT = 60;

   private final IDrawable background;
   private final IDrawable icon;

   public PicklingJarRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 9, 19, WIDTH, HEIGHT);
      this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.PICKLE_JAR));
   }

   @Override
   public void setRecipe(@NonNull IRecipeLayoutBuilder builder, RecipeHolder<PickleJarRecipe> holder, @NonNull IFocusGroup focuses) {
      PickleJarRecipe recipe = holder.value();
      NonNullList<Ingredient> ingredients = recipe.getIngredients();

      for (int i = 0; i < 4; i++) {
         int x = i % 2 * 17 + 12;
         int y = i / 2 * 17 + 14;
         Ingredient ing = i < ingredients.size() ? ingredients.get(i) : null;
         if (ing == null || ing.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, x, y);
         } else {
            // 通过 items() 流取配料物品（对齐厨房乐事 Teapot 写法），腌菜罐每格固定显示 4 个
            List<ItemStack> displayItems = ing.items()
               .map(item -> item.value().getDefaultInstance().copyWithCount(4))
               .toList();
            builder.addSlot(RecipeIngredientRole.INPUT, x, y).addItemStacks(displayItems);
         }
      }

      ItemStack result = recipe.getOutput().copy();
      result.setCount(result.getCount() * 4);
      int seconds = recipe.getFermentTime() / 20;
      builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 23).add(result)
         .addRichTooltipCallback(
            (slotView, tooltip) -> tooltip.add(Component.translatable("jei.kaleidoscope_chinesefood.ferment_time", seconds + "秒"))
         );
   }

   @Override
   public void draw(@NonNull RecipeHolder<PickleJarRecipe> recipe, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
   }

   @Override
   public @NotNull IRecipeType<RecipeHolder<PickleJarRecipe>> getRecipeType() {
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
