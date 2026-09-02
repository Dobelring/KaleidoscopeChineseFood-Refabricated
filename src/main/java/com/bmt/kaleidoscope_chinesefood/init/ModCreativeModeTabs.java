package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight.KTItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModCreativeModeTabs {
    public static CreativeModeTab KALEIDOSCOPE_SICHUAN_CUISINE_TAB;

    public static void register() {
        KALEIDOSCOPE_SICHUAN_CUISINE_TAB = Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                KaleidoscopeChineseFood.id("kaleidoscope_chinesefood_tab"),
                CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                        .icon(() -> new ItemStack(ModItems.SICHUAN_WONTON))
                        .title(Component.translatable("itemGroup.kaleidoscope_chinesefood_tab"))
                        .displayItems((parameters, output) -> {
                            output.accept(ModItems.RAW_STEAMED_RICE_ROLLS);
                            output.accept(ModItems.RAW_MOONCAKE);
                            output.accept(ModItems.SALT_BUCKET);
                            output.accept(ModItems.SALT);
                            output.accept(ModItems.SALTED_EGG);
                            output.accept(ModItems.CENTURY_EGG);
                            output.accept(ModItems.CHINESE_SAUERKRAUT);
                            output.accept(ModItems.EGGPLANT);
                            output.accept(ModItems.YELLOW_CROAKER);
                            output.accept(ModItems.MOONCAKE);
                            output.accept(ModItems.SICHUAN_WONTON);
                            output.accept(ModItems.WONTON_NOODLES);
                            output.accept(ModItems.YANGROU_PAOMO);
                            output.accept(ModItems.SAUERKRAUT_BEEF_NOODLES);
                            output.accept(ModItems.MAOCAI);
                            output.accept(ModItems.SEAWEED_EGG_DROP_SOUP);
                            output.accept(ModItems.TOMATO_EGG_DROP_SOUP);
                            output.accept(ModItems.CENTURY_EGG_CONGEE);
                            output.accept(ModItems.PUMPKIN_PORRIDGE);
                            output.accept(ModItems.DOUZHI);
                            output.accept(ModItems.TWICE_COOKED_PORK);
                            output.accept(ModItems.TWICE_COOKED_PORK_RICE);
                            output.accept(ModItems.STIR_FRIED_YELLOW_BEEF);
                            output.accept(ModItems.STIR_FRIED_YELLOW_BEEF_RICE);
                            output.accept(ModItems.BEEF_WITH_SCRAMBLED_EGGS);
                            output.accept(ModItems.BEEF_WITH_SCRAMBLED_EGGS_RICE);
                            output.accept(ModItems.STIR_FRIED_THREE_FRESH_VEGETABLES);
                            output.accept(ModItems.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE);
                            output.accept(ModItems.BIG_PLATE_CHICKEN);
                            output.accept(ModItems.BIG_PLATE_CHICKEN_NOODLES);
                            output.accept(ModItems.TOMATO_EGG_NOODLES);
                            output.accept(ModItems.PORK_CHILI_NOODLES);
                            output.accept(ModItems.FOUR_JOY_MEATBALLS);
                            output.accept(ModItems.STUFFED_EGGPLANT);
                            output.accept(ModItems.DRY_POT_POTATOES);
                            output.accept(ModItems.DRY_POT_CHICKEN);
                            output.accept(ModItems.DRY_POT_SPARE_RIBS);
                            output.accept(ModItems.YANGZHOU_FRIED_RICE);
                            output.accept(ModItems.LAMB_PILAF);
                            output.accept(ModItems.STEAMED_RICE_ROLLS);
                            FoodBiteRegistry.FOOD_DATA_MAP.forEach((resourceLocation, foodData) -> {
                                if (resourceLocation.getNamespace().equals("kaleidoscope_chinesefood")) {
                                    Item item = BuiltInRegistries.ITEM.getValue(resourceLocation);
                                    if (item == null || item == Items.AIR) {
                                        return;
                                    }
                                    output.accept(item);
                                }
                            });
                            acceptTeaIfRegistered(output, ModTea.LAPSANG);
                            acceptTeaIfRegistered(output, ModTea.HK_MILK_TEA);
                            output.accept(ModBlocks.BOWL_STACK);
                            output.accept(ModItems.MOONCAKE_MOLD);
                            output.accept(ModItems.CORN_RISTRA);
                            output.accept(ModBlocks.FREEZER);
                            output.accept(ModBlocks.FREEZER_GREEN);
                            output.accept(ModBlocks.FREEZER_ORANGE);
                            output.accept(ModBlocks.FREEZER_PINK);
                            output.accept(ModBlocks.FREEZER_LIGHT_BLUE);
                            output.accept(ModBlocks.FREEZER_YELLOW);
                            output.accept(ModBlocks.PICKLE_JAR);
                            output.accept(ModItems.FIRECRACKER);
                            output.accept(ModBlocks.FU_CHARACTER);
                            output.accept(ModBlocks.COUPLET_BLOCK);
                            output.accept(ModBlocks.HORIZONTAL_BANNER);
                            output.accept(ModBlocks.KONGMING_LANTERN);
                            output.accept(ModItems.EGGPLANT_SEED);
                            if (!FabricLoader.getInstance().isModLoaded("kaleidoscope_twilight")) {
                                output.accept(KTItems.FROZEN_BUN);
                            }
                        })
                        .build()
        );
    }

    /** 未注册的茶杯（getItem 返回 AIR）跳过，避免 accept 非法堆栈 */
    private static void acceptTeaIfRegistered(CreativeModeTab.Output output, Identifier teaId) {
        Item item = TeacupRegistry.getItem(teaId);
        if (item != null && item != Items.AIR) {
            output.accept(item);
        }
    }
}
