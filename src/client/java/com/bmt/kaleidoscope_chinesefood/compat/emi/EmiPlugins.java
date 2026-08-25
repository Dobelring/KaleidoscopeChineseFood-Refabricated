package com.bmt.kaleidoscope_chinesefood.compat.emi;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.BaseProcessingRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeMoldItem;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * EMI 原生插件：腌菜罐 / 冷冻 / 冷藏。
 * <p>
 * EMI 通过 fabric.mod.json 的 "emi" 入口点发现本类；
 * EMI 的 jemi 桥会跳过已带原生 EMI 插件的模组的 JEI 插件，因此 JEI+EMI 双装不会重复显示。
 */
public class EmiPlugins implements EmiPlugin {
    public static final EmiRecipeCategory PICKLE_JAR = category("pickle_jar", ModBlocks.PICKLE_JAR,
            "jei.kaleidoscope_chinesefood.pickle_jar");
    public static final EmiRecipeCategory FREEZING = category("freezing", ModBlocks.FREEZER,
            "jei.kaleidoscope_chinesefood.freezing");
    public static final EmiRecipeCategory REFRIGERATING = category("refrigerating", ModBlocks.FREEZER,
            "jei.kaleidoscope_chinesefood.refrigerating");
    public static final EmiRecipeCategory MOONCAKE_MOLD = category("mooncake_mold", ModItems.MOONCAKE_MOLD,
            "jei.kaleidoscope_chinesefood.mooncake_mold");

    private static final Block[] FREEZERS = {
            ModBlocks.FREEZER, ModBlocks.FREEZER_GREEN, ModBlocks.FREEZER_ORANGE,
            ModBlocks.FREEZER_LIGHT_GRAY, ModBlocks.FREEZER_PINK, ModBlocks.FREEZER_LIGHT_BLUE,
            ModBlocks.FREEZER_YELLOW
    };

    private static EmiRecipeCategory category(String path, ItemLike icon, String nameKey) {
        // EMI 默认按 emi.category.<命名空间>.<路径> 找翻译键，缺键时会显示原始键名；
        // 这里覆写 getName 直接复用 JEI 分类已有的翻译键（中英文均有现成译文）。
        return new EmiRecipeCategory(KaleidoscopeChineseFood.id("emi/" + path), EmiStack.of(icon)) {
            @Override
            public Component getName() {
                return Component.translatable(nameKey);
            }
        };
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(PICKLE_JAR);
        registry.addCategory(FREEZING);
        registry.addCategory(REFRIGERATING);
        registry.addCategory(MOONCAKE_MOLD);

        registry.addWorkstation(PICKLE_JAR, EmiStack.of(ModBlocks.PICKLE_JAR));
        registry.addWorkstation(MOONCAKE_MOLD, EmiStack.of(ModItems.MOONCAKE_MOLD));
        for (Block freezer : FREEZERS) {
            EmiStack stack = EmiStack.of(freezer);
            registry.addWorkstation(FREEZING, stack);
            registry.addWorkstation(REFRIGERATING, stack);
        }

        var manager = registry.getRecipeManager();
        for (RecipeHolder<PickleJarRecipe> holder : manager.getAllRecipesFor(ModRecipes.PICKLE_JAR_TYPE)) {
            PickleJarRecipe recipe = holder.value();
            registry.addRecipe(new ProcessingEmiRecipe(PICKLE_JAR, holder.id(),
                    recipe.getIngredients(), recipe.getOutput(), recipe.getFermentTime(), true));
        }

        for (RecipeHolder<? extends BaseProcessingRecipe> holder : manager.getAllRecipesFor(ModRecipes.FREEZING_TYPE)) {
            BaseProcessingRecipe recipe = holder.value();
            registry.addRecipe(new ProcessingEmiRecipe(FREEZING, holder.id(),
                    recipe.getIngredients(), recipe.getOutput(), 0, false));
        }
        for (RecipeHolder<? extends BaseProcessingRecipe> holder : manager.getAllRecipesFor(ModRecipes.REFRIGERATING_TYPE)) {
            BaseProcessingRecipe recipe = holder.value();
            registry.addRecipe(new ProcessingEmiRecipe(REFRIGERATING, holder.id(),
                    recipe.getIngredients(), recipe.getOutput(), 0, false));
        }

        // 月饼模具是纯代码交互（非配方系统），提供静态虚拟展示条目
        registry.addRecipe(new MoldEmiRecipe());
    }

