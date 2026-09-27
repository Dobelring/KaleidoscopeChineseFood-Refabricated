package com.bmt.kaleidoscope_chinesefood.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

public class EggplantCropBlock extends BaseCropBlock {
    public EggplantCropBlock(Supplier<Item> result, Supplier<Item> seed) {
        super(result, seed);
    }

    // 官方不走掉落表 JSON，破坏掉落全在代码里：成熟掉果实，任何阶段掉种子
    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder lootParamsBuilder) {
        int age = this.getAge(state);
        List<ItemStack> drops = new ArrayList<>();
        if (age >= this.getMaxAge()) {
            drops.add(new ItemStack((ItemLike) this.result.get()));
        }
        drops.add(new ItemStack((ItemLike) this.seed.get()));
        return drops;
    }
}
