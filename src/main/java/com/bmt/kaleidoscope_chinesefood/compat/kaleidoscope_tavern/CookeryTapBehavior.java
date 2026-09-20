package com.bmt.kaleidoscope_chinesefood.compat.kaleidoscope_tavern;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.ITapBehavior;
import com.github.ysbbbbbb.kaleidoscopetavern.init.ModParticles;
import java.util.IdentityHashMap;
import java.util.Map;
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

/**
 * 包在酒馆原有水龙头行为外面的组合行为：先让原行为判断，不匹配时再看是不是要给厨房的汤锅/茶壶注水/注岩浆。
 * <p>
 * 为什么不照搬官方做成 {@code TapBlock} 的 {@code @Redirect}：酒水模组（kaleidoscope_world_liquor）
 * 已经用 {@code @Redirect} 占了 {@code TapBlock#tryOpen}/{@code #tick} 里的同一批调用点，
 * 而 {@code @Redirect} 对同一条指令是互斥的——两个模组都这么写，先应用的那个会把指令吃掉，
 * 后一个报 "failed injection check (0/1) succeeded" 直接崩在启动阶段。
 * 改成从 {@code TapBehaviorManager#get} 的返回值上包一层，用 {@code @Inject} 实现，可以和其他模组叠加。
 */
public final class CookeryTapBehavior implements ITapBehavior {
    /** 同一个原行为只包一次，避免每次取行为都新建对象。 */
    private static final Map<ITapBehavior, CookeryTapBehavior> CACHE = new IdentityHashMap<>();

    private final ITapBehavior delegate;

    private CookeryTapBehavior(ITapBehavior delegate) {
        this.delegate = delegate;
    }

    public static ITapBehavior of(ITapBehavior delegate) {
        if (delegate instanceof CookeryTapBehavior) {
            return delegate;
        }
        return CACHE.computeIfAbsent(delegate, CookeryTapBehavior::new);
    }

    @Override
    public boolean isMatch(
            Level level, @Nullable Player player, BlockPos tapPos, BlockState tapState, BlockState sourceState, BlockState destinationState
    ) {
        return this.delegate.isMatch(level, player, tapPos, tapState, sourceState, destinationState)
                || isCookeryMatch(level, tapPos, sourceState);
    }

    @Override
    public ParticleOptions onStartExtract(
            Level level, @Nullable Player player, BlockPos tapPos, BlockState tapState, BlockState sourceState, BlockState destinationState
    ) {
        return isCookeryMatch(level, tapPos, sourceState)
                ? getParticle(sourceState)
                : this.delegate.onStartExtract(level, player, tapPos, tapState, sourceState, destinationState);
    }

    @Override
    public void onEndExtract(Level level, BlockPos tapPos, BlockState tapState, BlockState sourceState, BlockState destinationState) {
        if (isCookeryMatch(level, tapPos, sourceState)) {
            fillCookery(level, tapPos, sourceState, destinationState);
        } else {
            this.delegate.onEndExtract(level, tapPos, tapState, sourceState, destinationState);
        }
    }

    /** 水源/岩浆源 + 正下方是能接收的厨房容器。 */
    private static boolean isCookeryMatch(Level level, BlockPos tapPos, BlockState sourceState) {
        if (!isWaterSource(sourceState) && !isLavaSource(sourceState)) {
            return false;
        }
        BlockPos belowPos = tapPos.below();
        if (level.getBlockEntity(belowPos) instanceof StockpotBlockEntity stockpot) {
            return !stockpot.hasLid() && stockpot.getStatus() == 0;
        }
        if (level.getBlockEntity(belowPos) instanceof TeapotBlockEntity teapot) {
            return teapot.getStatus() == 0 && teapot.getTeaFluidId().equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID);
        }
        return false;
    }

    private static boolean isWaterSource(BlockState sourceState) {
        if (sourceState.is(Blocks.WATER_CAULDRON)) {
            return true;
        }
        return sourceState.hasProperty(BlockStateProperties.WATERLOGGED)
                && Boolean.TRUE.equals(sourceState.getValue(BlockStateProperties.WATERLOGGED));
    }

    private static boolean isLavaSource(BlockState sourceState) {
        return sourceState.is(Blocks.LAVA_CAULDRON);
    }

    private static ParticleOptions getParticle(BlockState sourceState) {
        return isLavaSource(sourceState) ? ModParticles.LAVA_TAP_DRIP : ModParticles.WATER_TAP_DRIP;
    }

    private static void fillCookery(Level level, BlockPos tapPos, BlockState sourceState, BlockState destinationState) {
        BlockPos belowPos = tapPos.below();
        boolean isLava = isLavaSource(sourceState);
        if (level.getBlockEntity(belowPos) instanceof StockpotBlockEntity stockpot) {
            ((com.bmt.kaleidoscope_chinesefood.mixins.tavern.StockpotBlockEntityAccessor) stockpot)
                    .kaleidoscope_chinesefood$setSoupBaseId(isLava ? ModSoupBases.LAVA : ModSoupBases.WATER);
            stockpot.setStatus(1);
            stockpot.refresh();
            playSound(level, belowPos, isLava);
            ITapBehavior.sendParticles(level, tapPos);
            level.sendBlockUpdated(belowPos, destinationState, destinationState, 3);
        } else if (level.getBlockEntity(belowPos) instanceof TeapotBlockEntity teapot) {
            ResourceLocation fluidId = isLava
                    ? BuiltInRegistries.FLUID.getKey(Fluids.LAVA)
                    : BuiltInRegistries.FLUID.getKey(Fluids.WATER);
            ((com.bmt.kaleidoscope_chinesefood.mixins.tavern.TeapotBlockEntityAccessor) teapot)
                    .kaleidoscope_chinesefood$setTeaFluidId(fluidId);
            teapot.refresh();
            playSound(level, belowPos, isLava);
            ITapBehavior.sendParticles(level, tapPos);
            level.sendBlockUpdated(belowPos, destinationState, destinationState, 3);
        }
    }

    private static void playSound(Level level, BlockPos pos, boolean isLava) {
        if (isLava) {
            level.playSound(null, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else {
            level.playSound(null, pos, SoundEvents.AXOLOTL_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
