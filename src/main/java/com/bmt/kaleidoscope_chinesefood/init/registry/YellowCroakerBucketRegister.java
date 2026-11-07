package com.bmt.kaleidoscope_chinesefood.init.registry;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * 1.2.0：黄鱼桶的发射器行为——像水桶一样朝目标格倒水放鱼并返回空桶，
 * 失败则按默认行为弹出物品（对齐官方 init.registry.YellowCroakerBucketRegister）。
 */
public class YellowCroakerBucketRegister {
    private YellowCroakerBucketRegister() {
    }

    public static void register() {
        DispenserBlock.registerBehavior(ModItems.YELLOW_CROAKER_BUCKET, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior fallback = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource source, ItemStack stack) {
                DispensibleContainerItem container = (DispensibleContainerItem)stack.getItem();
                Direction facing = source.getBlockState().getValue(BlockStateProperties.FACING);
                BlockPos targetPos = source.getPos().relative(facing);
                ServerLevel level = source.getLevel();
                if (container.emptyContents(null, level, targetPos, null)) {
                    container.checkExtraContent(null, level, stack, targetPos);
                    return new ItemStack(Items.BUCKET);
                }
                return this.fallback.dispense(source, stack);
            }
        });
    }
}
