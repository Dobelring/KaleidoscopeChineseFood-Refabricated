package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.block.FuCharacterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 活版门被红石切换后，主动刷新贴在它面上的福字贴图状态（原因同 {@link DoorBlockMixin}） */
@Mixin(TrapDoorBlock.class)
public abstract class TrapDoorBlockMixin {
   @Inject(method = "neighborChanged", at = @At("TAIL"))
   private void kaleidoscope_chinesefood$refreshAttachedFuCharacter(
      BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving, CallbackInfo ci
   ) {
      FuCharacterBlock.refreshAttached(level, pos, level.getBlockState(pos));
   }
}
