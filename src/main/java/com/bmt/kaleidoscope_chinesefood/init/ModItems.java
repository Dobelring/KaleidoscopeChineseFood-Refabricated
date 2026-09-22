package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.item.BambooSteamedEggBlockItem;
import com.bmt.kaleidoscope_chinesefood.item.FirecrackerItem;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeItem;
import com.bmt.kaleidoscope_chinesefood.item.MooncakeMoldItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodOnlyItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FoodWithEffectsItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.material.Fluids;

public class ModItems {
    public static final Item RAW_STEAMED_RICE_ROLLS = new Item(new Properties());
    public static final Item RAW_MOONCAKE = new Item(new Properties());
    public static final Item RAW_BAMBOO_STEAMED_EGG = new Item(new Properties());
    public static final Item DIANHONG_TEA_BAG = new Item(new Properties());
    public static final Item SALT_BUCKET = new Item(new Properties().stacksTo(1).craftRemainder(Items.BUCKET));
    public static final Item SICHUAN_WONTON = new BowlFoodOnlyItem(ModFoods.SICHUAN_WONTON);
    public static final Item WONTON_NOODLES = new BowlFoodOnlyItem(ModFoods.WONTON_NOODLES);
    public static final Item YANGROU_PAOMO = new BowlFoodOnlyItem(ModFoods.YANGROU_PAOMO);
    public static final Item MAOCAI = new BowlFoodOnlyItem(ModFoods.MAOCAI);
    public static final Item SEAWEED_EGG_DROP_SOUP = new BowlFoodOnlyItem(ModFoods.SEAWEED_EGG_DROP_SOUP);
    public static final Item TOMATO_EGG_DROP_SOUP = new BowlFoodOnlyItem(ModFoods.TOMATO_EGG_DROP_SOUP);
    public static final Item DOUZHI = new BowlFoodOnlyItem(ModFoods.DOUZHI);
    public static final Item CENTURY_EGG_CONGEE = new BowlFoodOnlyItem(ModFoods.CENTURY_EGG_CONGEE);
    public static final Item PUMPKIN_PORRIDGE = new BowlFoodOnlyItem(ModFoods.PUMPKIN_PORRIDGE);
    public static final Item TWICE_COOKED_PORK = new BowlFoodOnlyItem(ModFoods.TWICE_COOKED_PORK);
    public static final Item TWICE_COOKED_PORK_RICE = new BowlFoodOnlyItem(ModFoods.TWICE_COOKED_PORK_RICE);
    public static final Item STIR_FRIED_YELLOW_BEEF = new BowlFoodOnlyItem(ModFoods.STIR_FRIED_YELLOW_BEEF);
    public static final Item STIR_FRIED_YELLOW_BEEF_RICE = new BowlFoodOnlyItem(ModFoods.STIR_FRIED_YELLOW_BEEF_RICE);
    public static final Item BEEF_WITH_SCRAMBLED_EGGS = new BowlFoodOnlyItem(ModFoods.BEEF_WITH_SCRAMBLED_EGGS);
    public static final Item BEEF_WITH_SCRAMBLED_EGGS_RICE = new BowlFoodOnlyItem(ModFoods.BEEF_WITH_SCRAMBLED_EGGS_RICE);
    public static final Item STIR_FRIED_THREE_FRESH_VEGETABLES = new BowlFoodOnlyItem(ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES);
    public static final Item STIR_FRIED_THREE_FRESH_VEGETABLES_RICE = new BowlFoodOnlyItem(ModFoods.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE);
    public static final Item BIG_PLATE_CHICKEN = new BowlFoodOnlyItem(ModFoods.BIG_PLATE_CHICKEN);
    public static final Item BIG_PLATE_CHICKEN_NOODLES = new BowlFoodOnlyItem(ModFoods.BIG_PLATE_CHICKEN_NOODLES);
    public static final Item TOMATO_EGG_NOODLES = new BowlFoodOnlyItem(ModFoods.TOMATO_EGG_NOODLES);
    public static final Item PORK_CHILI_NOODLES = new BowlFoodOnlyItem(ModFoods.PORK_CHILI_NOODLES);
    public static final Item STUFFED_EGGPLANT = new BowlFoodOnlyItem(ModFoods.STUFFED_EGGPLANT);
    public static final Item YANGZHOU_FRIED_RICE = new BowlFoodOnlyItem(ModFoods.YANGZHOU_FRIED_RICE);
    public static final Item LAMB_PILAF = new BowlFoodOnlyItem(ModFoods.LAMB_PILAF);
    public static final Item DRY_POT_POTATOES = new BowlFoodOnlyItem(ModFoods.DRY_POT_POTATOES);
    public static final Item DRY_POT_CHICKEN = new BowlFoodOnlyItem(ModFoods.DRY_POT_CHICKEN);
    public static final Item DRY_POT_SPARE_RIBS = new BowlFoodOnlyItem(ModFoods.DRY_POT_SPARE_RIBS);
    public static final Item SAUERKRAUT_BEEF_NOODLES = new BowlFoodOnlyItem(ModFoods.SAUERKRAUT_BEEF_NOODLES);
    public static final Item SALTED_EGG = new FoodWithEffectsItem(ModFoods.SALTED_EGG);
    public static final Item CENTURY_EGG = new FoodWithEffectsItem(ModFoods.CENTURY_EGG);
    public static final Item CHINESE_SAUERKRAUT = new FoodWithEffectsItem(ModFoods.CHINESE_SAUERKRAUT);
    public static final Item EGGPLANT = new Item(new Properties().food(ModFoods.EGGPLANT));
    public static final Item EGGPLANT_SEED = new ItemNameBlockItem(ModBlocks.EGGPLANT_CROP, new Properties());
    public static final Item YELLOW_CROAKER = new Item(new Properties().food(ModFoods.YELLOW_CROAKER));
    // 原 Forge 代码用的是 Forge 补丁新增的 Supplier 版构造器，原版（Fabric）1.20.1 只有
    // (EntityType, Fluid, SoundEvent, Properties) 这一版，语义等价（Forge 那版也只是在构造时立即 get()）
    public static final Item YELLOW_CROAKER_BUCKET = new MobBucketItem(
        ModEntities.YELLOW_CROAKER, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, new Properties().stacksTo(1)
    );
    public static final SpawnEggItem YELLOW_CROAKER_SPAWN_EGG = new SpawnEggItem(
        ModEntities.YELLOW_CROAKER, 15181583, 9274720, new Properties()
    );
    public static final Item MOONCAKE = new MooncakeItem(ModBlocks.MOONCAKE_BLOCK, new Properties().food(ModFoods.MOONCAKE).stacksTo(64));
    public static final Item FIRECRACKER = new FirecrackerItem(ModBlocks.FIRECRACKER, new Properties());
    public static final Item MOONCAKE_MOLD = new MooncakeMoldItem(new Properties().stacksTo(1));
    public static final Item BAMBOO_STEAMED_EGG = new BambooSteamedEggBlockItem(ModBlocks.BAMBOO_STEAMED_EGG, ModFoods.BAMBOO_STEAMED_EGG);

