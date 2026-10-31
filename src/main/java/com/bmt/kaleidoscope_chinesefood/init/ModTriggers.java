package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * 官方 1.1.16 新增：进度触发器（岩浆畅游）。1.20.1 还是 JSON 反序列化时代的
 * SimpleCriterionTrigger（1.20.5+ 才换 Codec），与官方 Forge 版同一写法。
 */
public class ModTriggers {
    public static ModTriggers.LavaSwimTrigger LAVA_SWIM;

    public ModTriggers() {
    }

    public static void register() {
        LAVA_SWIM = (ModTriggers.LavaSwimTrigger)CriteriaTriggers.register(new ModTriggers.LavaSwimTrigger());
    }

    public static class LavaSwimTrigger extends SimpleCriterionTrigger<ModTriggers.LavaSwimTrigger.Instance> {
        private static final ResourceLocation ID = KaleidoscopeChineseFood.id("lava_swim");

        public LavaSwimTrigger() {
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        protected ModTriggers.LavaSwimTrigger.Instance createInstance(JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
            return new ModTriggers.LavaSwimTrigger.Instance(player);
        }

        public void trigger(ServerPlayer player) {
            this.trigger(player, instance -> true);
        }

        public static class Instance extends AbstractCriterionTriggerInstance {
            public Instance(ContextAwarePredicate player) {
                super(ModTriggers.LavaSwimTrigger.ID, player);
            }
        }
    }
}
