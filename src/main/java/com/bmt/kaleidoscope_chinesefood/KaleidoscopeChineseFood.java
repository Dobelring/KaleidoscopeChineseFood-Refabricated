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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KaleidoscopeChineseFood implements ModInitializer {
    public static final String MODID = "kaleidoscope_chinesefood";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        ModConfig.init();
        ModEffects.register();
        ModTea.init();
        ModFoodBiteRegistry.init();
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

        KaleidoscopeDollIntegration.register();
        ModBuiltInResourcePacks.register();
        FoodEventHandler.register();
        LavaSwimDamageEvents.register();
        DataMapsEvents.register();

        // 1.1.10 新增：放置菜品与 Kaleidoscope Contraption 兼容（反射软依赖，
        // create 与 kaleidoscope_contraption 均加载时才注册食物位交互行为）
        if (FabricLoader.getInstance().isModLoaded("create")
                && FabricLoader.getInstance().isModLoaded("kaleidoscope_contraption")) {
            com.bmt.kaleidoscope_chinesefood.compat.kaleidoscope_contraption.KaleidoscopeContraptionCompat.register();
        }

        KongmingLanternBlock.registerDispenserBehavior(ModBlocks.KONGMING_LANTERN.asItem());
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    public static ResourceLocation fromNamespaceAndPath(String namespace, String id) {
        return ResourceLocation.fromNamespaceAndPath(namespace, id);
    }
}
