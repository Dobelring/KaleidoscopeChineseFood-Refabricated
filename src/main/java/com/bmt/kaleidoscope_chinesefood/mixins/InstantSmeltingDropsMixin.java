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
 * 26.3 适配：1.20.1 的 6 参 getDrops 末位参数 ItemStack 在 26.3 改为 ItemInstance 接口，
 * 拦截点语义不变（dropResources 全部掉落来源都会经过这里）。
 */
@Mixin(Block.class)
public abstract class InstantSmeltingDropsMixin {
    @ModifyReturnValue(
        method = {"getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;"},
        at = {@At("RETURN")}
    )
    private static List<ItemStack> kaleidoscope_chinesefood$smeltDrops(
        List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, net.minecraft.world.item.ItemInstance tool
    ) {
        if (original != null && !original.isEmpty() && level != null && !level.isClientSide()) {
            InstantSmeltingEventHandler.smeltDrops(original, level, entity, state);
        }
        return original;
    }
}
