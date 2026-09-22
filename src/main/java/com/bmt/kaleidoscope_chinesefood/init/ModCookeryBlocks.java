package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.block.StrippedBambooBenchBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.EightImmortalsTableBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public class ModCookeryBlocks {
    // 注意：原 Forge 版的 DeferredRegister 使用的 modid 就是 "kaleidoscope_cookery"，
    // 即这两个方块/物品注册在厨艺的命名空间下，Fabric 侧保持同样行为
    private static final String COOKERY_MOD_ID = "kaleidoscope_cookery";
    public static final Block STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE = new EightImmortalsTableBlock();
    public static final Block STRIPPED_BAMBOO_BENCH = new StrippedBambooBenchBlock();
    public static final Item STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE_ITEM = new BlockItem(STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE, new Properties());
    public static final Item STRIPPED_BAMBOO_BENCH_ITEM = new BlockItem(STRIPPED_BAMBOO_BENCH, new Properties());

    public ModCookeryBlocks() {
    }

    public static void registerCookeryBlocks() {
        Registry.register(
            BuiltInRegistries.BLOCK,
            new ResourceLocation(COOKERY_MOD_ID, "stripped_bamboo_eight_immortals_table"),
            STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE
        );
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(COOKERY_MOD_ID, "stripped_bamboo_bench"), STRIPPED_BAMBOO_BENCH);
        Registry.register(
            BuiltInRegistries.ITEM,
            new ResourceLocation(COOKERY_MOD_ID, "stripped_bamboo_eight_immortals_table"),
            STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE_ITEM
        );
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(COOKERY_MOD_ID, "stripped_bamboo_bench"), STRIPPED_BAMBOO_BENCH_ITEM);
    }
}
