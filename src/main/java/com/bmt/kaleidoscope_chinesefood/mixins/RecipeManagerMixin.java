package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 与原版 1.1.10 一致：配方应用完成后，为 c:crops/corn 标签里的每个玉米作物
 * 动态生成一条"1 玉米串 → 6×该作物"的无序拆解配方（JSON 无法表达按输入变化产出）。
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow
    @Mutable
    private Multimap<RecipeType<?>, RecipeHolder<?>> byType;

    @Shadow
    @Mutable
    private Map<ResourceLocation, RecipeHolder<?>> byName;

    @Inject(method = "apply", at = @At("TAIL"))
    private void kaleidoscope_chinesefood$addCornRistraUncraftRecipes(
            Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci
    ) {
        Item inputItem = ModItems.CORN_RISTRA;
        TagLoader<Item> tagLoader = new TagLoader<>(BuiltInRegistries.ITEM::getOptional, "tags/item");
        Map<ResourceLocation, Collection<Item>> tagMap = tagLoader.loadAndBuild(resourceManager);
        ResourceLocation cornTagId = ResourceLocation.fromNamespaceAndPath("c", "crops/corn");
        Collection<Item> cornItems = tagMap.get(cornTagId);
        if (cornItems != null && !cornItems.isEmpty()) {
            Multimap<RecipeType<?>, RecipeHolder<?>> mutableByType = ArrayListMultimap.create();
            mutableByType.putAll(this.byType);
            Map<ResourceLocation, RecipeHolder<?>> mutableByName = new HashMap<>(this.byName);

            for (Item cornItem : cornItems) {
                if (cornItem != inputItem) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(cornItem);
                    ResourceLocation recipeId = KaleidoscopeChineseFood.id("corn_ristra_to_" + itemId.getNamespace() + "_" + itemId.getPath());
                    if (mutableByName.containsKey(recipeId)) {
                        continue;
                    }
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    ingredients.add(Ingredient.of(inputItem));
                    ItemStack output = new ItemStack(cornItem, 6);
                    ShapelessRecipe recipe = new ShapelessRecipe("", CraftingBookCategory.MISC, output, ingredients);
                    RecipeHolder<ShapelessRecipe> holder = new RecipeHolder<>(recipeId, recipe);
                    mutableByType.put(RecipeType.CRAFTING, holder);
                    mutableByName.put(recipeId, holder);
                }
            }

            this.byType = ImmutableMultimap.copyOf(mutableByType);
            this.byName = ImmutableMap.copyOf(mutableByName);
        }
    }
}
