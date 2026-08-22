package com.bmt.kaleidoscope_chinesefood.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;

public class EggplantCropBlock extends BaseCropBlock {
    public EggplantCropBlock(Supplier<Item> result, Supplier<Item> seed) {
        super(result, seed);
    }
}
