package com.bmt.kaleidoscope_chinesefood.compat.jei;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MooncakeMoldRecipeCategory implements IRecipeCategory<MooncakeMoldRecipe> {
    public static final RecipeType<MooncakeMoldRecipe> TYPE = RecipeType.create("kaleidoscope_chinesefood", "mooncake_mold", MooncakeMoldRecipe.class);
    private static final ResourceLocation BACKGROUND_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/jei/freezer.png");
    private static final int BACKGROUND_WIDTH = 116;
    private static final int BACKGROUND_HEIGHT = 40;
    private final IDrawable background;
    private final IDrawable icon;

    public MooncakeMoldRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BACKGROUND_TEXTURE, 0, 8, 116, 40);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.MOONCAKE_MOLD));
    }

    @Override
    public RecipeType<MooncakeMoldRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.kaleidoscope_chinesefood.mooncake_mold");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MooncakeMoldRecipe recipe, IFocusGroup focuses) {
        ItemStack stuffedDoughFood = new ItemStack(
            BuiltInRegistries.ITEM.get(new ResourceLocation("kaleidoscope_cookery", "stuffed_dough_food"))
        );
        ItemStack rawMooncake = new ItemStack(ModItems.RAW_MOONCAKE);
        builder.addSlot(RecipeIngredientRole.INPUT, 12, 13).addItemStack(stuffedDoughFood);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 13).addItemStack(rawMooncake);
    }
}
