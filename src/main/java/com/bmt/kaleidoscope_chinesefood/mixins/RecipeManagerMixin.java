package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.2.0：玉米串反解配方从 TAIL 直塞对象改为 HEAD 注入 JSON——
 * 配方走原版解析（可进配方书、可被数据包覆盖）。
 */
@Mixin({RecipeManager.class})
public abstract class RecipeManagerMixin {
    @Unique
    private static final ResourceLocation CORN_TAG = ResourceLocation.tryBuild("forge", "crops/corn");

    @Inject(
        method = {"apply"},
        at = {@At("HEAD")}
    )
    private void kaleidoscope_chinesefood$addCornRistraUncraftRecipes(
        Map<ResourceLocation, JsonElement> recipesJson, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci
    ) {
        Item ristraItem = ModBlocks.CORN_RISTRA.asItem();
        ResourceLocation ristraId = BuiltInRegistries.ITEM.getKey(ristraItem);
        TagLoader<Item> tagLoader = new TagLoader<Item>(BuiltInRegistries.ITEM::getOptional, "tags/items");
        Collection<Item> cornItems = tagLoader.loadAndBuild(resourceManager).get(CORN_TAG);
        if (cornItems == null || cornItems.isEmpty()) {
            return;
        }

        for (Item cornItem : cornItems) {
            if (cornItem == ristraItem) {
                continue;
            }
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(cornItem);
            ResourceLocation recipeId = KaleidoscopeChineseFood.id("corn_ristra_to_" + itemId.getNamespace() + "_" + itemId.getPath());
            recipesJson.put(recipeId, kaleidoscope_chinesefood$createShapelessRecipeJson(ristraId, itemId, 6));
        }
    }

    @Unique
    private static JsonObject kaleidoscope_chinesefood$createShapelessRecipeJson(ResourceLocation inputId, ResourceLocation outputId, int count) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        recipe.addProperty("category", "misc");
        JsonObject ingredient = new JsonObject();
        ingredient.addProperty("item", inputId.toString());
        JsonArray ingredients = new JsonArray();
        ingredients.add(ingredient);
        recipe.add("ingredients", ingredients);
        JsonObject result = new JsonObject();
        result.addProperty("item", outputId.toString());
        result.addProperty("count", count);
        recipe.add("result", result);
        return recipe;
    }
}
