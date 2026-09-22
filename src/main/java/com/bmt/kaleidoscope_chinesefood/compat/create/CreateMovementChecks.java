package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.bmt.kaleidoscope_chinesefood.block.FreezerBlock;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;
import net.minecraft.core.Direction;

public class CreateMovementChecks {
    public CreateMovementChecks() {
    }

    public static void register() {
        BlockMovementChecks.registerAttachedCheck((state, world, pos, direction) -> {
            if (state.getBlock() instanceof FreezerBlock) {
                boolean isTop = state.getValue(FreezerBlock.TOP);
                if (!isTop && direction == Direction.UP) {
                    return CheckResult.SUCCESS;
                } else {
                    return isTop && direction == Direction.DOWN ? CheckResult.SUCCESS : CheckResult.FAIL;
                }
            } else {
                return CheckResult.PASS;
            }
        });
        BlockMovementChecks.registerBrittleCheck(state -> state.getBlock() instanceof FreezerBlock ? CheckResult.SUCCESS : CheckResult.PASS);
    }
}
