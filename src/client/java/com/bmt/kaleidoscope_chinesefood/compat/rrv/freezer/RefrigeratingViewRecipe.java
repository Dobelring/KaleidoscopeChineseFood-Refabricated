package com.bmt.kaleidoscope_chinesefood.compat.rrv.freezer;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class RefrigeratingViewRecipe implements ReliableClientRecipe {
    private final Ingredient ingredient;
    private final ItemStack result;

    public RefrigeratingViewRecipe(RefrigeratingServerRecipe recipe) {
        this.ingredient = recipe.getIngredient();
        this.result = recipe.getResult();
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return RefrigeratingViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, SlotContent.of(this.ingredient));
        slotFillContext.bindSlot(1, SlotContent.of(this.result));
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(SlotContent.of(this.ingredient));
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(SlotContent.of(this.result));
    }
}
