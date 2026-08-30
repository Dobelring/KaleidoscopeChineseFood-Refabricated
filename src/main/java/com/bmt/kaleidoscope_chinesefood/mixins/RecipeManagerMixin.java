package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 官方 1.1.10：修复玉米串串配方注册缺失 bug。
 * 原版在 recipe/cron/ 下以静态 json 逐个注册特定 mod 玉米的反解配方（1 串 → 6 玉米），
 * 遇到未覆盖的玉米物品（不在那几个 json 里）就没有反解配方。官方改为在配方加载完成后
 * 动态读取 c:crops/corn 标签，为每个玉米物品自动注册反解配方。
 *
 * 26.x 的 RecipeManager 已重构为 RecipeMap（apply 只接收构建好的 RecipeMap，无 byType/byName
 * 字段），因此这里在 apply TAIL 收集现有配方 + 动态玉米反解配方，重建 RecipeMap 写回。
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Shadow
    private RecipeMap recipes;

    @Unique
    private static final Identifier CORN_TAG = Identifier.fromNamespaceAndPath("c", "crops/corn");

    @Inject(method = "apply", at = @At("TAIL"))
    private void kaleidoscope_chinesefood$addCornRistraRecipes(RecipeMap ignored, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        Item inputItem = ModItems.CORN_RISTRA;
        TagLoader<Item> tagLoader = new TagLoader<>((id, allowEmpty) -> BuiltInRegistries.ITEM.getOptional(id), "tags/item");
        Map<Identifier, List<Item>> tagMap = tagLoader.build(tagLoader.load(resourceManager));
        List<Item> cornItems = tagMap.get(CORN_TAG);
        if (cornItems == null || cornItems.isEmpty()) {
            return;
        }

        List<RecipeHolder<?>> all = new ArrayList<>(this.recipes.values());
        for (Item cornItem : cornItems) {
            if (cornItem == inputItem) {
                continue;
            }
            Identifier itemId = BuiltInRegistries.ITEM.getKey(cornItem);
            Identifier recipeId = KaleidoscopeChineseFood.id("corn_ristra_to_" + itemId.getNamespace() + "_" + itemId.getPath());
            // 避免重复注册（配方 ID 冲突时 RecipeMap.create 会覆盖，这里先跳过）
            boolean exists = false;
            for (RecipeHolder<?> holder : all) {
                if (holder.id().identifier().equals(recipeId)) {
                    exists = true;
                    break;
                }
            }
            if (exists) {
                continue;
            }
            RecipeHolder<ShapelessRecipe> holder = new RecipeHolder<>(
                    ResourceKey.create(Registries.RECIPE, recipeId),
                    new ShapelessRecipe(
                            new Recipe.CommonInfo(false),
                            new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.MISC, ""),
                            ItemStackTemplate.fromStack(new ItemStack(cornItem, 6)),
                            List.of(Ingredient.of(inputItem))
                    )
            );
            all.add(holder);
        }

        this.recipes = RecipeMap.create(all);
    }
}
