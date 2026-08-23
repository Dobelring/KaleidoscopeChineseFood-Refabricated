package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.item.FirecrackerItem;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeItem;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeMoldItem;
import com.bmt.kaleidoscope_chinesefood.item.TooltipBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodOnlyItem;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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
        RAW_STEAMED_RICE_ROLLS = register("raw_steamed_rice_rolls", Item::new);
        RAW_MOONCAKE = register("raw_mooncake", Item::new);
        SALT_BUCKET = register("salt_bucket", p -> p.stacksTo(1).craftRemainder(Items.BUCKET), Item::new);
        SALT = registerBlockItem("salt", BlockItem::new, ModBlocks.SALT_BLOCK, p -> p);
        SICHUAN_WONTON = register("sichuan_wonton", p -> new BowlFoodOnlyItem(p, ModFoods.SICHUAN_WONTON, ModConsumables.SICHUAN_WONTON));
        WONTON_NOODLES = register("wonton_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.WONTON_NOODLES, ModConsumables.WONTON_NOODLES));
        YANGROU_PAOMO = register("yangrou_paomo", p -> new BowlFoodOnlyItem(p, ModFoods.YANGROU_PAOMO, ModConsumables.YANGROU_PAOMO));
        MAOCAI = register("maocai", p -> new BowlFoodOnlyItem(p, ModFoods.MAOCAI, ModConsumables.MAOCAI));
        SEAWEED_EGG_DROP_SOUP = register("seaweed_egg_drop_soup", p -> new BowlFoodOnlyItem(p, ModFoods.SEAWEED_EGG_DROP_SOUP, ModConsumables.SEAWEED_EGG_DROP_SOUP));
        TOMATO_EGG_DROP_SOUP = register("tomato_egg_drop_soup", p -> new BowlFoodOnlyItem(p, ModFoods.TOMATO_EGG_DROP_SOUP, ModConsumables.TOMATO_EGG_DROP_SOUP));
        DOUZHI = register("douzhi", p -> new BowlFoodOnlyItem(p, ModFoods.DOUZHI, ModConsumables.DOUZHI));
        CENTURY_EGG_CONGEE = register("century_egg_congee", p -> new BowlFoodOnlyItem(p, ModFoods.CENTURY_EGG_CONGEE, ModConsumables.CENTURY_EGG_CONGEE));
        PUMPKIN_PORRIDGE = register("pumpkin_porridge", p -> new BowlFoodOnlyItem(p, ModFoods.PUMPKIN_PORRIDGE, ModConsumables.PUMPKIN_PORRIDGE));
        TWICE_COOKED_PORK = register("twice_cooked_pork", p -> new BowlFoodOnlyItem(p, ModFoods.TWICE_COOKED_PORK, ModConsumables.TWICE_COOKED_PORK));
        TWICE_COOKED_PORK_RICE = register("twice_cooked_pork_rice", p -> new BowlFoodOnlyItem(p, ModFoods.TWICE_COOKED_PORK_RICE, ModConsumables.TWICE_COOKED_PORK_RICE));
        STIR_FRIED_YELLOW_BEEF = register("stir_fried_yellow_beef", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_YELLOW_BEEF, ModConsumables.STIR_FRIED_YELLOW_BEEF));
        STIR_FRIED_YELLOW_BEEF_RICE = register("stir_fried_yellow_beef_rice", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_YELLOW_BEEF_RICE, ModConsumables.STIR_FRIED_YELLOW_BEEF_RICE));
        BEEF_WITH_SCRAMBLED_EGGS = register("beef_with_scrambled_eggs", p -> new BowlFoodOnlyItem(p, ModFoods.BEEF_WITH_SCRAMBLED_EGGS, ModConsumables.BEEF_WITH_SCRAMBLED_EGGS));
        BEEF_WITH_SCRAMBLED_EGGS_RICE = register("beef_with_scrambled_eggs_rice", p -> new BowlFoodOnlyItem(p, ModFoods.BEEF_WITH_SCRAMBLED_EGGS_RICE, ModConsumables.BEEF_WITH_SCRAMBLED_EGGS_RICE));
        STIR_FRIED_THREE_FRESH_VEGETABLES = register("stir_fried_three_fresh_vegetables", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES, ModConsumables.STIR_FRIED_THREE_FRESH_VEGETABLES));
        STIR_FRIED_THREE_FRESH_VEGETABLES_RICE = register("stir_fried_three_fresh_vegetables_rice", p -> new BowlFoodOnlyItem(p, ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE, ModConsumables.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE));
        BIG_PLATE_CHICKEN = register("big_plate_chicken", p -> new BowlFoodOnlyItem(p, ModFoods.BIG_PLATE_CHICKEN, ModConsumables.BIG_PLATE_CHICKEN));
        BIG_PLATE_CHICKEN_NOODLES = register("big_plate_chicken_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.BIG_PLATE_CHICKEN_NOODLES, ModConsumables.BIG_PLATE_CHICKEN_NOODLES));
        TOMATO_EGG_NOODLES = register("tomato_egg_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.TOMATO_EGG_NOODLES, ModConsumables.TOMATO_EGG_NOODLES));
        PORK_CHILI_NOODLES = register("pork_chili_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.PORK_CHILI_NOODLES, ModConsumables.PORK_CHILI_NOODLES));
        FOUR_JOY_MEATBALLS = register("four_joy_meatballs", p -> new BowlFoodOnlyItem(p, ModFoods.FOUR_JOY_MEATBALLS, ModConsumables.FOUR_JOY_MEATBALLS));
        STUFFED_EGGPLANT = register("stuffed_eggplant", p -> new BowlFoodOnlyItem(p, ModFoods.STUFFED_EGGPLANT, ModConsumables.STUFFED_EGGPLANT));
        DRY_POT_POTATOES = register("dry_pot_potatoes", p -> new BowlFoodOnlyItem(p, ModFoods.DRY_POT_POTATOES, ModConsumables.DRY_POT_POTATOES));
        DRY_POT_CHICKEN = register("dry_pot_chicken", p -> new BowlFoodOnlyItem(p, ModFoods.DRY_POT_CHICKEN, ModConsumables.DRY_POT_CHICKEN));
        DRY_POT_SPARE_RIBS = register("dry_pot_spare_ribs", p -> new BowlFoodOnlyItem(p, ModFoods.DRY_POT_SPARE_RIBS, ModConsumables.DRY_POT_SPARE_RIBS));
        YANGZHOU_FRIED_RICE = register("yangzhou_fried_rice", p -> new BowlFoodOnlyItem(p, ModFoods.YANGZHOU_FRIED_RICE, ModConsumables.YANGZHOU_FRIED_RICE));
        LAMB_PILAF = register("lamb_pilaf", p -> new BowlFoodOnlyItem(p, ModFoods.LAMB_PILAF, ModConsumables.LAMB_PILAF));
        STEAMED_RICE_ROLLS = register("steamed_rice_rolls", p -> new BowlFoodOnlyItem(p, ModFoods.STEAMED_RICE_ROLLS, ModConsumables.STEAMED_RICE_ROLLS));
        SAUERKRAUT_BEEF_NOODLES = register("sauerkraut_beef_noodles", p -> new BowlFoodOnlyItem(p, ModFoods.SAUERKRAUT_BEEF_NOODLES, ModConsumables.SAUERKRAUT_BEEF_NOODLES));
        SALTED_EGG = register("salted_egg", p -> p.food(ModFoods.SALTED_EGG), Item::new);
        CENTURY_EGG = register("century_egg", p -> p.food(ModFoods.CENTURY_EGG), Item::new);
        CHINESE_SAUERKRAUT = register("chinese_sauerkraut", p -> p.food(ModFoods.CHINESE_SAUERKRAUT), Item::new);
        EGGPLANT = register("eggplant", p -> p.food(ModFoods.EGGPLANT), Item::new);
        EGGPLANT_SEED = registerBlockItem("eggplant_seed", BlockItem::new, ModBlocks.EGGPLANT_CROP, p -> p.useItemDescriptionPrefix());
        YELLOW_CROAKER = register("yellow_croaker", p -> p.food(ModFoods.YELLOW_CROAKER), Item::new);
        MOONCAKE = register("mooncake", p -> p.food(ModFoods.MOONCAKE), props -> new MooncakeItem(ModBlocks.MOONCAKE_BLOCK, props));
        CORN_RISTRA = registerBlockItem("corn_ristra", ModBlocks.CORN_RISTRA);
        MOONCAKE_MOLD = register("mooncake_mold", p -> p.stacksTo(1), MooncakeMoldItem::new);
        FIRECRACKER = register("firecracker", FirecrackerItem::new);

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
        registerBlockItem("fu_character", ModBlocks.FU_CHARACTER, "block.kaleidoscope_chinesefood.fu_character.desc");
        registerBlockItem("couplet_block", ModBlocks.COUPLET_BLOCK, "block.kaleidoscope_chinesefood.couplet.desc");
        registerBlockItem("horizontal_banner", ModBlocks.HORIZONTAL_BANNER, "block.kaleidoscope_chinesefood.horizontal_banner.desc");
        registerBlockItem("mooncake_block", ModBlocks.MOONCAKE_BLOCK);
        registerBlockItem("kongming_lantern", ModBlocks.KONGMING_LANTERN, "block.kaleidoscope_chinesefood.kongming_lantern.desc");
    }

    private static Item register(String name, Function<Item.Properties, Item> factory) {
        return register(name, p -> p, factory);
    }

    private static Item register(String name, Function<Item.Properties, Item.Properties> propsBuilder, Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = itemKey(name);
        Item.Properties properties = propsBuilder.apply(new Item.Properties().setId(key));
        return bindAndRegister(key, factory.apply(properties));
    }

    private static Item registerBlockItem(String name, Block block) {
        ResourceKey<Item> key = itemKey(name);
        Item.Properties properties = new Item.Properties().setId(key).useBlockDescriptionPrefix();
        return bindAndRegister(key, new BlockItem(block, properties));
    }

    private static Item registerBlockItem(String name, Block block, String tooltipKey) {
        ResourceKey<Item> key = itemKey(name);
        Item.Properties properties = new Item.Properties().setId(key).useBlockDescriptionPrefix();
        return bindAndRegister(key, new TooltipBlockItem(block, properties, tooltipKey));
    }

    private static Item registerBlockItem(
        String name,
        java.util.function.BiFunction<Block, Item.Properties, Item> factory,
        Block block,
        Function<Item.Properties, Item.Properties> propsBuilder
    ) {
        ResourceKey<Item> key = itemKey(name);
        Item.Properties properties = propsBuilder.apply(new Item.Properties().setId(key));
        return bindAndRegister(key, factory.apply(block, properties));
    }

    private static Item bindAndRegister(ResourceKey<Item> key, Item item) {
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(BuiltInRegistries.ITEM.key(), KaleidoscopeChineseFood.id(name));
    }
}
