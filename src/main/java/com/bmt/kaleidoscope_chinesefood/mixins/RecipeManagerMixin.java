package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.google.gson.JsonElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RecipeManager.class})
public abstract class RecipeManagerMixin {
    @Shadow
    private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes;

    public RecipeManagerMixin() {
    }

    @Inject(
        method = {"apply"},
        at = {@At("TAIL")}
    )
    private void kaleidoscope_chinesefood$addCornRistraUncraftRecipes(
        Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci
    ) {
        Item inputItem = ModBlocks.CORN_RISTRA.asItem();
        TagLoader<Item> tagLoader = new TagLoader<Item>(BuiltInRegistries.ITEM::getOptional, "tags/items");
        Map<ResourceLocation, Collection<Item>> tagMap = tagLoader.loadAndBuild(resourceManager);
        ResourceLocation cornTagId = ResourceLocation.tryBuild("forge", "crops/corn");
        Collection<Item> cornItems = cornTagId == null ? null : tagMap.get(cornTagId);
        if (cornItems != null && !cornItems.isEmpty()) {
            Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> mutableRecipes = new HashMap<>();

            for (Entry<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> entry : this.recipes.entrySet()) {
                mutableRecipes.put(entry.getKey(), new HashMap<>(entry.getValue()));
            }

            Map<ResourceLocation, Recipe<?>> craftingRecipes = mutableRecipes.computeIfAbsent(RecipeType.CRAFTING, k -> new HashMap<>());

            for (Item cornItem : cornItems) {
                if (cornItem != inputItem) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(cornItem);
                    ResourceLocation recipeId = KaleidoscopeChineseFood.id("corn_ristra_to_" + itemId.getNamespace() + "_" + itemId.getPath());
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    ingredients.add(Ingredient.of(new ItemLike[]{inputItem}));
                    ItemStack output = new ItemStack(cornItem, 6);
                    ShapelessRecipe recipe = new ShapelessRecipe(recipeId, "", CraftingBookCategory.MISC, output, ingredients);
                    craftingRecipes.put(recipeId, recipe);
                }
            }

            this.recipes = mutableRecipes;
        }
    }
}
