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

public class RefrigeratingRecipe extends BaseProcessingRecipe {
    public RefrigeratingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int baseTime) {
        super(id, input, output, baseTime);
    }

    @NotNull
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.REFRIGERATING_SERIALIZER;
    }

    @NotNull
    public RecipeType<?> getType() {
        return ModRecipeTypes.REFRIGERATING;
    }

    public static class Serializer extends BaseProcessingRecipe.Serializer<RefrigeratingRecipe> {
        public Serializer() {
        }

        @NotNull
        public RefrigeratingRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            int time = GsonHelper.getAsInt(pSerializedRecipe, "base_time", 100);
            return new RefrigeratingRecipe(pRecipeId, ingredient, result, time);
        }

        protected RefrigeratingRecipe createRecipe(ResourceLocation id, Ingredient input, ItemStack output, int baseTime) {
            return new RefrigeratingRecipe(id, input, output, baseTime);
        }
    }
}
