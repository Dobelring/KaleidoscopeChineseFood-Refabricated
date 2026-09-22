package com.bmt.kaleidoscope_chinesefood.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class BaseProcessingRecipe implements Recipe<SimpleContainer> {
    protected final ResourceLocation id;
    protected final Ingredient input;
    protected final ItemStack output;
    protected final int baseTime;
    public static final int TIME_MULTIPLIER = 5;
    public static final int DEFAULT_BASE_TIME = 100;

    public BaseProcessingRecipe(ResourceLocation id, Ingredient input, ItemStack output, int baseTime) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.baseTime = baseTime;
    }

    public int calculateProcessingTime(int count) {
        return this.baseTime + Math.max(0, (count - 1) * 5);
    }

    public boolean matches(@NotNull SimpleContainer pContainer, @NotNull Level pLevel) {
        return matchesIngredient(this.input, pContainer.getItem(0));
    }

    public static boolean matchesIngredient(@NotNull Ingredient ingredient, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else if (ingredient.getClass() != Ingredient.class) {
            return ingredient.test(stack);
        } else {
            for (ItemStack ingredientStack : ingredient.getItems()) {
                if (ingredientStack.hasTag()) {
                    if (ItemStack.isSameItemSameTags(ingredientStack, stack)) {
                        return true;
                    }
                } else if (ingredientStack.getItem() == stack.getItem()) {
                    return true;
                }
            }

            return false;
        }
    }

    @NotNull
    public ItemStack assemble(@NotNull SimpleContainer pContainer, @NotNull RegistryAccess pRegistryAccess) {
        return this.output.copy();
    }

    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @NotNull
    public ItemStack getResultItem(@NotNull RegistryAccess pRegistryAccess) {
        return this.output;
    }

    @NotNull
    public ResourceLocation getId() {
        return this.id;
    }

    @NotNull
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.input);
        return ingredients;
    }

    public abstract static class Serializer<T extends BaseProcessingRecipe> implements RecipeSerializer<T> {
        public Serializer() {
        }

        protected abstract T createRecipe(ResourceLocation var1, Ingredient var2, ItemStack var3, int var4);

        @NotNull
        public T fromNetwork(@NotNull ResourceLocation pRecipeId, @NotNull FriendlyByteBuf pBuffer) {
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            int time = pBuffer.readInt();
            return this.createRecipe(pRecipeId, ingredient, result, time);
        }

        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, @NotNull T pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.output);
            pBuffer.writeInt(pRecipe.baseTime);
        }
    }
}
