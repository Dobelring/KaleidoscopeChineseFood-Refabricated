package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.item.FirecrackerItem;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeItem;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeMoldItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodOnlyItem;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
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
        RAW_STEAMED_RICE_ROLLS = register("raw_steamed_rice_rolls", () -> new Item(new Item.Properties()));
        RAW_MOONCAKE = register("raw_mooncake", () -> new Item(new Item.Properties()));
        SALT_BUCKET = register("salt_bucket", () -> new Item(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));
        SALT = register("salt", () -> new BlockItem(ModBlocks.SALT_BLOCK, new Item.Properties()));
        SICHUAN_WONTON = register("sichuan_wonton", () -> new BowlFoodOnlyItem(ModFoods.SICHUAN_WONTON));
        WONTON_NOODLES = register("wonton_noodles", () -> new BowlFoodOnlyItem(ModFoods.WONTON_NOODLES));
        YANGROU_PAOMO = register("yangrou_paomo", () -> new BowlFoodOnlyItem(ModFoods.YANGROU_PAOMO));
        MAOCAI = register("maocai", () -> new BowlFoodOnlyItem(ModFoods.MAOCAI));
        SEAWEED_EGG_DROP_SOUP = register("seaweed_egg_drop_soup", () -> new BowlFoodOnlyItem(ModFoods.SEAWEED_EGG_DROP_SOUP));
        TOMATO_EGG_DROP_SOUP = register("tomato_egg_drop_soup", () -> new BowlFoodOnlyItem(ModFoods.TOMATO_EGG_DROP_SOUP));
        DOUZHI = register("douzhi", () -> new BowlFoodOnlyItem(ModFoods.DOUZHI));
        CENTURY_EGG_CONGEE = register("century_egg_congee", () -> new BowlFoodOnlyItem(ModFoods.CENTURY_EGG_CONGEE));
        PUMPKIN_PORRIDGE = register("pumpkin_porridge", () -> new BowlFoodOnlyItem(ModFoods.PUMPKIN_PORRIDGE));
        TWICE_COOKED_PORK = register("twice_cooked_pork", () -> new BowlFoodOnlyItem(ModFoods.TWICE_COOKED_PORK));
        TWICE_COOKED_PORK_RICE = register("twice_cooked_pork_rice", () -> new BowlFoodOnlyItem(ModFoods.TWICE_COOKED_PORK_RICE));
        STIR_FRIED_YELLOW_BEEF = register("stir_fried_yellow_beef", () -> new BowlFoodOnlyItem(ModFoods.STIR_FRIED_YELLOW_BEEF));
        STIR_FRIED_YELLOW_BEEF_RICE = register("stir_fried_yellow_beef_rice", () -> new BowlFoodOnlyItem(ModFoods.STIR_FRIED_YELLOW_BEEF_RICE));
        BEEF_WITH_SCRAMBLED_EGGS = register("beef_with_scrambled_eggs", () -> new BowlFoodOnlyItem(ModFoods.BEEF_WITH_SCRAMBLED_EGGS));
        BEEF_WITH_SCRAMBLED_EGGS_RICE = register("beef_with_scrambled_eggs_rice", () -> new BowlFoodOnlyItem(ModFoods.BEEF_WITH_SCRAMBLED_EGGS_RICE));
        STIR_FRIED_THREE_FRESH_VEGETABLES = register("stir_fried_three_fresh_vegetables", () -> new BowlFoodOnlyItem(ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES));
        STIR_FRIED_THREE_FRESH_VEGETABLES_RICE = register("stir_fried_three_fresh_vegetables_rice", () -> new BowlFoodOnlyItem(ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE));
        BIG_PLATE_CHICKEN = register("big_plate_chicken", () -> new BowlFoodOnlyItem(ModFoods.BIG_PLATE_CHICKEN));
        BIG_PLATE_CHICKEN_NOODLES = register("big_plate_chicken_noodles", () -> new BowlFoodOnlyItem(ModFoods.BIG_PLATE_CHICKEN_NOODLES));
        TOMATO_EGG_NOODLES = register("tomato_egg_noodles", () -> new BowlFoodOnlyItem(ModFoods.TOMATO_EGG_NOODLES));
        PORK_CHILI_NOODLES = register("pork_chili_noodles", () -> new BowlFoodOnlyItem(ModFoods.PORK_CHILI_NOODLES));
        FOUR_JOY_MEATBALLS = register("four_joy_meatballs", () -> new BowlFoodOnlyItem(ModFoods.FOUR_JOY_MEATBALLS));
        STUFFED_EGGPLANT = register("stuffed_eggplant", () -> new BowlFoodOnlyItem(ModFoods.STUFFED_EGGPLANT));
        DRY_POT_POTATOES = register("dry_pot_potatoes", () -> new BowlFoodOnlyItem(ModFoods.DRY_POT_POTATOES));
        DRY_POT_CHICKEN = register("dry_pot_chicken", () -> new BowlFoodOnlyItem(ModFoods.DRY_POT_CHICKEN));
        DRY_POT_SPARE_RIBS = register("dry_pot_spare_ribs", () -> new BowlFoodOnlyItem(ModFoods.DRY_POT_SPARE_RIBS));
        YANGZHOU_FRIED_RICE = register("yangzhou_fried_rice", () -> new BowlFoodOnlyItem(ModFoods.YANGZHOU_FRIED_RICE));
        LAMB_PILAF = register("lamb_pilaf", () -> new BowlFoodOnlyItem(ModFoods.LAMB_PILAF));
        STEAMED_RICE_ROLLS = register("steamed_rice_rolls", () -> new BowlFoodOnlyItem(ModFoods.STEAMED_RICE_ROLLS));
        SAUERKRAUT_BEEF_NOODLES = register("sauerkraut_beef_noodles", () -> new BowlFoodOnlyItem(ModFoods.SAUERKRAUT_BEEF_NOODLES));
        SALTED_EGG = register("salted_egg", () -> new Item(new Item.Properties().food(ModFoods.SALTED_EGG)));
        CENTURY_EGG = register("century_egg", () -> new Item(new Item.Properties().food(ModFoods.CENTURY_EGG)));
        CHINESE_SAUERKRAUT = register("chinese_sauerkraut", () -> new Item(new Item.Properties().food(ModFoods.CHINESE_SAUERKRAUT)));
        EGGPLANT = register("eggplant", () -> new Item(new Item.Properties().food(ModFoods.EGGPLANT)));
        EGGPLANT_SEED = register("eggplant_seed", () -> new ItemNameBlockItem(ModBlocks.EGGPLANT_CROP, new Item.Properties()));
        YELLOW_CROAKER = register("yellow_croaker", () -> new Item(new Item.Properties().food(ModFoods.YELLOW_CROAKER)));
        MOONCAKE = register("mooncake", () -> new MooncakeItem(ModBlocks.MOONCAKE_BLOCK, new Item.Properties().food(ModFoods.MOONCAKE)));
        CORN_RISTRA = register("corn_ristra", () -> new BlockItem(ModBlocks.CORN_RISTRA, new Item.Properties()));
        MOONCAKE_MOLD = register("mooncake_mold", () -> new MooncakeMoldItem(new Item.Properties().stacksTo(1)));
        FIRECRACKER = register("firecracker", () -> new FirecrackerItem(new Item.Properties()));

        // plain block items that were auto-registered alongside their blocks on NeoForge
        registerBlockItem("freezer", ModBlocks.FREEZER);
        registerBlockItem("freezer_green", ModBlocks.FREEZER_GREEN);
        registerBlockItem("freezer_orange", ModBlocks.FREEZER_ORANGE);
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
        return register(name, () -> new BlockItem(block, new Item.Properties()));
    }

    private static Item register(String name, Supplier<Item> supplier) {
        return Registry.register(BuiltInRegistries.ITEM, KaleidoscopeChineseFood.id(name), supplier.get());
    }
}
