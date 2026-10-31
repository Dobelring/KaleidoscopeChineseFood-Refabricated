package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

/**
 * 官方 1.1.16 新增：进度触发器（岩浆畅游）。原 NeoForge 用 DeferredRegister，
 * Fabric 直接 Registry.register。1.21.1 触发器已是 Codec 时代（Codec.unit 无参实例）。
 */
public class ModTriggers {
    public static ModTriggers.LavaSwimTrigger LAVA_SWIM;

    public ModTriggers() {
    }

    public static void register() {
        LAVA_SWIM = Registry.register(BuiltInRegistries.TRIGGER_TYPES, KaleidoscopeChineseFood.id("lava_swim"), new ModTriggers.LavaSwimTrigger());
    }

    public static class LavaSwimTrigger extends SimpleCriterionTrigger<ModTriggers.LavaSwimTrigger.Instance> {
        private static final Codec<ModTriggers.LavaSwimTrigger.Instance> CODEC = Codec.unit(ModTriggers.LavaSwimTrigger.Instance::new);

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
