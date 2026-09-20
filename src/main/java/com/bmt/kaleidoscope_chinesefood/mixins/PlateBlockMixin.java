package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModPlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 让附魔金苹果拼盘的方块带上方块实体（渲染附魔光效）。
 * <p>
 * 拼盘方块全部由 cookery 的 {@code PlateBlock} 承载，无法给单个实例加接口，
 * 所以在 {@code PlateBlock} 上统一 {@code @Implements(EntityBlock)}，
 * 由 {@code newBlockEntity} 按注册 id 只对附魔拼盘返回方块实体，其余拼盘返回 null。
 */
@Mixin(PlateBlock.class)
@Implements(@Interface(iface = EntityBlock.class, prefix = "enchanted_plate$"))
public abstract class PlateBlockMixin {
    @Nullable
    public BlockEntity enchanted_plate$newBlockEntity(BlockPos pos, BlockState state) {
        Identifier key = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return key != null && key.equals(ModPlateRegistry.ENCHANTED_GOLDEN_APPLE_PLATTER)
                ? new EnchantedPlateBlockEntity(pos, state)
                : null;
    }
}
