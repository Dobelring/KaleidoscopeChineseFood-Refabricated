package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.misc.CornBlock;
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
 * 1.1.10 起:为 c:crops/corn 标签里的每个玉米作物动态生成"1 玉米串 → 6×该作物"的
 * 无序拆解配方(JSON 无法表达按输入变化产出)。
 * <p>
 * 1.2.0:注入方式从 TAIL 直塞配方对象改为 HEAD 注入 JSON——合成配方走原版解析,
 * 可进配方书、可被数据包覆盖(对齐官方)。
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Unique
    private static final ResourceLocation CORN_TAG = ResourceLocation.fromNamespaceAndPath("c", "crops/corn");

    @Inject(method = "apply", at = @At("HEAD"))
    private void kaleidoscope_chinesefood$addCornRistraUncraftRecipes(
            Map<ResourceLocation, JsonElement> recipesJson, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci
    ) {
        Item ristraItem = ((CornBlock)ModBlocks.CORN_RISTRA).asItem();
        ResourceLocation ristraId = BuiltInRegistries.ITEM.getKey(ristraItem);
        TagLoader<Item> tagLoader = new TagLoader<>(BuiltInRegistries.ITEM::getOptional, "tags/item");
        Collection<Item> cornItems = tagLoader.loadAndBuild(resourceManager).get(CORN_TAG);
        if (cornItems == null) {
            return;
        }

        for (Item cornItem : cornItems) {
            if (cornItem != ristraItem) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(cornItem);
                ResourceLocation recipeId = KaleidoscopeChineseFood.id("corn_ristra_to_" + itemId.getNamespace() + "_" + itemId.getPath());
                recipesJson.put(recipeId, kaleidoscope_chinesefood$createShapelessRecipeJson(ristraId, itemId, 6));
            }
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
        result.addProperty("id", outputId.toString());
        result.addProperty("count", count);
        recipe.add("result", result);
        return recipe;
    }
}
