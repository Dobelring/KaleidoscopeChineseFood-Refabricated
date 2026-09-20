package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 26.1.2 里 {@code TrapDoorBlock.getType()} 是 protected、{@code toggle(...)} 是 private，
 * 福字方块需要用它们按活版门自身的 BlockSetType 判断能否手动开、并播放开关音效。
 * <p>
 * 工程目前没有 accesswidener 基础设施，所以走 mixin 加宽而不是 AW。
 */
@Mixin(TrapDoorBlock.class)
public interface TrapDoorBlockAccessor {
    @Accessor("type")
    BlockSetType kaleidoscope_chinesefood$getType();

    @Invoker("toggle")
    void kaleidoscope_chinesefood$toggle(BlockState state, Level level, BlockPos pos, @Nullable Player player);
}
