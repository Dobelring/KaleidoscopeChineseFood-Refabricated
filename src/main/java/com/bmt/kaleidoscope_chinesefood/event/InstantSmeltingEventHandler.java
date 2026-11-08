package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.config.ModConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;

/**
 * 对齐官方 1.1.15：接管 cookery"即时熔炼"效果的掉落熔炼，改为预算制——
 * 每次左键方块时刷新 16×(等级+1) 方块预算，预算耗尽后本轮不再熔炼，登出清理。
 * Forge 版挂在 PlayerInteractEvent.LeftClickBlock / PlayerLoggedOutEvent 上，Fabric 分别对应
 * AttackBlockCallback / ServerPlayConnectionEvents.DISCONNECT。
 */
public final class InstantSmeltingEventHandler {
    private static final Map<UUID, Integer> SMELT_BUDGETS = new HashMap<>();
    /** 1.2.0：配置开启时仅对矿石生效（26.3 无 forge 命名空间，走 c:ores，与厨房乐事的 c 系列一致）。 */
    private static final TagKey<Block> ORES_TAG = TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("ores"));

    private InstantSmeltingEventHandler() {
    }

    public static void register() {
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (!level.isClientSide()) {
                MobEffectInstance effect = player.getEffect(ModEffects.INSTANT_SMELTING);
                if (effect == null) {
                    SMELT_BUDGETS.remove(player.getUUID());
                } else {
                    SMELT_BUDGETS.put(player.getUUID(), 16 * (effect.getAmplifier() + 1));
                }
            }
            return InteractionResult.PASS;
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> SMELT_BUDGETS.remove(handler.player.getUUID()));
    }

    public static void smeltDrops(List<ItemStack> drops, ServerLevel level, Entity breaker, BlockState state) {
        if (breaker instanceof LivingEntity living) {
            MobEffectInstance effect = living.getEffect(ModEffects.INSTANT_SMELTING);
            if (effect == null || level.isClientSide()) {
                return;
            }
            // 1.2.0：配置开启时仅对矿石生效
            if (ModConfig.instantSmeltingOnlyOres && !state.is(ORES_TAG)) {
                return;
            }
            UUID uuid = living.getUUID();
            int budget = SMELT_BUDGETS.getOrDefault(uuid, 16 * (effect.getAmplifier() + 1));
            if (budget > 0) {
                boolean smeltedAny = false;
                for (int i = 0; i < drops.size(); i++) {
                    ItemStack stack = drops.get(i);
                    if (!stack.isEmpty()) {
                        SingleRecipeInput input = new SingleRecipeInput(stack);
                        Optional<SmeltingRecipe> recipeOpt = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level)
                                .map(holder -> holder.value());
                        if (recipeOpt.isPresent()) {
                            ItemStack result = recipeOpt.get().assemble(input);
                            if (!result.isEmpty()) {
                                int resultCount = result.getCount() * stack.getCount();
                                drops.set(i, result.copyWithCount(resultCount));
                                smeltedAny = true;
                            }
                        }
                    }
                }
                if (smeltedAny) {
                    SMELT_BUDGETS.put(uuid, budget - 1);
                }
            }
        }
    }
}
