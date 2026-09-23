package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.bmt.kaleidoscope_chinesefood.block.FreezerBlock;
import com.zurrtum.create.api.contraption.BlockMovementChecks;
import com.zurrtum.create.api.contraption.BlockMovementChecks.CheckResult;
import net.minecraft.core.Direction;

/**
 * 冰箱与 Create 装置（列车、轴承等）的搬运兼容，对应官方 1.1.12 的
 * {@code compat/create/CreateMovementChecks}。
 * <p>
 * 官方编译依赖 {@code com.simibubi.create}；Fabric 侧由第三方 Create 移植 Create Fly
 * 提供同签名 API（包名 {@code com.zurrtum.create}），故此处只改包名。
 * <p>
 * 两条规则：上下两半冰箱互相附着（装置搬运时不会被拆散）、冰箱整体视为易碎块。
 */
public final class CreateMovementChecks {
    private CreateMovementChecks() {
    }

    public static void register() {
        BlockMovementChecks.registerAttachedCheck((state, world, pos, direction) -> {
            if (!(state.getBlock() instanceof FreezerBlock)) {
                return CheckResult.PASS;
            }
            boolean isTop = state.getValue(FreezerBlock.TOP);
            if (!isTop && direction == Direction.UP) {
                return CheckResult.SUCCESS;
            }
            return isTop && direction == Direction.DOWN ? CheckResult.SUCCESS : CheckResult.FAIL;
        });
        BlockMovementChecks.registerBrittleCheck(
                state -> state.getBlock() instanceof FreezerBlock ? CheckResult.SUCCESS : CheckResult.PASS);
    }
}
