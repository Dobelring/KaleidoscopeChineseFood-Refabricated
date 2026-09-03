package com.bmt.kaleidoscope_chinesefood;

import com.bmt.kaleidoscope_chinesefood.block.KongmingLanternBlock;
import com.bmt.kaleidoscope_chinesefood.config.ModConfig;
import com.bmt.kaleidoscope_chinesefood.event.DataMapsEvents;
import com.bmt.kaleidoscope_chinesefood.event.FoodEventHandler;
import com.bmt.kaleidoscope_chinesefood.event.LavaSwimDamageEvents;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModCreativeModeTabs;
import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModFoodBiteRegistry;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.init.ModMenuTypes;
import com.bmt.kaleidoscope_chinesefood.init.ModPlateRegistry;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.bmt.kaleidoscope_chinesefood.init.ModSounds;
import com.bmt.kaleidoscope_chinesefood.init.ModTea;
import com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight.KTItems;
import com.bmt.kaleidoscope_chinesefood.integration.KaleidoscopeDollIntegration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class KaleidoscopeChineseFood implements ModInitializer {
    public static final String MODID = "kaleidoscope_chinesefood";

    @Override
    public void onInitialize() {
        ModConfig.init();
        ModEffects.register();
        ModTea.init();
        ModFoodBiteRegistry.registerFoodBiteBlocks();
        ModPlateRegistry.init();
        if (!FabricLoader.getInstance().isModLoaded("kaleidoscope_twilight")) {
            KTItems.register();
        }

        ModBlocks.register();
        ModItems.register();
        ModBlockEntities.register();
        ModEntities.register();
        ModMenuTypes.register();
        ModSounds.register();
        ModRecipes.register();
        ModCreativeModeTabs.register();

        // JEI 客户端查看自定义配方需要完整配方对象：通过 Fabric 配方同步 API 按序列化器显式注册，客户端自动下发。
        if (FabricLoader.getInstance().isModLoaded("jei")) {
            RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.PICKLE_JAR_SERIALIZER);
            RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.FREEZING_SERIALIZER);
            RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.REFRIGERATING_SERIALIZER);
        }

        KaleidoscopeDollIntegration.register();
        ModBuiltInResourcePacks.register();
        FoodEventHandler.register();
        LavaSwimDamageEvents.register();
        DataMapsEvents.register();

        KongmingLanternBlock.registerDispenserBehavior(ModBlocks.KONGMING_LANTERN.asItem());
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MODID, name);
    }
}
