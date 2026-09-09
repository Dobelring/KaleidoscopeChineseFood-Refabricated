package com.bmt.kaleidoscope_chinesefood.compat.rrv.freezer;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingRecipe;
import java.util.List;
import net.minecraft.resources.Identifier;

public class RefrigeratingViewRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final SlotContent ingredient;
    private final SlotContent result;

    public RefrigeratingViewRecipe(Identifier id, RefrigeratingRecipe recipe) {
        this.id = id;
        this.ingredient = SlotContent.of(recipe.getIngredients().getFirst());
        this.result = SlotContent.of(recipe.getOutput());
    }

    @Override
    public ReliableClientRecipeType getType() {
        return RefrigeratingViewType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.ingredient);
        slotFillContext.bindSlot(1, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.ingredient);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }
}
