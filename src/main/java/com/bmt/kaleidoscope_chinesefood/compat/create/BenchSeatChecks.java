package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModCookeryBlocks;
import com.zurrtum.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.zurrtum.create.api.behaviour.movement.MovementBehaviour;
import com.zurrtum.create.content.contraptions.actors.seat.SeatInteractionBehaviour;
import com.zurrtum.create.content.contraptions.actors.seat.SeatMovementBehaviour;
import com.zurrtum.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.world.level.block.Block;

/**
 * 竹躺椅在 Create 装置上的座位兼容（create-fly 原生路径）。
 * <p>
 * 官方 1.1.14 对竹躺椅的三行注册依赖 kaleidoscope_contraption 的行为类与座位高度表，
 * 而该模组没有 Fabric 构建；当 kaleidoscope_contraption 缺席时改走本类：
 * 直接继承 create-fly 的 {@link SeatMovementBehaviour} / {@link SeatInteractionBehaviour}
 * （与 liquor 端口同款做法），让长凳在装置（列车等）移动时被识别为座位、
 * 可落座/离座。仅当 {@code isModLoaded("create")} 且 kaleidoscope_contraption
 * 未加载时由主入口调用，两条路径互斥。
 */
public final class BenchSeatChecks {
    private BenchSeatChecks() {
    }

    public static void register() {
        Block bench = ModCookeryBlocks.STRIPPED_BAMBOO_BENCH;
        MovementBehaviour.REGISTRY.register(bench, new BenchSeatMovement());
        MovingInteractionBehaviour.REGISTRY.register(bench, new BenchSeatInteraction());
        KaleidoscopeChineseFood.LOGGER.info("竹躺椅已注册为 Create 装置座位（create-fly 原生 Seat API）");
    }

    /** 把长凳位置并入装置座位表，让 SeatIndex 追踪与上下客可用（liquor ChairBlockMovementBehaviour 同款）。 */
    static class BenchSeatMovement extends SeatMovementBehaviour {
        @Override
        public void startMoving(MovementContext context) {
            if (!context.contraption.getSeats().contains(context.localPos)) {
                context.contraption.getSeats().add(context.localPos);
            }
            super.startMoving(context);
        }
    }

    /** 装置移动中与座位碰撞的实体可直接落座（create-fly SeatInteractionBehaviour 原生逻辑）。 */
    static class BenchSeatInteraction extends SeatInteractionBehaviour {
    }
}
