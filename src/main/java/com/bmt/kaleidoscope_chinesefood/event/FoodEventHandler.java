package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class FoodEventHandler {
    private static final float MAX_ABSORPTION = 60.0F;
    private static final Map<Player, Integer> preEatFoodLevels = new HashMap<>();
    private static final Map<Player, Deque<PendingConversionTask>> pendingTasks = new HashMap<>();
    private static final Map<Player, Float> preDamageAbsorption = new HashMap<>();
    private static final Map<UUID, Float> shieldAbsorption = new ConcurrentHashMap<>();

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> setMaxAbsorption(handler.player));

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            Player player = handler.player;
            preEatFoodLevels.remove(player);
            pendingTasks.remove(player);
            preDamageAbsorption.remove(player);
            shieldAbsorption.remove(player.getUUID());
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!newPlayer.level().isClientSide()) {
                clearShieldAbsorption(newPlayer);
                pendingTasks.remove(newPlayer);
                preDamageAbsorption.remove(newPlayer);
                setMaxAbsorption(newPlayer);
            }
        });

        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!entity.level().isClientSide()) {
                if (entity instanceof Player player && player.hasEffect(ModEffects.SATURATION_SHIELD)) {
                    float ourAbsorption = getShieldAbsorption(player);
                    if (ourAbsorption > 0.0F) {
                        preDamageAbsorption.put(player, player.getAbsorptionAmount());
                    }
                }
            }
            return true;
        });

        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (!entity.level().isClientSide()) {
                if (entity instanceof Player player) {
                    Float absorptionBefore = preDamageAbsorption.remove(player);
                    if (absorptionBefore != null) {
                        float absorptionAfter = player.getAbsorptionAmount();
                        float absorbedDamage = absorptionBefore - absorptionAfter;
                        if (absorbedDamage > 0.0F) {
                            float ourAbsorption = getShieldAbsorption(player);
                            float newOurAbsorption = Math.max(0.0F, ourAbsorption - absorbedDamage);
                            setShieldAbsorption(player, newOurAbsorption);
                        }
                    }
                }
            }
        });
    }

    // Called from LivingEntityMixin (startUsingItem)
    public static void onStartEating(LivingEntity entity, ItemStack item) {
        if (!entity.level().isClientSide()) {
            if (entity instanceof Player player && item.has(DataComponents.FOOD)) {
                preEatFoodLevels.put(player, player.getFoodData().getFoodLevel());
            }
        }
    }

    // Called from LivingEntityMixin (completeUsingItem)
    public static void onFinishEating(LivingEntity entity, ItemStack stack) {
        if (!entity.level().isClientSide()) {
            if (entity instanceof Player player) {
                if (stack.has(DataComponents.FOOD)) {
                    FoodProperties foodProperties = stack.get(DataComponents.FOOD);
                    if (foodProperties != null) {
                        MobEffectInstance effectInstance = player.getEffect(ModEffects.SATURATION_SHIELD);
                        if (effectInstance == null) {
                            preEatFoodLevels.remove(player);
                        } else {
                            Integer preEatLevel = preEatFoodLevels.remove(player);
                            if (preEatLevel != null) {
                                int overflow = Math.max(0, preEatLevel + foodProperties.nutrition() - 20);
                                if (overflow > 0) {
                                    int amplifier = effectInstance.getAmplifier();
                                    pendingTasks
                                            .computeIfAbsent(player, k -> new ArrayDeque<>())
                                            .add(new PendingConversionTask(overflow, amplifier));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Called from PlayerTickMixin (Player#tick tail)
    public static void onPlayerTick(Player player) {
        if (!player.level().isClientSide()) {
            Deque<PendingConversionTask> tasks = pendingTasks.remove(player);
            if (tasks != null) {
                tasks.forEach(task -> processConversionTask(player, task));
            }

            // Detect SATURATION_SHIELD removal/expiry and clear the shield absorption.
            if (getShieldAbsorption(player) > 0.0F && !player.hasEffect(ModEffects.SATURATION_SHIELD)) {
                clearShieldAbsorption(player);
                preDamageAbsorption.remove(player);
            }
        }
    }

    private static void setMaxAbsorption(Player player) {
        AttributeInstance maxAbsorption = player.getAttribute(Attributes.MAX_ABSORPTION);
        if (maxAbsorption != null && maxAbsorption.getBaseValue() < 60.0) {
            maxAbsorption.setBaseValue(60.0);
        }
    }

    private static void processConversionTask(Player player, PendingConversionTask task) {
        if (player.hasEffect(ModEffects.SATURATION_SHIELD)) {
            float conversionRatio = 0.5F + task.amplifier * 1.0F;
            float currentOurAbsorption = getShieldAbsorption(player);
            float actualAbsorptionToAdd = Math.min(task.overflow * conversionRatio, MAX_ABSORPTION - currentOurAbsorption);
            if (actualAbsorptionToAdd > 0.0F) {
                float newOurAbsorption = currentOurAbsorption + actualAbsorptionToAdd;
                setShieldAbsorption(player, newOurAbsorption);
                player.setAbsorptionAmount(player.getAbsorptionAmount() + actualAbsorptionToAdd);
            }
        }
    }

    private static void clearShieldAbsorption(Player player) {
        float ourAbsorption = getShieldAbsorption(player);
        if (ourAbsorption > 0.0F) {
            float newAbsorption = Math.max(0.0F, player.getAbsorptionAmount() - ourAbsorption);
            player.setAbsorptionAmount(newAbsorption);
            shieldAbsorption.remove(player.getUUID());
        }
    }

    private static float getShieldAbsorption(Player player) {
        return shieldAbsorption.getOrDefault(player.getUUID(), 0.0F);
    }

    private static void setShieldAbsorption(Player player, float value) {
        shieldAbsorption.put(player.getUUID(), value);
    }

    private static class PendingConversionTask {
        final int overflow;
        final int amplifier;

        PendingConversionTask(int overflow, int amplifier) {
            this.overflow = overflow;
            this.amplifier = amplifier;
        }
    }
}
