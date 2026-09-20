package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.block.FuCharacterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 门被红石切换后，主动刷新贴在它面上的福字贴图状态。
 * <p>
 * 挂在 {@code neighborChanged} 上是因为它就是红石开门/关门的入口（门确实响应了红石，说明它一定被调用过），
 * 不依赖原版"邻居形状更新"链能否把状态推到福字上——门自己 setBlock 用的是 flags=10，不带形状更新。
 */
@Mixin(DoorBlock.class)
public abstract class DoorBlockMixin {
    @Inject(method = "neighborChanged", at = @At("TAIL"))
    private void kaleidoscope_chinesefood$refreshAttachedFuCharacter(
            BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean isMoving, CallbackInfo ci
    ) {
        FuCharacterBlock.refreshAttached(level, pos, level.getBlockState(pos));
    }
}
