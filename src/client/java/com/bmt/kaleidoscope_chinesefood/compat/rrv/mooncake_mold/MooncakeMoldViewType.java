package com.bmt.kaleidoscope_chinesefood.compat.rrv.mooncake_mold;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class MooncakeMoldViewType implements ReliableClientRecipeType {
    public static final MooncakeMoldViewType INSTANCE = new MooncakeMoldViewType();

    private MooncakeMoldViewType() {
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("jei.kaleidoscope_chinesefood.mooncake_mold");
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
        return Identifier.fromNamespaceAndPath(KaleidoscopeChineseFood.MODID, "textures/gui/eiv/mooncake_mold.png");
    }

    @Override
    public int getSlotCount() {
        return 3;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        // 模具 + 馅料 -> 生月饼
        slotDefinition.addItemSlot(0, 34, 12);
        slotDefinition.addItemSlot(1, 34, 47);
        slotDefinition.addItemSlot(2, 91, 30);
    }

    @Override
    public Identifier getId() {
        return Identifier.withDefaultNamespace("kaleidoscope_mooncake_mold");
    }

    @Override
    public ItemStack getIcon() {
        return ModItems.MOONCAKE_MOLD.getDefaultInstance();
    }
}
