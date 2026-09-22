package com.bmt.kaleidoscope_chinesefood.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PickleJarRecipe implements Recipe<SimpleContainer> {
    public static final int DEFAULT_FERMENT_TIME = 200;
    private final ResourceLocation id;
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final int fermentTime;

    public PickleJarRecipe(ResourceLocation id, NonNullList<Ingredient> inputs, ItemStack output, int fermentTime) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
        this.fermentTime = fermentTime;
    }

    public boolean matches(SimpleContainer container, Level level) {
        List<ItemStack> containerItems = new ArrayList<>();

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.getCount() != 4) {
                    return false;
                }

                containerItems.add(stack.copy());
            }
        }

        if (containerItems.size() != this.inputs.size()) {
            return false;
        } else {
            for (Ingredient ingredient : this.inputs) {
                boolean found = false;

                for (int ix = 0; ix < containerItems.size(); ix++) {
                    if (BaseProcessingRecipe.matchesIngredient(ingredient, containerItems.get(ix))) {
                        containerItems.remove(ix);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    return false;
                }
            }

            return true;
        }
    }

    public ItemStack assemble(SimpleContainer container, RegistryAccess access) {
        return this.output.copy();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public ItemStack getResultItem(RegistryAccess access) {
        return this.output;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.PICKLE_JAR_SERIALIZER;
    }

    public RecipeType<?> getType() {
        return ModRecipes.PICKLE_JAR_TYPE;
    }

    public int getFermentTime() {
        return this.fermentTime;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    public static class Serializer implements RecipeSerializer<PickleJarRecipe> {
        public Serializer() {
        }

        public PickleJarRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            int fermentTime = GsonHelper.getAsInt(json, "fermentTime", 200);
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredientsJson.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredientsJson.get(i)));
            }

            return new PickleJarRecipe(id, inputs, output, fermentTime);
        }

        @Nullable
        public PickleJarRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readInt();
            NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);

            for (int i = 0; i < size; i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            int fermentTime = buf.readInt();
            return new PickleJarRecipe(id, inputs, output, fermentTime);
        }

        public void toNetwork(FriendlyByteBuf buf, PickleJarRecipe recipe) {
            buf.writeInt(recipe.inputs.size());

            for (Ingredient ingredient : recipe.inputs) {
                ingredient.toNetwork(buf);
            }

            buf.writeItem(recipe.output);
            buf.writeInt(recipe.fermentTime);
        }
    }
}
