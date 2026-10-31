package com.bmt.kaleidoscope_chinesefood.mixins;

import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.TableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 官方 1.1.16：允许把蛋糕放在 cookery 的餐桌上（餐桌不是完整方块，
 * 原版 canSurvive 会判 false 导致蛋糕放不上/被顶掉）。本模组硬依赖 cookery，
 * 直接引用其类是安全的。
 */
@Mixin({CakeBlock.class})
public class CakeBlockMixin {
    public CakeBlockMixin() {
    }

    @Inject(
        method = {"canSurvive"},
        at = {@At("HEAD")},
        cancellable = true
    )
    private void kaleidoscope$allowPlaceOnTable(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level.getBlockState(pos.below()).getBlock() instanceof TableBlock) {
            cir.setReturnValue(true);
        }
    }
}
