package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.entity.FirecrackerEntity;
import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.bmt.kaleidoscope_chinesefood.entity.YellowCroaker;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.SpawnPlacements.Type;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class ModEntities {
    public static final EntityType<FirecrackerEntity> FIRECRACKER = Builder.<FirecrackerEntity>of(FirecrackerEntity::new, MobCategory.MISC)
        .sized(0.25F, 0.25F)
        .clientTrackingRange(4)
        .updateInterval(10)
        .build(KaleidoscopeChineseFood.id("firecracker").toString());
    public static final EntityType<KongmingLanternEntity> KONGMING_LANTERN = Builder.<KongmingLanternEntity>of(KongmingLanternEntity::new, MobCategory.MISC)
        .sized(0.5F, 1.0F)
        .clientTrackingRange(10)
        .updateInterval(1)
        .build(KaleidoscopeChineseFood.id("kongming_lantern").toString());
    public static final EntityType<YellowCroaker> YELLOW_CROAKER = Builder.of(YellowCroaker::new, MobCategory.WATER_AMBIENT)
        .sized(0.5F, 0.3F)
        .clientTrackingRange(4)
        .build(KaleidoscopeChineseFood.id("yellow_croaker").toString());

    public ModEntities() {
    }

    public static void registerEntities() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "firecracker"), FIRECRACKER);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "kongming_lantern"), KONGMING_LANTERN);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "yellow_croaker"), YELLOW_CROAKER);

        // 原 Forge EntityAttributeCreationEvent
        FabricDefaultAttributeRegistry.register(YELLOW_CROAKER, AbstractFish.createAttributes().build());
        // 原 Forge FMLCommonSetupEvent#enqueueWork 里的 SpawnPlacements.register
        SpawnPlacements.register(YELLOW_CROAKER, Type.IN_WATER, Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
    }
}
