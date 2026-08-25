package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 月饼模具的虚拟展示条目：月饼模具（不消耗）+ 厨房乐事夹心面团 → 生月饼。
 * 实际机制在 MooncakeMoldItem 里以代码实现（双手持物长按右键），
 * 不属于配方系统，因此查看器无法自动感知，这里手动提供展示。
 * 样式与 1.21.1 版一致：空白背景，模具槽为催化剂角色。
 */
public class MooncakeMoldRecipeCategory implements IRecipeCategory<MooncakeMoldRecipeCategory.Display> {
    public static final IRecipeType<Display> TYPE =
            IRecipeType.create(KaleidoscopeChineseFood.id("mooncake_mold"), Display.class);
    private static final int WIDTH = 126;
    private static final int HEIGHT = 40;
    /** 复用 freezer 贴图中的进度箭头区域（与其他分类观感一致） */
    private static final Identifier FREEZER_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/jei/freezer.png");
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable outputSlot;
    private final IDrawable arrow;

    /** 虚拟条目（不对应任何 RecipeManager 配方）。 */
    public record Display(ItemStack mold, ItemStack dough, ItemStack result) {}

    public MooncakeMoldRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(ModItems.MOONCAKE_MOLD);
        // 原版风格的槽位方框：输入格普通样式，输出格带高亮
        this.slot = guiHelper.getSlotDrawable();
        this.outputSlot = guiHelper.getOutputSlot();
        this.arrow = guiHelper.createDrawable(FREEZER_TEXTURE, 49, 22, 25, 15);
    }

    public static Display createDisplay() {
        return new Display(
                new ItemStack(ModItems.MOONCAKE_MOLD),
                new ItemStack(stuffedDoughItem()),
                new ItemStack(ModItems.RAW_MOONCAKE)
        );
    }

    private static Item stuffedDoughItem() {
        return BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "stuffed_dough_food"));
    }

    public IRecipeType<Display> getRecipeType() {
        return TYPE;
    }

    public void draw(Display display, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        // 槽位方框：输入格 18x18 在物品坐标外扩 1px；输出格 26x26，物品在框内 +5,+5
        this.slot.draw(guiGraphics, 9, 11);
        this.slot.draw(guiGraphics, 33, 11);
        this.outputSlot.draw(guiGraphics, 91, 6);
        this.arrow.draw(guiGraphics, 59, 12);
    }

    public void setRecipe(IRecipeLayoutBuilder builder, Display display, IFocusGroup focuses) {
        // JEI 29.x 移除了 CATALYST 角色，CRAFTING_STATION 即旧版催化剂（工具、不消耗）
        IRecipeSlotBuilder mold = builder.addSlot(RecipeIngredientRole.CRAFTING_STATION, 10, 12)
                .add(display.mold());
        // 模具不会被消耗，悬浮提示说明
        mold.addRichTooltipCallback((view, tooltip) ->
                tooltip.add(Component.translatable("jei.kaleidoscope_chinesefood.mold_keep")));

        builder.addSlot(RecipeIngredientRole.INPUT, 34, 12)
                .add(display.dough());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 11)
                .add(display.result());
    }

    public Component getTitle() {
        return Component.translatable("jei.kaleidoscope_chinesefood.mooncake_mold");
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
