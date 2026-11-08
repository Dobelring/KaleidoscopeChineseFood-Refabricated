package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.event.InstantSmeltingEventHandler;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 1.2.0:即时熔炼掉落改写挂点(cookery 自带的 onGetDrops 已由
 * {@link InstantSmeltingEffectMixin} 关闭,这里按官方 1.2.0 语义重新实现:
 * 左键预算制 + instantSmeltingOnlyOres 仅矿石配置)。
 */
@Mixin({Block.class})
public abstract class InstantSmeltingDropsMixin {
   @ModifyReturnValue(
      method = {"getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"},
      at = {@At("RETURN")}
   )
   private static List<ItemStack> kcf$smeltDrops(
      List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool
   ) {
      if (original != null && !original.isEmpty() && level != null && !level.isClientSide()) {
         InstantSmeltingEventHandler.smeltDrops(original, level, entity, state);
      }

      return original;
   }
}
