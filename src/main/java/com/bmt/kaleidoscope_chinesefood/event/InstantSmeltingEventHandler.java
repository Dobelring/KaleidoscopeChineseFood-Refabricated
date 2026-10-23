package com.bmt.kaleidoscope_chinesefood.event;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

/**
 * 对齐官方 1.1.15：接管 cookery"即时熔炼"效果的掉落熔炼，改为预算制——
 * 每次左键方块时刷新 16×(等级+1) 方块预算，预算耗尽后本轮不再熔炼，登出清理。
 * Forge 版挂在 PlayerInteractEvent.LeftClickBlock / PlayerLoggedOutEvent 上，Fabric 分别对应
 * AttackBlockCallback / ServerPlayConnectionEvents.DISCONNECT。
 */
public final class InstantSmeltingEventHandler {
   private static final Map<UUID, Integer> SMELT_BUDGETS = new HashMap<>();

   private InstantSmeltingEventHandler() {
   }

   public static void register() {
      AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
         if (!level.isClientSide) {
            MobEffectInstance effect = player.getEffect((MobEffect)ModEffects.INSTANT_SMELTING.get());
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

   public static void smeltDrops(List<ItemStack> drops, ServerLevel level, Entity breaker) {
      if (breaker instanceof LivingEntity living) {
         MobEffectInstance effect = living.getEffect((MobEffect)ModEffects.INSTANT_SMELTING.get());
         if (effect != null && !level.isClientSide) {
            UUID uuid = living.getUUID();
            int budget = SMELT_BUDGETS.getOrDefault(uuid, 16 * (effect.getAmplifier() + 1));
            if (budget > 0) {
               boolean smeltedAny = false;

               for (int i = 0; i < drops.size(); i++) {
                  ItemStack stack = drops.get(i);
                  if (!stack.isEmpty()) {
                     SimpleContainer input = new SimpleContainer(stack);
                     Optional<SmeltingRecipe> recipeOpt = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level);
                     if (recipeOpt.isPresent()) {
                        ItemStack result = recipeOpt.get().assemble(input, level.registryAccess());
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
}
