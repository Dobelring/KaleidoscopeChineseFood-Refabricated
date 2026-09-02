package com.bmt.kaleidoscope_chinesefood.compat.kaleidoscope_contraption;

import com.bmt.kaleidoscope_chinesefood.init.ModFoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

/**
 * 放置菜品与 Kaleidoscope Contraption（Create 附属模组）的兼容：
 * 让水煮鱼/水煮肉片/黄鱼汤/红米卷/黄鱼豆腐汤等大份菜肴方块
 * 在机械动力装置（列车等）移动时仍可交互食用。
 * 通过反射注册（软依赖，create 和 kaleidoscope_contraption 均加载时执行）。
 */
public class KaleidoscopeContraptionCompat {
    public static void register() {
        try {
            Class<?> interactionClass = Class.forName(
                    "com.sshakusora.kaleidoscope_contraption.content.behaviour.interaction.FoodBiteBlockMovingInteraction");
            Constructor<?> ctor = interactionClass.getConstructor();
            Object interaction = ctor.newInstance();

            register(ModFoodBiteRegistry.SICHUAN_BOILED_FISH, interaction);
            register(ModFoodBiteRegistry.SICHUAN_BOILED_PORK_SLICES, interaction);
            register(ModFoodBiteRegistry.YELLOW_CROAKER_SOUP, interaction);
            register(ModFoodBiteRegistry.RED_RICE_ROLL, interaction);
            register(ModFoodBiteRegistry.YELLOW_CROAKER_TOFU_SOUP, interaction);
        } catch (Throwable t) {
            // 反射失败时静默跳过
        }
    }

    private static void register(ResourceLocation id, Object interaction) {
        if (id == null) {
            return;
        }
        try {
            Block block = FoodBiteRegistry.getBlock(id);
            if (block == null) {
                return;
            }
            // MovingInteractionBehaviour.REGISTRY.register(block, interaction)
            Class<?> behaviourClass = Class.forName("com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour");
            Field registryField = behaviourClass.getField("REGISTRY");
            Object registry = registryField.get(null);
            Method registerMethod = registry.getClass().getMethod("register", Block.class, behaviourClass);
            registerMethod.invoke(registry, block, interaction);
        } catch (Throwable t) {
            // 静默跳过
        }
    }
}
