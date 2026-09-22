package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight.KTItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import java.util.Objects;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTabs {
    private static final ResourceKey<CreativeModeTab> KALEIDOSCOPE_SICHUAN_CUISINE_TAB_KEY = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB, new ResourceLocation("kaleidoscope_chinesefood", "kaleidoscope_chinesefood_tab")
    );
    public static final CreativeModeTab KALEIDOSCOPE_SICHUAN_CUISINE_TAB = FabricItemGroup.builder()
        .icon(() -> new ItemStack(ModItems.SICHUAN_WONTON))
        .title(Component.translatable("itemGroup.kaleidoscope_chinesefood_tab"))
        .displayItems((pParameters, pOutput) -> {
            pOutput.accept(ModItems.RAW_STEAMED_RICE_ROLLS);
            pOutput.accept(ModItems.RAW_BAMBOO_STEAMED_EGG);
            pOutput.accept(ModItems.RAW_MOONCAKE);
            pOutput.accept(ModItems.DIANHONG_TEA_BAG);
            pOutput.accept(ModItems.YELLOW_CROAKER_BUCKET);
            pOutput.accept(ModItems.SALT_BUCKET);
            pOutput.accept(ModBlocks.SALT_BLOCK);
            pOutput.accept(ModItems.SALTED_EGG);
            pOutput.accept(ModItems.CENTURY_EGG);
            pOutput.accept(ModItems.CHINESE_SAUERKRAUT);
            pOutput.accept(ModItems.EGGPLANT);
            pOutput.accept(ModItems.YELLOW_CROAKER);
            pOutput.accept(ModItems.MOONCAKE);
            pOutput.accept(ModItems.BAMBOO_STEAMED_EGG);
            pOutput.accept(ModItems.SICHUAN_WONTON);
            pOutput.accept(ModItems.WONTON_NOODLES);
            pOutput.accept(ModItems.YANGROU_PAOMO);
            pOutput.accept(ModItems.SAUERKRAUT_BEEF_NOODLES);
            pOutput.accept(ModItems.MAOCAI);
            pOutput.accept(ModItems.SEAWEED_EGG_DROP_SOUP);
            pOutput.accept(ModItems.TOMATO_EGG_DROP_SOUP);
            pOutput.accept(ModItems.CENTURY_EGG_CONGEE);
            pOutput.accept(ModItems.PUMPKIN_PORRIDGE);
            pOutput.accept(ModItems.DOUZHI);
            pOutput.accept(ModItems.TWICE_COOKED_PORK);
            pOutput.accept(ModItems.TWICE_COOKED_PORK_RICE);
            pOutput.accept(ModItems.STIR_FRIED_YELLOW_BEEF);
            pOutput.accept(ModItems.STIR_FRIED_YELLOW_BEEF_RICE);
            pOutput.accept(ModItems.BEEF_WITH_SCRAMBLED_EGGS);
            pOutput.accept(ModItems.BEEF_WITH_SCRAMBLED_EGGS_RICE);
            pOutput.accept(ModItems.STIR_FRIED_THREE_FRESH_VEGETABLES);
            pOutput.accept(ModItems.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE);
            pOutput.accept(ModItems.BIG_PLATE_CHICKEN);
            pOutput.accept(ModItems.BIG_PLATE_CHICKEN_NOODLES);
            pOutput.accept(ModItems.TOMATO_EGG_NOODLES);
            pOutput.accept(ModItems.PORK_CHILI_NOODLES);
            pOutput.accept(ModItems.STUFFED_EGGPLANT);
            pOutput.accept(ModItems.DRY_POT_POTATOES);
            pOutput.accept(ModItems.DRY_POT_CHICKEN);
            pOutput.accept(ModItems.DRY_POT_SPARE_RIBS);
            pOutput.accept(ModItems.YANGZHOU_FRIED_RICE);
            pOutput.accept(ModItems.LAMB_PILAF);
            FoodBiteRegistry.FOOD_DATA_MAP.forEach((resourceLocation, foodData) -> {
                if (resourceLocation.getNamespace().equals("kaleidoscope_chinesefood")) {
                    Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                    pOutput.accept(Objects.requireNonNull(item));
                }
            });
            pOutput.accept(TeacupRegistry.getItem(ModTea.DIANHONG_TEA));
            pOutput.accept(TeacupRegistry.getItem(ModTea.HK_MILK_TEA));
            PlateRegistry.PLATE_DATA_MAP.forEach((resourceLocation, plateData) -> {
                if (resourceLocation.getNamespace().equals("kaleidoscope_chinesefood")) {
                    Item item = BuiltInRegistries.ITEM.get(resourceLocation);
                    pOutput.accept(Objects.requireNonNull(item));
                }
            });
            pOutput.accept(ModBlocks.BOWL_STACK);
            pOutput.accept(ModItems.MOONCAKE_MOLD);
            pOutput.accept(ModBlocks.CORN_RISTRA);
            pOutput.accept(ModBlocks.FREEZER);
            pOutput.accept(ModBlocks.FREEZER_GREEN);
            pOutput.accept(ModBlocks.FREEZER_ORANGE);
            pOutput.accept(ModBlocks.FREEZER_PINK);
            pOutput.accept(ModBlocks.FREEZER_LIGHT_BLUE);
            pOutput.accept(ModBlocks.FREEZER_YELLOW);
            pOutput.accept(ModBlocks.PICKLE_JAR);
            pOutput.accept(ModItems.FIRECRACKER);
            pOutput.accept(ModBlocks.FU_CHARACTER);
            pOutput.accept(ModBlocks.COUPLET);
            pOutput.accept(ModBlocks.HORIZONTAL_BANNER);
            pOutput.accept(ModBlocks.KONGMING_LANTERN);
            pOutput.accept(ModItems.EGGPLANT_SEED);
            pOutput.accept(ModCookeryBlocks.STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE);
            pOutput.accept(ModCookeryBlocks.STRIPPED_BAMBOO_BENCH);
            if (!FabricLoader.getInstance().isModLoaded("kaleidoscope_twilight")) {
                pOutput.accept(KTItems.FROZEN_BUN);
            }
        })
        .build();

    public ModCreativeModeTabs() {
    }

    public static void registerCreativeModeTabs() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, KALEIDOSCOPE_SICHUAN_CUISINE_TAB_KEY, KALEIDOSCOPE_SICHUAN_CUISINE_TAB);
    }
}
