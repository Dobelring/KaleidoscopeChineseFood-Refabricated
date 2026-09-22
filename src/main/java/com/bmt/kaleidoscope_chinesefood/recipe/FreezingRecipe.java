package com.bmt.kaleidoscope_chinesefood.recipe;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipeSerializers;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipeTypes;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

public class FreezingRecipe extends BaseProcessingRecipe {
    public FreezingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int baseTime) {
        super(id, input, output, baseTime);
    }

    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FREEZING_SERIALIZER;
    }

    @NotNull
    public RecipeType<?> getType() {
        return ModRecipeTypes.FREEZING;
    }

    public static class Serializer extends BaseProcessingRecipe.Serializer<FreezingRecipe> {
        public Serializer() {
        }

        @NotNull
        public FreezingRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            int time = GsonHelper.getAsInt(pSerializedRecipe, "base_time", 100);
            return new FreezingRecipe(pRecipeId, ingredient, result, time);
        }

        protected FreezingRecipe createRecipe(ResourceLocation id, Ingredient input, ItemStack output, int baseTime) {
            return new FreezingRecipe(id, input, output, baseTime);
        }
    }
}
