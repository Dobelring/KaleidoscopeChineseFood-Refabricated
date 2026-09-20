package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModPlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 给 cookery 的盘子方块补上 EntityBlock：只有附魔金苹果拼盘会产出方块实体
 * （用于附魔光效渲染），其余拼盘仍返回 null。
 */
@Mixin(PlateBlock.class)
@Implements(@Interface(iface = EntityBlock.class, prefix = "enchanted_plate$"))
public abstract class PlateBlockMixin {
    @Nullable
    public BlockEntity enchanted_plate$newBlockEntity(BlockPos pos, BlockState state) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return key != null && key.equals(ModPlateRegistry.ENCHANTED_GOLDEN_APPLE_PLATTER)
                ? new EnchantedPlateBlockEntity(pos, state)
                : null;
    }
}
