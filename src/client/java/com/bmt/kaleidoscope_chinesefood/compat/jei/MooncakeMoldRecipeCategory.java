package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeMoldItem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * 月饼模具的虚拟展示条目：厨房乐事夹心面团 → 生月饼。
 * 实际机制在 MooncakeMoldItem 里以代码实现（双手持物长按右键），
 * 不属于配方系统，因此查看器无法自动感知，这里手动提供展示。
 */
public class MooncakeMoldRecipeCategory implements IRecipeCategory<MooncakeMoldRecipeCategory.Display> {
   public static final IRecipeType<Display> TYPE = IRecipeType.create("kaleidoscope_chinesefood", "mooncake_mold", Display.class);

   /** 虚拟条目（不对应任何 RecipeManager 配方）。 */
   public record Display(ItemStack dough, ItemStack result) {}

   public static final int WIDTH = 126;
   public static final int HEIGHT = 40;

   private final IDrawable background;
   private final IDrawable icon;
   private final IDrawable arrow;

   public MooncakeMoldRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
      this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.MOONCAKE_MOLD));
      // 显式调用 setStandardSlotBackground 绘制槽位方框；箭头使用 JEI 自带样式
      this.arrow = guiHelper.getRecipeArrow();
   }

   public static Display createDisplay() {
      return new Display(
         new ItemStack(stuffedDoughItem()),
         new ItemStack(ModItems.RAW_MOONCAKE)
      );
   }

   private static Item stuffedDoughItem() {
      String[] parts = MooncakeMoldItem.STUFFED_DOUGH_FOOD_ID.split(":", 2);
      Identifier id = Identifier.fromNamespaceAndPath(parts[0], parts[1]);
      return BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
   }

   @Override
   public void setRecipe(@NonNull IRecipeLayoutBuilder builder, Display display, @NonNull IFocusGroup focuses) {
      if (display.dough().isEmpty()) {
         builder.addSlot(RecipeIngredientRole.INPUT, 20, 12).setStandardSlotBackground();
      } else {
         builder.addSlot(RecipeIngredientRole.INPUT, 20, 12).setStandardSlotBackground().add(display.dough());
      }

      builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 12).setOutputSlotBackground().add(display.result());
   }

   @Override
   public void draw(@NonNull Display display, @NonNull IRecipeSlotsView recipeSlotsView, @NonNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
      // 箭头中心 x=61 位于两物品中心 (28,20) 与 (94,20) 的正中点
      this.arrow.draw(guiGraphics, 50, 12);
   }

   @Override
   public @NotNull IRecipeType<Display> getRecipeType() {
      return TYPE;
   }

   @Override
   public @NotNull Component getTitle() {
      return Component.translatable("jei.kaleidoscope_chinesefood.mooncake_mold");
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