    public ModItems() {
    }

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "raw_steamed_rice_rolls"), RAW_STEAMED_RICE_ROLLS);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "raw_mooncake"), RAW_MOONCAKE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "raw_bamboo_steamed_egg"), RAW_BAMBOO_STEAMED_EGG);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "dianhong_tea_bag"), DIANHONG_TEA_BAG);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "salt_bucket"), SALT_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "sichuan_wonton"), SICHUAN_WONTON);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "wonton_noodles"), WONTON_NOODLES);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "yangrou_paomo"), YANGROU_PAOMO);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "maocai"), MAOCAI);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "seaweed_egg_drop_soup"), SEAWEED_EGG_DROP_SOUP);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "tomato_egg_drop_soup"), TOMATO_EGG_DROP_SOUP);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "douzhi"), DOUZHI);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "century_egg_congee"), CENTURY_EGG_CONGEE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "pumpkin_porridge"), PUMPKIN_PORRIDGE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "twice_cooked_pork"), TWICE_COOKED_PORK);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "twice_cooked_pork_rice"), TWICE_COOKED_PORK_RICE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "stir_fried_yellow_beef"), STIR_FRIED_YELLOW_BEEF);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "stir_fried_yellow_beef_rice"), STIR_FRIED_YELLOW_BEEF_RICE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "beef_with_scrambled_eggs"), BEEF_WITH_SCRAMBLED_EGGS);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "beef_with_scrambled_eggs_rice"), BEEF_WITH_SCRAMBLED_EGGS_RICE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "stir_fried_three_fresh_vegetables"), STIR_FRIED_THREE_FRESH_VEGETABLES);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "stir_fried_three_fresh_vegetables_rice"), STIR_FRIED_THREE_FRESH_VEGETABLES_RICE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "big_plate_chicken"), BIG_PLATE_CHICKEN);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "big_plate_chicken_noodles"), BIG_PLATE_CHICKEN_NOODLES);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "tomato_egg_noodles"), TOMATO_EGG_NOODLES);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "pork_chili_noodles"), PORK_CHILI_NOODLES);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "stuffed_eggplant"), STUFFED_EGGPLANT);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "yangzhou_fried_rice"), YANGZHOU_FRIED_RICE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "lamb_pilaf"), LAMB_PILAF);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "dry_pot_potatoes"), DRY_POT_POTATOES);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "dry_pot_chicken"), DRY_POT_CHICKEN);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "dry_pot_spare_ribs"), DRY_POT_SPARE_RIBS);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "sauerkraut_beef_noodles"), SAUERKRAUT_BEEF_NOODLES);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "salted_egg"), SALTED_EGG);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "century_egg"), CENTURY_EGG);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "chinese_sauerkraut"), CHINESE_SAUERKRAUT);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "eggplant"), EGGPLANT);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "eggplant_seed"), EGGPLANT_SEED);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "yellow_croaker"), YELLOW_CROAKER);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "yellow_croaker_bucket"), YELLOW_CROAKER_BUCKET);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "yellow_croaker_spawn_egg"), YELLOW_CROAKER_SPAWN_EGG);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "mooncake"), MOONCAKE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "firecracker"), FIRECRACKER);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "mooncake_mold"), MOONCAKE_MOLD);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "bamboo_steamed_egg"), BAMBOO_STEAMED_EGG);
    }
}
