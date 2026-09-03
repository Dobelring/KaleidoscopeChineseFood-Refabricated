package com.bmt.kaleidoscope_chinesefood.compat.rrv.mooncake_mold;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeMoldItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 月饼模具为虚拟配方（无 JSON），数据由模具物品常量构建。
 */
public class MooncakeMoldServerRecipe implements ReliableServerRecipe {
    public static final ReliableServerRecipeType<MooncakeMoldServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.fromNamespaceAndPath(KaleidoscopeChineseFood.MODID, "mooncake_mold"),
            MooncakeMoldServerRecipe::new
    );
    private ItemStack stuffedDough;
    private ItemStack result;

    public MooncakeMoldServerRecipe() {
        this.stuffedDough = ItemStack.EMPTY;
        this.result = ItemStack.EMPTY;
    }

    private MooncakeMoldServerRecipe(ItemStack stuffedDough, ItemStack result) {
        this.stuffedDough = stuffedDough;
        this.result = result;
    }

    public static MooncakeMoldServerRecipe virtual() {
        Item dough = stuffedDoughItem();
        return new MooncakeMoldServerRecipe(
                dough != Items.AIR ? dough.getDefaultInstance() : ItemStack.EMPTY,
                ModItems.RAW_MOONCAKE.getDefaultInstance()
        );
    }

    private static Item stuffedDoughItem() {
        String[] parts = MooncakeMoldItem.STUFFED_DOUGH_FOOD_ID.split(":", 2);
        Identifier id = Identifier.fromNamespaceAndPath(parts[0], parts[1]);
        return BuiltInRegistries.ITEM.getValue(id);
    }

    public ItemStack getStuffedDough() {
        return this.stuffedDough;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        tag.put("stuffed_dough", TagUtil.encodeItemStackOnServer(this.stuffedDough));
        tag.put("result", TagUtil.encodeItemStackOnServer(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        this.stuffedDough = TagUtil.decodeItemStackOnServer(tag.getCompound("stuffed_dough").orElseGet(CompoundTag::new));
        this.result = TagUtil.decodeItemStackOnServer(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
