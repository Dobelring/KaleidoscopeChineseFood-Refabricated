package com.bmt.kaleidoscope_chinesefood.compat.rrv.pickle_jar;

import cc.cassian.rrv.api.TagUtil;
import cc.cassian.rrv.api.recipe.ReliableServerRecipe;
import cc.cassian.rrv.api.recipe.ReliableServerRecipeType;
import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jspecify.annotations.Nullable;

public class PickleJarServerRecipe implements ReliableServerRecipe {
    public static final ReliableServerRecipeType<PickleJarServerRecipe> TYPE = ReliableServerRecipeType.register(
            Identifier.fromNamespaceAndPath(KaleidoscopeChineseFood.MODID, "pickle_jar"),
            () -> new PickleJarServerRecipe(List.of(), null)
    );
    private List<Ingredient> ingredients;
    private ItemStack result;

    public PickleJarServerRecipe(List<Ingredient> ingredients, @Nullable ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }

    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public ItemStack getResult() {
        return this.result;
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Ingredient ingredient : this.ingredients) {
            list.add(TagUtil.writeIngredient(ingredient));
        }
        tag.put("ingredients", list);
        tag.put("result", TagUtil.encodeItemStackOnServer(this.result));
    }

    @Override
    public void loadFromTag(CompoundTag tag) {
        List<Ingredient> inputs = new ArrayList<>();
        ListTag list = tag.getListOrEmpty("ingredients");
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i).orElseGet(CompoundTag::new);
            inputs.add(TagUtil.readIngredient(entry));
        }
        this.ingredients = inputs;
        this.result = TagUtil.decodeItemStackOnServer(tag.getCompound("result").orElseGet(CompoundTag::new));
    }

    @Override
    public ReliableServerRecipeType<? extends ReliableServerRecipe> getRecipeType() {
        return TYPE;
    }
}
