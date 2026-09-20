package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.StrippedBambooBenchBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.EightImmortalsTableBlock;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * 竹躺椅 / 竹八仙桌。
 * <p>
 * 与官方一致：注册在 <b>kaleidoscope_cookery</b> 命名空间（它们属于厨房的家具系列）。
 * 八仙桌直接复用 cookery 的 {@code EightImmortalsTableBlock}，躺椅是本模组实现的
 * {@link StrippedBambooBenchBlock}。
 * <p>
 * 在 food phase 调用（见 {@link KaleidoscopeChineseFood#runFoodPhase()}）：此时 cookery 已完成初始化。
 */
public final class ModCookeryBlocks {
    private static final String NAMESPACE = "kaleidoscope_cookery";

    public static Block STRIPPED_BAMBOO_BENCH;
    public static Block STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE;

    public static Item STRIPPED_BAMBOO_BENCH_ITEM;
    public static Item STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE_ITEM;

    private ModCookeryBlocks() {
    }

    public static void register() {
        STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE = registerBlock(
                "stripped_bamboo_eight_immortals_table", EightImmortalsTableBlock::new
        );
        STRIPPED_BAMBOO_BENCH = registerBlock(
                "stripped_bamboo_bench", StrippedBambooBenchBlock::new
        );
        STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE_ITEM =
                registerBlockItem("stripped_bamboo_eight_immortals_table", STRIPPED_BAMBOO_EIGHT_IMMORTALS_TABLE);
        STRIPPED_BAMBOO_BENCH_ITEM = registerBlockItem("stripped_bamboo_bench", STRIPPED_BAMBOO_BENCH);
    }

    private static Block registerBlock(String path, Function<Properties, Block> factory) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id(path));
        return Registry.register(BuiltInRegistries.BLOCK, key, factory.apply(StrippedBambooBenchBlock.benchProperties().setId(key)));
    }

    private static Item registerBlockItem(String path, Block block) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id(path));
        BlockItem item = new BlockItem(block, new Item.Properties().setId(key).useBlockDescriptionPrefix());
        item.registerBlocks(Item.BY_BLOCK, item);
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(NAMESPACE, path);
    }
}
