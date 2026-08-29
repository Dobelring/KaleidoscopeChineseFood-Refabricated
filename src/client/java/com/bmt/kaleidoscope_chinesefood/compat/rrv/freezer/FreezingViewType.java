package com.bmt.kaleidoscope_chinesefood.compat.rrv.freezer;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class FreezingViewType implements ReliableClientRecipeType {
    public static final FreezingViewType INSTANCE = new FreezingViewType();

    private FreezingViewType() {
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("jei.kaleidoscope_chinesefood.freezing");
    }

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 80;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return Identifier.fromNamespaceAndPath(KaleidoscopeChineseFood.MODID, "textures/gui/eiv/freezing.png");
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 16, 21);
        slotDefinition.addItemSlot(1, 91, 30);
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("kaleidoscope_freezing");
    }

    @Override
    public ItemStack getIcon() {
        return ModBlocks.FREEZER.asItem().getDefaultInstance();
    }
}
