package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 26.1.2 里 {@code SpawnPlacements.register} 仍是 private，Fabric API 也没有对应注册入口
 * （NeoForge 用 RegisterSpawnPlacementsEvent），所以开个 invoker 给黄花鱼注册生成规则。
 */
@Mixin(SpawnPlacements.class)
public interface SpawnPlacementsInvoker {
    @Invoker("register")
    static <T extends Mob> void kaleidoscope_chinesefood$register(
            EntityType<T> type,
            SpawnPlacementType placementType,
            Heightmap.Types heightmap,
            SpawnPlacements.SpawnPredicate<T> predicate
    ) {
        throw new AssertionError("Mixin invoker was not transformed");
    }
}
