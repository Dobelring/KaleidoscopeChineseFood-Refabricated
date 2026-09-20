package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModPlateRegistry;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 拼盘方块是 cookery 在自己的初始化阶段按 {@code PlateRegistry.PLATE_DATA_MAP} 代注册的，
 * 而 chinesefood 的初始化排在 cookery 之前，建方块实体类型时拿不到方块实例，
 * 所以这里用空的 validBlocks + 按注册 id 判断的 isValid 顶替。
 */
public class EnchantedPlateBlockEntityType extends BlockEntityType<EnchantedPlateBlockEntity> {
    public EnchantedPlateBlockEntityType() {
        super(EnchantedPlateBlockEntity::new, Set.of(), null);
    }

    @Override
    public boolean isValid(BlockState state) {
        Block plate = BuiltInRegistries.BLOCK.get(ModPlateRegistry.ENCHANTED_GOLDEN_APPLE_PLATTER);
        return plate != Blocks.AIR && state.getBlock() == plate;
    }
}
