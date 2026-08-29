package com.bmt.kaleidoscope_chinesefood.compat.rrv.pickle_jar;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class PickleJarViewRecipe implements ReliableClientRecipe {
    private final List<Ingredient> ingredients;
    private final ItemStack result;

    public PickleJarViewRecipe(PickleJarServerRecipe recipe) {
        this.ingredients = recipe.getIngredients();
        this.result = recipe.getResult();
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return PickleJarViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        for (int i = 0; i < 4 && i < this.ingredients.size(); i++) {
            slotFillContext.bindSlot(i, SlotContent.of(this.ingredients.get(i)));
        }
        slotFillContext.bindSlot(4, SlotContent.of(this.result));
    }

    @Override
    public List<SlotContent> getIngredients() {
        return this.ingredients.stream().map(SlotContent::of).toList();
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(SlotContent.of(this.result));
    }
}
