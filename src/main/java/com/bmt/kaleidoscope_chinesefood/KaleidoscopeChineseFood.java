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

        // 依赖 cookery 的注册（ModFoods / ModItems / ModFoodBiteRegistry / KTItems / DataMapsEvents）
        // 统一推迟到 runFoodPhase（client/server 入口点阶段执行）

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

        // 发射器行为注册移至 runFoodPhase（ModItems 注册之后，BlockItem 已就绪）

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
     * 由 client 与 server 入口点调用：这两个阶段在所有 main 入口点之后运行，
     * 此时 cookery 必然初始化完毕，效果 Holder 与 FOOD_DATA_MAP 均已就绪。
     * 一次性守护确保只执行一遍。
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
            // TeacupItem 构造器急切解析效果 supplier，此处 cookery 效果已注册完毕
            ModTea.registerTeacupBlocksAndItems();
            // BlockItem 已注册，此时 asItem() 能解析到真实物品
            KongmingLanternBlock.registerDispenserBehavior(ModBlocks.KONGMING_LANTERN.asItem());
            if (!FabricLoader.getInstance().isModLoaded("kaleidoscope_twilight")) {
                KTItems.register();
            }
            DataMapsEvents.register();
            // 1.1.10 新增：放置菜品与 Kaleidoscope Contraption 兼容（反射软依赖，
            // create 与 kaleidoscope_contraption 均加载时才注册食物位交互行为）
            if (FabricLoader.getInstance().isModLoaded("create")
                    && FabricLoader.getInstance().isModLoaded("kaleidoscope_contraption")) {
                com.bmt.kaleidoscope_chinesefood.compat.kaleidoscope_contraption.KaleidoscopeContraptionCompat.register();
            }
            foodPhaseDone = true;
        }
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(MODID, name);
    }

    public static Identifier fromNamespaceAndPath(String namespace, String id) {
        return Identifier.fromNamespaceAndPath(namespace, id);
    }
}
