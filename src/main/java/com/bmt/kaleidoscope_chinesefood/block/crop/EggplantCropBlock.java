package com.bmt.kaleidoscope_chinesefood.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class EggplantCropBlock extends BaseCropBlock {
    public EggplantCropBlock(Properties properties, Supplier<Item> result, Supplier<Item> seed) {
        super(properties, result, seed);
    }
}
