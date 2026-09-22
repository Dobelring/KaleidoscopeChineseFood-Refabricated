package com.bmt.kaleidoscope_chinesefood.mixins.tavern;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.ITapBehavior;
import com.github.ysbbbbbb.kaleidoscopetavern.block.brew.TapBlock;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModParticles;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({TapBlock.class})
public abstract class TapBlockMixin {
    public TapBlockMixin() {
    }

    @WrapOperation(
        method = {"tryOpen"},
        remap = false,
        at = {@At(
            value = "INVOKE",
            target = "Lcom/github/ysbbbbbb/kaleidoscopetavern/api/blockentity/ITapBehavior;isMatch(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z",
            remap = false
        )}
    )
    private boolean kcf$redirectIsMatchTryOpen(
        ITapBehavior behavior,
        Level level,
        @Nullable Player player,
        BlockPos tapPos,
        BlockState tapState,
        BlockState sourceState,
        BlockState destinationState,
        Operation<Boolean> original
    ) {
        boolean originalResult = (Boolean)original.call(new Object[]{behavior, level, player, tapPos, tapState, sourceState, destinationState});
        return originalResult ? true : kcf$isCookeryMatch(level, tapPos, sourceState, destinationState);
    }

    @WrapOperation(
        method = {"tryOpen"},
        remap = false,
        at = {@At(
            value = "INVOKE",
            target = "Lcom/github/ysbbbbbb/kaleidoscopetavern/api/blockentity/ITapBehavior;onStartExtract(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/particles/ParticleOptions;",
            remap = false
        )}
    )
    private ParticleOptions kcf$redirectOnStartExtract(
        ITapBehavior behavior,
        Level level,
        @Nullable Player player,
        BlockPos tapPos,
        BlockState tapState,
        BlockState sourceState,
        BlockState destinationState,
        Operation<ParticleOptions> original
    ) {
        return kcf$isCookeryMatch(level, tapPos, sourceState, destinationState)
            ? kcf$getParticle(sourceState)
            : (ParticleOptions)original.call(new Object[]{behavior, level, player, tapPos, tapState, sourceState, destinationState});
    }

    @WrapOperation(
        method = {"tick"},
        at = {@At(
            value = "INVOKE",
            target = "Lcom/github/ysbbbbbb/kaleidoscopetavern/api/blockentity/ITapBehavior;isMatch(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z",
            remap = false
        )}
    )
    private boolean kcf$redirectIsMatchTick(
        ITapBehavior behavior,
        Level level,
        @Nullable Player player,
        BlockPos tapPos,
        BlockState tapState,
        BlockState sourceState,
        BlockState destinationState,
        Operation<Boolean> original
    ) {
        boolean originalResult = (Boolean)original.call(new Object[]{behavior, level, player, tapPos, tapState, sourceState, destinationState});
        return originalResult ? true : kcf$isCookeryMatch(level, tapPos, sourceState, destinationState);
    }

    @WrapOperation(
        method = {"tick"},
        at = {@At(
            value = "INVOKE",
            target = "Lcom/github/ysbbbbbb/kaleidoscopetavern/api/blockentity/ITapBehavior;onEndExtract(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V",
            remap = false
        )}
    )
    private void kcf$redirectOnEndExtract(
        ITapBehavior behavior, Level level, BlockPos tapPos, BlockState tapState, BlockState sourceState, BlockState destinationState, Operation<Void> original
    ) {
        if (kcf$isCookeryMatch(level, tapPos, sourceState, destinationState)) {
            kcf$fillCookery(level, tapPos, sourceState, destinationState);
        } else {
            original.call(new Object[]{behavior, level, tapPos, tapState, sourceState, destinationState});
        }
    }

    private static boolean kcf$isCookeryMatch(Level level, BlockPos tapPos, BlockState sourceState, BlockState destinationState) {
        if (!kcf$isWaterSource(sourceState) && !kcf$isLavaSource(sourceState)) {
            return false;
        } else {
            BlockPos belowPos = tapPos.below();
            if (level.getBlockEntity(belowPos) instanceof StockpotBlockEntity stockpot) {
                return stockpot.hasLid() ? false : stockpot.getStatus() == 0;
            } else if (level.getBlockEntity(belowPos) instanceof TeapotBlockEntity teapot) {
                return teapot.getStatus() != 0 ? false : teapot.getTeaFluidId().equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID);
            } else {
                return false;
            }
        }
    }

    private static boolean kcf$isWaterSource(BlockState sourceState) {
        return sourceState.is(Blocks.WATER_CAULDRON)
            ? true
            : sourceState.hasProperty(BlockStateProperties.WATERLOGGED) && Boolean.TRUE.equals(sourceState.getValue(BlockStateProperties.WATERLOGGED));
    }

    private static boolean kcf$isLavaSource(BlockState sourceState) {
        return sourceState.is(Blocks.LAVA_CAULDRON);
    }

    private static ParticleOptions kcf$getParticle(BlockState sourceState) {
        return kcf$isLavaSource(sourceState) ? (ParticleOptions)ModParticles.LAVA_TAP_DRIP : (ParticleOptions)ModParticles.WATER_TAP_DRIP;
    }

    private static void kcf$fillCookery(Level level, BlockPos tapPos, BlockState sourceState, BlockState destinationState) {
        BlockPos belowPos = tapPos.below();
        boolean isLava = kcf$isLavaSource(sourceState);
        if (level.getBlockEntity(belowPos) instanceof StockpotBlockEntity stockpot) {
            ResourceLocation soupBaseId = isLava ? ModSoupBases.LAVA : ModSoupBases.WATER;
            ((StockpotBlockEntityAccessor)stockpot).setSoupBaseId(soupBaseId);
            stockpot.setStatus(1);
            stockpot.refresh();
            kcf$playSound(level, belowPos, isLava);
            ITapBehavior.sendParticles(level, tapPos);
            level.sendBlockUpdated(belowPos, destinationState, destinationState, 3);
        } else {
            if (level.getBlockEntity(belowPos) instanceof TeapotBlockEntity teapot) {
                ResourceLocation fluidId = isLava ? BuiltInRegistries.FLUID.getKey(Fluids.LAVA) : BuiltInRegistries.FLUID.getKey(Fluids.WATER);
                ((TeapotBlockEntityAccessor)teapot).setTeaFluidId(fluidId);
                teapot.refresh();
                kcf$playSound(level, belowPos, isLava);
                ITapBehavior.sendParticles(level, tapPos);
                level.sendBlockUpdated(belowPos, destinationState, destinationState, 3);
            }
        }
    }

    private static void kcf$playSound(Level level, BlockPos pos, boolean isLava) {
        if (isLava) {
            level.playSound(null, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            level.playSound(null, pos, SoundEvents.AXOLOTL_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
