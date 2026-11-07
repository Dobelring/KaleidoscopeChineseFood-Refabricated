package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * 官方 1.1.16 新增（changelog 未列）：冷藏时把输入物品 NBT 里 propagate_nbt 列出的键
 * （如酒馆的 BrewLevel 冲泡等级）复制到输出物品上——用于 smc:ice_tea → liquor cool_tea。
 */
public class RefrigeratingBrewLevelRecipe extends RefrigeratingRecipe {
    private final List<String> propagateNbtKeys;

    public RefrigeratingBrewLevelRecipe(ResourceLocation id, Ingredient input, ItemStack output, int baseTime, List<String> propagateNbtKeys) {
        super(id, input, output, baseTime);
        this.propagateNbtKeys = Collections.unmodifiableList(propagateNbtKeys);
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull SimpleContainer container, @NotNull net.minecraft.core.RegistryAccess registryAccess) {
        ItemStack result = this.output.copy();
        ItemStack inputStack = container.getItem(0);
        if (!inputStack.isEmpty() && inputStack.hasTag() && !this.propagateNbtKeys.isEmpty()) {
            CompoundTag inputTag = inputStack.getTag();
            CompoundTag resultTag = result.getOrCreateTag();

            for (String key : this.propagateNbtKeys) {
                if (inputTag.contains(key)) {
                    resultTag.put(key, inputTag.get(key).copy());
                }
            }
        }

        return result;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.REFRIGERATING_BREW_LEVEL_SERIALIZER;
    }

    public List<String> getPropagateNbtKeys() {
        return this.propagateNbtKeys;
    }

    public static class Serializer extends BaseProcessingRecipe.Serializer<RefrigeratingBrewLevelRecipe> {
        public Serializer() {
        }

        @NotNull
        @Override
        public RefrigeratingBrewLevelRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            int time = GsonHelper.getAsInt(json, "base_time", 100);
            List<String> propagateKeys = new ArrayList<>();
            JsonArray arr = GsonHelper.getAsJsonArray(json, "propagate_nbt", null);
            if (arr != null) {
                for (JsonElement el : arr) {
                    propagateKeys.add(el.getAsString());
                }
            }

            return new RefrigeratingBrewLevelRecipe(recipeId, ingredient, result, time, propagateKeys);
        }

        @NotNull
        @Override
        public RefrigeratingBrewLevelRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            int time = buffer.readInt();
            int keyCount = buffer.readVarInt();
            List<String> propagateKeys = new ArrayList<>(keyCount);

            for (int i = 0; i < keyCount; i++) {
                propagateKeys.add(buffer.readUtf());
            }

            return new RefrigeratingBrewLevelRecipe(recipeId, ingredient, result, time, propagateKeys);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RefrigeratingBrewLevelRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
            buffer.writeInt(recipe.baseTime);
            buffer.writeVarInt(recipe.propagateNbtKeys.size());

            for (String key : recipe.propagateNbtKeys) {
                buffer.writeUtf(key);
            }
        }

        @Override
        protected RefrigeratingBrewLevelRecipe createRecipe(ResourceLocation id, Ingredient input, ItemStack output, int baseTime) {
            return new RefrigeratingBrewLevelRecipe(id, input, output, baseTime, Collections.emptyList());
        }
    }
}
