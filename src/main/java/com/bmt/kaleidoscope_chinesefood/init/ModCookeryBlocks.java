package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.block.StrippedBambooBenchBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.EightImmortalsTableBlock;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 竹制家具。与官方一致，注册在 {@code kaleidoscope_cookery} 命名空间下
 * （八仙桌直接复用 cookery 自己的方块实现）。
 */
public class ModCookeryBlocks {
    private static final String COOKERY = "kaleidoscope_cookery";

    public static Block STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE;
    public static Block STRIPPED_BAMBOO_BENCH;

    public static void register() {
        STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE = registerBlock("stripped_bamboo_eight_immortals_table", EightImmortalsTableBlock::new);
        STRIPPED_BAMBOO_BENCH = registerBlock("stripped_bamboo_bench", StrippedBambooBenchBlock::new);
    }

    private static Block registerBlock(String name, Supplier<Block> supplier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(COOKERY, name);
        Block block = Registry.register(BuiltInRegistries.BLOCK, id, supplier.get());
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
        return block;
    }
}
