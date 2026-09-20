package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry.PlateData;
import com.github.ysbbbbbb.kaleidoscopecookery.item.WithTooltipsBlockItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 拼盘（盘子）注册。
 * <p>
 * 不能把条目交给 cookery 的 {@code CommonRegistry.registerPlateBlocks()} 代注册：它虽然用 map 的 key
 * 注册方块/物品，但 {@code Properties.setId(...)} 一律用 {@code PortHelper.createBlockId/createItemId(path)}
 * 硬拼 cookery 命名空间（见 cookery 的 PlateBlockItem 构造器）。26.x 的
 * {@code DataComponentInitializers} 会按 {@code Properties} 的 id 调 {@code HolderGetter#getOrThrow}，
 * id 与注册键不一致时资源重载直接抛 {@code Missing element ResourceKey[minecraft:item / kaleidoscope_cookery:xxx]}
 * 导致世界无法加载——与工程里茶杯（{@code ModTea}）踩过的是同一个坑。
 * <p>
 * 所以这里自己注册方块与物品（id 与 Properties 一致），并在 cookery 初始化【之后】
 * 才把数据放进 {@code PLATE_DATA_MAP}：既不会被它代注册，又保留 cookery 侧
 * {@code PlateRegistry.getItem/getCount} 与它自己的创造栏对拼盘的查询。
 */
public class ModPlateRegistry {
    public static Identifier GOLDEN_APPLE_PLATTER;
    public static Block GOLDEN_APPLE_PLATTER_BLOCK;
    public static Item GOLDEN_APPLE_PLATTER_ITEM;

    /** 必须在所有 main 入口点之后调用（见 {@link KaleidoscopeChineseFood#runFoodPhase()}） */
    public static void init() {
        PlateData data = PlateData.create(4)
                .setServingItems(() -> Items.GOLDEN_APPLE)
                .setLootItem(Items.BOWL)
                .platterAABB();
        Identifier id = KaleidoscopeChineseFood.id("golden_apple_platter");

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        PlateBlock block = new PlateBlock(data.getMaxCount(), data.getServingItems(), Properties.of().setId(blockKey));
        VoxelShape aabb = data.getAABB();
        if (aabb != null) {
            block.setAABB(aabb);
        }

        GOLDEN_APPLE_PLATTER_BLOCK = Registry.register(BuiltInRegistries.BLOCK, id, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        WithTooltipsBlockItem item = new WithTooltipsBlockItem(
                block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix(), id.getPath()
        );
        item.registerBlocks(Item.BY_BLOCK, item);
        GOLDEN_APPLE_PLATTER_ITEM = Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        GOLDEN_APPLE_PLATTER = id;
        // cookery 自己的创造栏会遍历这个 map；此时它早已初始化完，不会再拿它代注册
        new PlateRegistry().registerPlateData(id, data);
    }
}
