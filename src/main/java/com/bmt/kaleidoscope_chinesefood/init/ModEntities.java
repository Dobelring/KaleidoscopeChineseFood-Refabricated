package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.entity.FirecrackerEntity;
import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static EntityType<FirecrackerEntity> FIRECRACKER;
    public static EntityType<KongmingLanternEntity> KONGMING_LANTERN;

    public static void register() {
        // 1.21.11：EntityType.Builder.build() 需要 ResourceKey 参数
        FIRECRACKER = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                KaleidoscopeChineseFood.id("firecracker"),
                Builder.<FirecrackerEntity>of(FirecrackerEntity::new, MobCategory.MISC)
                        .sized(0.25F, 0.25F)
                        .clientTrackingRange(4)
                        .updateInterval(10)
                        .build(ResourceKey.create(Registries.ENTITY_TYPE, KaleidoscopeChineseFood.id("firecracker")))
        );
        KONGMING_LANTERN = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                KaleidoscopeChineseFood.id("kongming_lantern"),
                Builder.<KongmingLanternEntity>of(KongmingLanternEntity::new, MobCategory.MISC)
                        .sized(0.5F, 1.0F)
                        .clientTrackingRange(10)
                        .updateInterval(1)
                        .build(ResourceKey.create(Registries.ENTITY_TYPE, KaleidoscopeChineseFood.id("kongming_lantern")))
        );
    }
}