    private static class ProcessingEmiRecipe extends BasicEmiRecipe {
        private final List<EmiIngredient> slots;
        private final int fermentTicks;
        private final boolean grid4;

        ProcessingEmiRecipe(EmiRecipeCategory category, ResourceLocation id,
                            List<Ingredient> ingredients, ItemStack output,
                            int fermentTicks, boolean grid4) {
            super(category, id, grid4 ? 140 : 116, grid4 ? 60 : 54);
            this.fermentTicks = fermentTicks;
            this.grid4 = grid4;
            this.slots = new ArrayList<>();
            for (Ingredient ingredient : ingredients) {
                if (ingredient.isEmpty()) {
                    continue;
                }
                List<EmiStack> stacks = new ArrayList<>();
                for (ItemStack stack : ingredient.getItems()) {
                    // 腌菜罐配方每格正好消耗 4 个（matches 里强制 count==4），显示 x4 与 JEI 版一致
                    ItemStack display = stack.copy();
                    if (grid4) {
                        display.setCount(4);
                    }
                    stacks.add(EmiStack.of(display));
                }
                EmiIngredient slot = stacks.size() == 1 ? stacks.getFirst() : EmiIngredient.of(stacks);
                this.slots.add(slot);
                this.inputs.add(slot);
            }
            this.outputs.add(EmiStack.of(output));
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            if (this.grid4) {
                for (int i = 0; i < 4; i++) {
                    int x = i % 2 * 17 + 12;
                    int y = i / 2 * 17 + 14;
                    widgets.addSlot(i < this.slots.size() ? this.slots.get(i) : EmiStack.EMPTY, x, y);
                }
                widgets.addFillingArrow(65, 23, Math.max(1000, this.fermentTicks * 50));
                widgets.addSlot(this.outputs.getFirst(), 113, 23).recipeContext(this);
                int seconds = Math.max(1, this.fermentTicks / 20);
                widgets.addText(
                        Component.translatable("jei.kaleidoscope_chinesefood.ferment_time", seconds + "s"),
                        12, 49, 0x555555, false);
            } else {
                widgets.addSlot(this.slots.getFirst(), 12, 21);
                widgets.addFillingArrow(58, 21, 2000);
                widgets.addSlot(this.outputs.getFirst(), 87, 21).recipeContext(this);
            }
        }
    }

    /** 虚拟展示：月饼模具（不消耗）+ 厨房乐事夹心面团 → 生月饼。 */
    private static class MoldEmiRecipe extends BasicEmiRecipe {
        MoldEmiRecipe() {
            super(MOONCAKE_MOLD, KaleidoscopeChineseFood.id("emi/mooncake_mold"), 140, 46);
            this.inputs.add(EmiStack.of(ModItems.MOONCAKE_MOLD));
            Item dough = stuffedDoughItem();
            this.inputs.add(dough != Items.AIR ? EmiStack.of(dough) : EmiStack.EMPTY);
            this.outputs.add(EmiStack.of(ModItems.RAW_MOONCAKE));
        }

        private static Item stuffedDoughItem() {
            String[] parts = MooncakeMoldItem.STUFFED_DOUGH_FOOD_ID.split(":", 2);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
            return (Item) BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
        }

        @Override
        public void addWidgets(WidgetHolder widgets) {
            widgets.addSlot(this.inputs.getFirst(), 36, 4);
            widgets.addSlot(this.inputs.get(1), 36, 26);
            widgets.addFillingArrow(68, 21, 2000);
            widgets.addSlot(this.outputs.getFirst(), 106, 19).recipeContext(this);
        }
    }
}
