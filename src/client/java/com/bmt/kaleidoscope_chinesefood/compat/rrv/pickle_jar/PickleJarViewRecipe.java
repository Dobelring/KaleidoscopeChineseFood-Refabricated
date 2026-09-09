package com.bmt.kaleidoscope_chinesefood.compat.rrv.pickle_jar;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class PickleJarViewRecipe implements ReliableClientRecipe {
    private final Identifier id;
    private final List<SlotContent> ingredients;
    private final SlotContent result;

    public PickleJarViewRecipe(Identifier id, PickleJarRecipe recipe) {
        this.id = id;
        this.ingredients = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.ingredients.add(i < recipe.getIngredients().size()
                    ? SlotContent.of(recipe.getIngredients().get(i))
                    : SlotContent.of());
        }
        this.result = SlotContent.of(recipe.getOutput());
    }

    @Override
    public ReliableClientRecipeType getType() {
        return PickleJarViewType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        for (int i = 0; i < 4; i++) {
            slotFillContext.bindSlot(i, this.ingredients.get(i));
        }
        slotFillContext.bindSlot(4, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return this.ingredients;
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }
}
