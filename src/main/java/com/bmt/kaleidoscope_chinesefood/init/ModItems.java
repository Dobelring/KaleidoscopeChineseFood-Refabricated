package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.item.FirecrackerItem;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeItem;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeMoldItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodOnlyItem;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ModItems {
    public static Item RAW_STEAMED_RICE_ROLLS;
    public static Item RAW_MOONCAKE;
    public static Item SALT_BUCKET;
    public static Item SALT;
    public static Item SICHUAN_WONTON;
    public static Item WONTON_NOODLES;
    public static Item YANGROU_PAOMO;
    public static Item MAOCAI;
    public static Item SEAWEED_EGG_DROP_SOUP;
    public static Item TOMATO_EGG_DROP_SOUP;
    public static Item DOUZHI;
    public static Item CENTURY_EGG_CONGEE;
    public static Item PUMPKIN_PORRIDGE;
    public static Item TWICE_COOKED_PORK;
    public static Item TWICE_COOKED_PORK_RICE;
    public static Item STIR_FRIED_YELLOW_BEEF;
    public static Item STIR_FRIED_YELLOW_BEEF_RICE;
    public static Item BEEF_WITH_SCRAMBLED_EGGS;
    public static Item BEEF_WITH_SCRAMBLED_EGGS_RICE;
    public static Item STIR_FRIED_THREE_FRESH_VEGETABLES;
    public static Item STIR_FRIED_THREE_FRESH_VEGETABLES_RICE;
    public static Item BIG_PLATE_CHICKEN;
    public static Item BIG_PLATE_CHICKEN_NOODLES;
    public static Item TOMATO_EGG_NOODLES;
    public static Item PORK_CHILI_NOODLES;
    public static Item FOUR_JOY_MEATBALLS;
    public static Item STUFFED_EGGPLANT;
    public static Item DRY_POT_POTATOES;
    public static Item DRY_POT_CHICKEN;
    public static Item DRY_POT_SPARE_RIBS;
    public static Item YANGZHOU_FRIED_RICE;
    public static Item LAMB_PILAF;
    public static Item STEAMED_RICE_ROLLS;
    public static Item SAUERKRAUT_BEEF_NOODLES;
    public static Item SALTED_EGG;
    public static Item CENTURY_EGG;
    public static Item CHINESE_SAUERKRAUT;
    public static Item EGGPLANT;
    public static Item EGGPLANT_SEED;
    public static Item YELLOW_CROAKER;
    public static Item MOONCAKE;
    public static Item CORN_RISTRA;
    public static Item MOONCAKE_MOLD;
    public static Item FIRECRACKER;

    public static void register() {
        RAW_STEAMED_RICE_ROLLS = register("raw_steamed_rice_rolls", p -> new Item(p));
        RAW_MOONCAKE = register("raw_mooncake", p -> new Item(p));
        SALT_BUCKET = register("salt_bucket", p -> new Item(p.stacksTo(1).craftRemainder(Items.BUCKET)));
        SALT = register("salt", p -> new BlockItem(ModBlocks.SALT_BLOCK, p));
        SICHUAN_WONTON = register("sichuan_wonton", p -> new BowlFoodOnlyItem(p, ModFoods.SICHUAN_WONTON, ModFoods.SICHUAN_WONTON_C));
        WONTON_NOODLES = register("wonton_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.WONTON_NOODLES, ModFoods.WONTON_NOODLES_C));
        YANGROU_PAOMO = register("yangrou_paomo", p -> new BowlFoodOnlyItem(p, ModFoods.YANGROU_PAOMO, ModFoods.YANGROU_PAOMO_C));
        MAOCAI = register("maocai", p -> new BowlFoodOnlyItem(p, ModFoods.MAOCAI, ModFoods.MAOCAI_C));
        SEAWEED_EGG_DROP_SOUP = register("seaweed_egg_drop_soup", p -> new BowlFoodOnlyItem(p, ModFoods.SEAWEED_EGG_DROP_SOUP, ModFoods.SEAWEED_EGG_DROP_SOUP_C));
        TOMATO_EGG_DROP_SOUP = register("tomato_egg_drop_soup", p -> new BowlFoodOnlyItem(p, ModFoods.TOMATO_EGG_DROP_SOUP, ModFoods.TOMATO_EGG_DROP_SOUP_C));
        DOUZHI = register("douzhi", p -> new BowlFoodOnlyItem(p, ModFoods.DOUZHI, ModFoods.DOUZHI_C));
        CENTURY_EGG_CONGEE = register("century_egg_congee", p -> new BowlFoodOnlyItem(p, ModFoods.CENTURY_EGG_CONGEE, ModFoods.CENTURY_EGG_CONGEE_C));
        PUMPKIN_PORRIDGE = register("pumpkin_porridge", p -> new BowlFoodOnlyItem(p, ModFoods.PUMPKIN_PORRIDGE, ModFoods.PUMPKIN_PORRIDGE_C));
        TWICE_COOKED_PORK = register("twice_cooked_pork", p -> new BowlFoodOnlyItem(p, ModFoods.TWICE_COOKED_PORK, ModFoods.TWICE_COOKED_PORK_C));
        TWICE_COOKED_PORK_RICE = register("twice_cooked_pork_rice", p -> new BowlFoodOnlyItem(p, ModFoods.TWICE_COOKED_PORK_RICE, ModFoods.TWICE_COOKED_PORK_RICE_C));
        STIR_FRIED_YELLOW_BEEF = register("stir_fried_yellow_beef", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_YELLOW_BEEF, ModFoods.STIR_FRIED_YELLOW_BEEF_C));
        STIR_FRIED_YELLOW_BEEF_RICE = register("stir_fried_yellow_beef_rice", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_YELLOW_BEEF_RICE, ModFoods.STIR_FRIED_YELLOW_BEEF_RICE_C));
        BEEF_WITH_SCRAMBLED_EGGS = register("beef_with_scrambled_eggs", p -> new BowlFoodOnlyItem(p, ModFoods.BEEF_WITH_SCRAMBLED_EGGS, ModFoods.BEEF_WITH_SCRAMBLED_EGGS_C));
        BEEF_WITH_SCRAMBLED_EGGS_RICE = register("beef_with_scrambled_eggs_rice", p -> new BowlFoodOnlyItem(p, ModFoods.BEEF_WITH_SCRAMBLED_EGGS_RICE, ModFoods.BEEF_WITH_SCRAMBLED_EGGS_RICE_C));
        STIR_FRIED_THREE_FRESH_VEGETABLES = register("stir_fried_three_fresh_vegetables", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES, ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES_C));
        STIR_FRIED_THREE_FRESH_VEGETABLES_RICE = register("stir_fried_three_fresh_vegetables_rice", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE, ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE_C));
        BIG_PLATE_CHICKEN = register("big_plate_chicken", p -> new BowlFoodOnlyItem(p, ModFoods.BIG_PLATE_CHICKEN, ModFoods.BIG_PLATE_CHICKEN_C));
        BIG_PLATE_CHICKEN_NOODLES = register("big_plate_chicken_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.BIG_PLATE_CHICKEN_NOODLES, ModFoods.BIG_PLATE_CHICKEN_NOODLES_C));
        TOMATO_EGG_NOODLES = register("tomato_egg_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.TOMATO_EGG_NOODLES, ModFoods.TOMATO_EGG_NOODLES_C));
        PORK_CHILI_NOODLES = register("pork_chili_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.PORK_CHILI_NOODLES, ModFoods.PORK_CHILI_NOODLES_C));
        FOUR_JOY_MEATBALLS = register("four_joy_meatballs", p -> new BowlFoodOnlyItem(p, ModFoods.FOUR_JOY_MEATBALLS, ModFoods.FOUR_JOY_MEATBALLS_C));
        STUFFED_EGGPLANT = register("stuffed_eggplant", p -> new BowlFoodOnlyItem(p, ModFoods.STUFFED_EGGPLANT, ModFoods.STUFFED_EGGPLANT_C));
        DRY_POT_POTATOES = register("dry_pot_potatoes", p -> new BowlFoodOnlyItem(p, ModFoods.DRY_POT_POTATOES, ModFoods.DRY_POT_POTATOES_C));
        DRY_POT_CHICKEN = register("dry_pot_chicken", p -> new BowlFoodOnlyItem(p, ModFoods.DRY_POT_CHICKEN, ModFoods.DRY_POT_CHICKEN_C));
        DRY_POT_SPARE_RIBS = register("dry_pot_spare_ribs", p -> new BowlFoodOnlyItem(p, ModFoods.DRY_POT_SPARE_RIBS, ModFoods.DRY_POT_SPARE_RIBS_C));
        YANGZHOU_FRIED_RICE = register("yangzhou_fried_rice", p -> new BowlFoodOnlyItem(p, ModFoods.YANGZHOU_FRIED_RICE, ModFoods.YANGZHOU_FRIED_RICE_C));
        LAMB_PILAF = register("lamb_pilaf", p -> new BowlFoodOnlyItem(p, ModFoods.LAMB_PILAF, ModFoods.LAMB_PILAF_C));
        STEAMED_RICE_ROLLS = register("steamed_rice_rolls", p -> new BowlFoodOnlyItem(p, ModFoods.STEAMED_RICE_ROLLS, ModFoods.STEAMED_RICE_ROLLS_C));
        SAUERKRAUT_BEEF_NOODLES = register("sauerkraut_beef_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.SAUERKRAUT_BEEF_NOODLES, ModFoods.SAUERKRAUT_BEEF_NOODLES_C));
        SALTED_EGG = register("salted_egg", p -> new Item(p.food(ModFoods.SALTED_EGG, ModFoods.SALTED_EGG_C)));
        CENTURY_EGG = register("century_egg", p -> new Item(p.food(ModFoods.CENTURY_EGG, ModFoods.CENTURY_EGG_C)));
        CHINESE_SAUERKRAUT = register("chinese_sauerkraut", p -> new Item(p.food(ModFoods.CHINESE_SAUERKRAUT, ModFoods.CHINESE_SAUERKRAUT_C)));
        EGGPLANT = register("eggplant", p -> new Item(p.food(ModFoods.EGGPLANT, ModFoods.EGGPLANT_C)));
        EGGPLANT_SEED = register("eggplant_seed", p -> new BlockItem(ModBlocks.EGGPLANT_CROP, p.useItemDescriptionPrefix()));
        YELLOW_CROAKER = register("yellow_croaker", p -> new Item(p.food(ModFoods.YELLOW_CROAKER, ModFoods.YELLOW_CROAKER_C)));
        MOONCAKE = register("mooncake", p -> new MooncakeItem(ModBlocks.MOONCAKE_BLOCK, p.food(ModFoods.MOONCAKE, ModFoods.MOONCAKE_C)));
        CORN_RISTRA = register("corn_ristra", p -> new BlockItem(ModBlocks.CORN_RISTRA, p));
        MOONCAKE_MOLD = register("mooncake_mold", p -> new MooncakeMoldItem(p.stacksTo(1)));
        FIRECRACKER = register("firecracker", p -> new FirecrackerItem(p));

        // plain block items that were auto-registered alongside their blocks on NeoForge
        registerBlockItem("freezer", ModBlocks.FREEZER);
        registerBlockItem("freezer_green", ModBlocks.FREEZER_GREEN);
        registerBlockItem("freezer_orange", ModBlocks.FREEZER_ORANGE);
        registerBlockItem("freezer_light_gray", ModBlocks.FREEZER_LIGHT_GRAY);
        registerBlockItem("freezer_pink", ModBlocks.FREEZER_PINK);
        registerBlockItem("freezer_light_blue", ModBlocks.FREEZER_LIGHT_BLUE);
        registerBlockItem("freezer_yellow", ModBlocks.FREEZER_YELLOW);
        registerBlockItem("pickle_jar", ModBlocks.PICKLE_JAR);
        registerBlockItem("bowl_stack", ModBlocks.BOWL_STACK);
        registerBlockItem("fu_character", ModBlocks.FU_CHARACTER);
        registerBlockItem("couplet_block", ModBlocks.COUPLET_BLOCK);
        registerBlockItem("horizontal_banner", ModBlocks.HORIZONTAL_BANNER);
        registerBlockItem("mooncake_block", ModBlocks.MOONCAKE_BLOCK);
        registerBlockItem("kongming_lantern", ModBlocks.KONGMING_LANTERN);
    }

    private static Item registerBlockItem(String name, Block block) {
        return register(name, p -> new BlockItem(block, p));
    }

    private static Item register(String name, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, KaleidoscopeChineseFood.id(name));
        Item item = factory.apply(new Item.Properties().setId(key));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }
}
