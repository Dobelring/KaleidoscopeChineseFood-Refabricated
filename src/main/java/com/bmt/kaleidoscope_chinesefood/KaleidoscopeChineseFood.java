package com.bmt.kaleidoscope_chinesefood;

import com.bmt.kaleidoscope_chinesefood.block.KongmingLanternBlock;
import com.bmt.kaleidoscope_chinesefood.compat.carryon.CarryOnCompat;
import com.bmt.kaleidoscope_chinesefood.compat.create.CreateMovementChecks;
import com.bmt.kaleidoscope_chinesefood.compat.kaleidoscope_contraption.KaleidoscopeContraptionCompat;
import com.bmt.kaleidoscope_chinesefood.config.ModConfig;
import com.bmt.kaleidoscope_chinesefood.event.CreativeTabEventHandler;
import com.bmt.kaleidoscope_chinesefood.event.LavaSwimDamageEvents;
import com.bmt.kaleidoscope_chinesefood.event.LootTableEvents;
import com.bmt.kaleidoscope_chinesefood.event.VillagerTradeEvents;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModCompostables;
import com.bmt.kaleidoscope_chinesefood.init.ModCookeryBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModCreativeModeTabs;
import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.init.ModMenuTypes;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipeSerializers;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipeTypes;
import com.bmt.kaleidoscope_chinesefood.init.ModSounds;
import com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight.KTItems;
import com.bmt.kaleidoscope_chinesefood.integration.KaleidoscopeDollIntegration;
import com.bmt.kaleidoscope_chinesefood.network.ModNetwork;
import com.bmt.kaleidoscope_chinesefood.recipe.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.config.ModConfig.Type;

/**
 * 主入口点。
 * <p>
 * 原 Forge 版在构造器里拿 mod 事件总线做全部注册；Fabric 拆成
 * {@link PreLaunchEntrypoint}（必须早于 cookery）+ {@link ModInitializer} 两段。
 */
public class KaleidoscopeChineseFood implements ModInitializer {
    public static final String MODID = "kaleidoscope_chinesefood";

    /** 别名，部分转换后的文件按此名引用。 */
    public static final String MOD_ID = MODID;

    @Override
    public void onInitialize() {
        ForgeConfigRegistry.INSTANCE.register(MODID, Type.COMMON, ModConfig.SPEC);

        ModBuiltInResourcePacks.register();

        // ModItems 的静态字段直接读 ModBlocks 的字段，因此 ModBlocks 必须先行
        ModBlocks.registerBlocks();
        ModItems.registerItems();
        ModCookeryBlocks.registerCookeryBlocks();
        // ModBlockEntities 在类初始化时会读盘装方块，依赖 onPreLaunch 里的 ModPlateRegistry
        ModBlockEntities.registerBlockEntities();
        ModMenuTypes.registerMenuTypes();
        ModCreativeModeTabs.registerCreativeModeTabs();
        ModSounds.registerSounds();
        ModRecipes.registerRecipes();
        ModRecipeTypes.registerRecipeTypes();
        ModRecipeSerializers.registerRecipeSerializers();
        ModEffects.registerEffects();
        ModEntities.registerEntities();
        ModCompostables.init();
        if (!FabricLoader.getInstance().isModLoaded("kaleidoscope_twilight")) {
            KTItems.registerKTItems();
        }

        // 原 mod 总线监听器 KongmingLanternBlock::onCommonSetup（发射器行为）
        KongmingLanternBlock.onCommonSetup();

        ModNetwork.init();
        CreativeTabEventHandler.register();
        LavaSwimDamageEvents.register();
        LootTableEvents.register();
        VillagerTradeEvents.register();
        KaleidoscopeDollIntegration.register();

        SoupBaseManager.registerMobSoupBase(id("yellow_croaker_bucket"), ModItems.YELLOW_CROAKER_BUCKET);

        CarryOnCompat.registerBlacklist();
        if (FabricLoader.getInstance().isModLoaded("create")) {
            CreateMovementChecks.register();
        }
        if (FabricLoader.getInstance().isModLoaded("kaleidoscope_contraption")) {
            KaleidoscopeContraptionCompat.register();
        }
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }

    /**
     * 原 Forge 版的同名便利方法（Forge 47.x 给 ResourceLocation 补了 fromNamespaceAndPath）。
     * 1.20.1 原版没有该方法，这里保留同名包装以兼容既有调用点。
     */
    public static ResourceLocation fromNamespaceAndPath(String namespace, String id) {
        return new ResourceLocation(namespace, id);
    }
}
