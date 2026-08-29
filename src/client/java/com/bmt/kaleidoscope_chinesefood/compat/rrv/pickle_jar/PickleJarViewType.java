package com.bmt.kaleidoscope_chinesefood.compat.rrv.pickle_jar;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class PickleJarViewType implements ReliableClientRecipeType {
    public static final PickleJarViewType INSTANCE = new PickleJarViewType();

    private PickleJarViewType() {
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("jei.kaleidoscope_chinesefood.pickle_jar");
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
        return Identifier.fromNamespaceAndPath(KaleidoscopeChineseFood.MODID, "textures/gui/eiv/pickle_jar.png");
    }

    @Override
    public int getSlotCount() {
        return 5;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        // 2x2 输入格 + 结果
        slotDefinition.addItemSlot(0, 16, 21);
        slotDefinition.addItemSlot(1, 34, 21);
        slotDefinition.addItemSlot(2, 16, 39);
        slotDefinition.addItemSlot(3, 34, 39);
        slotDefinition.addItemSlot(4, 91, 30);
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("kaleidoscope_pickle_jar");
    }

    @Override
    public ItemStack getIcon() {
        return ModBlocks.PICKLE_JAR.asItem().getDefaultInstance();
    }
}
