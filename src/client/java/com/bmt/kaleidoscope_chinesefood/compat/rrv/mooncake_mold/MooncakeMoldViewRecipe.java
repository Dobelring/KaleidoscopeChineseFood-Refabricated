package com.bmt.kaleidoscope_chinesefood.compat.rrv.mooncake_mold;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * 月饼模具虚拟条目：厨房乐事夹心面团 → 生月饼。无 RecipeManager 配方对应，
 * 由 ModRRVPlugin 直接加入客户端列表。
 */
public class MooncakeMoldViewRecipe implements ReliableClientRecipe {
    private final SlotContent stuffedDough;
    private final SlotContent result;

    private MooncakeMoldViewRecipe(SlotContent stuffedDough, SlotContent result) {
        this.stuffedDough = stuffedDough;
        this.result = result;
    }

    public static MooncakeMoldViewRecipe virtual() {
        return new MooncakeMoldViewRecipe(
                SlotContent.of(ModItems.RAW_MOONCAKE.getDefaultInstance().isEmpty()
                        ? ItemStack.EMPTY : stuffedDoughOrEmpty()),
                SlotContent.of(ModItems.RAW_MOONCAKE.getDefaultInstance())
        );
    }

    private static ItemStack stuffedDoughOrEmpty() {
        // 与 MooncakeMoldItem.STUFFED_DOUGH_FOOD_ID 一致，运行时物品由 cookery 注册
        var id = net.minecraft.resources.Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "stuffed_dough_food");
        var item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(id).orElse(net.minecraft.world.item.Items.AIR);
        return item == net.minecraft.world.item.Items.AIR ? ItemStack.EMPTY : item.getDefaultInstance();
    }

    @Override
    public ReliableClientRecipeType getType() {
        return MooncakeMoldViewType.INSTANCE;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, this.stuffedDough);
        slotFillContext.bindSlot(1, this.result);
    }

    @Override
    public List<SlotContent> getIngredients() {
        return List.of(this.stuffedDough);
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(this.result);
    }
}
