package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;

/**
 * 官方 1.1.16 新增：进度触发器（岩浆畅游）。原 NeoForge 用 DeferredRegister，
 * Fabric 直接 Registry.register（26.1.2 注册表为 BuiltInRegistries.TRIGGER_TYPES，
 * SimpleCriterionTrigger 已迁到 advancements.criterion 包，触发器全 Codec 化）。
 */
public class ModTriggers {
    public static ModTriggers.LavaSwimTrigger LAVA_SWIM;

    public ModTriggers() {
    }

    public static void register() {
        LAVA_SWIM = Registry.register(BuiltInRegistries.TRIGGER_TYPES, KaleidoscopeChineseFood.id("lava_swim"), new ModTriggers.LavaSwimTrigger());
    }

    public static class LavaSwimTrigger extends SimpleCriterionTrigger<ModTriggers.LavaSwimTrigger.Instance> {
        // 26.1 的 DFU 9 删掉了 Codec.unit，原版触发器用 MapCodec.unitCodec(常量实例)
        private static final ModTriggers.LavaSwimTrigger.Instance INSTANCE = new ModTriggers.LavaSwimTrigger.Instance();
        private static final Codec<ModTriggers.LavaSwimTrigger.Instance> CODEC = com.mojang.serialization.MapCodec.unitCodec(INSTANCE);

        public LavaSwimTrigger() {
        }

        @Override
        public Codec<ModTriggers.LavaSwimTrigger.Instance> codec() {
            return CODEC;
        }

        public void trigger(ServerPlayer player) {
            this.trigger(player, instance -> true);
        }

        public static class Instance implements SimpleCriterionTrigger.SimpleInstance {
            public Instance() {
            }

            @Override
            public Optional<ContextAwarePredicate> player() {
                return Optional.empty();
            }
        }
    }
}
