package com.bmt.kaleidoscope_chinesefood.compat.rrv.mooncake_mold;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class MooncakeMoldViewRecipe implements ReliableClientRecipe {
    private final ItemStack stuffedDough;
    private final ItemStack result;

    public MooncakeMoldViewRecipe(MooncakeMoldServerRecipe recipe) {
        this.stuffedDough = recipe.getStuffedDough();
        this.result = recipe.getResult();
    }

    @Override
    public ReliableClientRecipeType getViewType() {
        return MooncakeMoldViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, SlotContent.of(this.stuffedDough));
        slotFillContext.bindSlot(1, SlotContent.of(this.result));
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(SlotContent.of(this.stuffedDough));
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(SlotContent.of(this.result));
    }
}
