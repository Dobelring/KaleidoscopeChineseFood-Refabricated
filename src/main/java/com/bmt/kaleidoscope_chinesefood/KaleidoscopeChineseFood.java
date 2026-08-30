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
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.bmt.kaleidoscope_chinesefood.init.ModSounds;
import com.bmt.kaleidoscope_chinesefood.init.ModTea;
import com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight.KTItems;
import com.bmt.kaleidoscope_chinesefood.integration.KaleidoscopeDollIntegration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KaleidoscopeChineseFood implements ModInitializer {
    public static final String MODID = "kaleidoscope_chinesefood";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private static volatile boolean foodPhaseDone = false;

    @Override
    public void onInitialize() {
        ModConfig.init();
        ModEffects.register();
        ModTea.init(); // 茶杯效果是懒加载 supplier，注册期不读取 cookery 的 Holder，可留在 main 阶段

        // 注意：不要在 main 阶段触发 ModFoods 的类初始化（会过早读取 cookery 的效果 Holder），
        // ModItems / ModFoodBiteRegistry / KTItems / DataMapsEvents 统一推迟到 foodPhase

        ModBlocks.register();
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

        KongmingLanternBlock.registerDispenserBehavior(ModBlocks.KONGMING_LANTERN.asItem());

        // 1.21.2+ 原版不再向客户端同步完整配方：JEI 存在时声明需要同步这些配方序列化器，
        // 客户端 JEI 插件通过 RecipeSynchronization 读取完整配方（与厨房乐事做法一致）
        if (FabricLoader.getInstance().isModLoaded("jei")) {
            RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.PICKLE_JAR_SERIALIZER);
            RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.FREEZING_SERIALIZER);
            RecipeSynchronization.synchronizeRecipeSerializer(ModRecipes.REFRIGERATING_SERIALIZER);
        }
    }

    /**
     * 依赖 cookery 的注册阶段（食物消耗品、碗装食物方块/物品、数据映射）。
     * <p>
     * Fabric Loader 按字母序调用入口点（kaleidoscope_chinesefood 排在 kaleidoscope_cookery
     * 之前），depends 只校验存在性、不保证初始化顺序；若在 main 阶段构建 ModFoods 的消耗品，
     * cookery 的效果 Holder（WARMTH/VIGOR/SATIATED_SHIELD）还是 null，会产出损坏的
     * MobEffectInstance（打开创造物品栏哈希时 NPE），且我们的 FoodData 进入 cookery 的
     * FOOD_DATA_MAP 后还会被 cookery 的 CommonRegistry 重复注册导致崩溃。
     * <p>
     * client / server 入口点阶段在所有 main 入口点之后运行，此时 cookery 必然初始化完毕，
     * 其 CommonRegistry 对 FOOD_DATA_MAP 的迭代也已结束，两个问题一并消除。
     * 由 client 与 server 入口点调用，一次性守护确保只执行一遍。
     */
    public static void runFoodPhase() {
        if (foodPhaseDone) {
            return;
        }
        synchronized (KaleidoscopeChineseFood.class) {
            if (foodPhaseDone) {
                return;
            }
            ModFoodBiteRegistry.init();
            ModItems.register();
            if (!FabricLoader.getInstance().isModLoaded("kaleidoscope_twilight")) {
                KTItems.register();
            }
            DataMapsEvents.register();
            foodPhaseDone = true;
            LOGGER.info("Food phase initialized (cookery-dependent registrations complete)");
        }
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MODID, name);
    }

    public static Identifier fromNamespaceAndPath(String namespace, String id) {
        return Identifier.fromNamespaceAndPath(namespace, id);
    }
}
