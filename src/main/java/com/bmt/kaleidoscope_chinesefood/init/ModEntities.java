package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.entity.FirecrackerEntity;
import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.bmt.kaleidoscope_chinesefood.entity.YellowCroaker;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.SpawnPlacementsInvoker;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModEntities {
    public static EntityType<FirecrackerEntity> FIRECRACKER;
    public static EntityType<KongmingLanternEntity> KONGMING_LANTERN;
    public static EntityType<YellowCroaker> YELLOW_CROAKER;

    public static void register() {
        FIRECRACKER = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                KaleidoscopeChineseFood.id("firecracker"),
                Builder.<FirecrackerEntity>of(FirecrackerEntity::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F)
                        .clientTrackingRange(4)
                        .updateInterval(10)
                        .build(KaleidoscopeChineseFood.id("firecracker").toString())
        );
        KONGMING_LANTERN = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                KaleidoscopeChineseFood.id("kongming_lantern"),
                Builder.<KongmingLanternEntity>of(KongmingLanternEntity::new, MobCategory.MISC)
                        .sized(0.5F, 1.0F)
                        .clientTrackingRange(10)
                        .updateInterval(1)
                        .build(KaleidoscopeChineseFood.id("kongming_lantern").toString())
        );
        YELLOW_CROAKER = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                KaleidoscopeChineseFood.id("yellow_croaker"),
                Builder.<YellowCroaker>of(YellowCroaker::new, MobCategory.WATER_AMBIENT)
                        .sized(0.5F, 0.3F)
                        .eyeHeight(0.3F)
                        .clientTrackingRange(4)
                        .build(KaleidoscopeChineseFood.id("yellow_croaker").toString())
        );

        // 属性：NeoForge 用 EntityAttributeCreationEvent，Fabric 走 FabricDefaultAttributeRegistry
        FabricDefaultAttributeRegistry.register(YELLOW_CROAKER, AbstractFish.createAttributes());
        // 生成规则：1.21.1 的 SpawnPlacements.register 是 private，通过 invoker mixin 调用
        SpawnPlacementsInvoker.kaleidoscope_chinesefood$register(
                YELLOW_CROAKER,
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WaterAnimal::checkSurfaceWaterAnimalSpawnRules
        );
    }
}
