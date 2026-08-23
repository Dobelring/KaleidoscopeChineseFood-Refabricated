package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.item.TooltipBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry.PlateData;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModPlateRegistry {
    public static Identifier GOLDEN_APPLE_PLATTER;
    public static Item GOLDEN_APPLE_PLATTER_ITEM;

    public static void init() {
        // Cookery's PlateBlockItem hard-codes its setId to the cookery namespace, so we register the
        // plate block/item ourselves (own namespace) with our own TooltipBlockItem. See ModTea.
        PlateData data = PlateData.create(4).setServingItems(() -> Items.GOLDEN_APPLE).setLootItem(Items.BOWL).platterAABB();
        GOLDEN_APPLE_PLATTER = KaleidoscopeChineseFood.id("golden_apple_platter");

        PlateBlock block = new PlateBlock(data.getMaxCount(), data.getServingItems(),
                BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, GOLDEN_APPLE_PLATTER)));
        VoxelShape aabb = data.getAABB();
        if (aabb != null) {
            block.setAABB(aabb);
        }
        Registry.register(BuiltInRegistries.BLOCK, GOLDEN_APPLE_PLATTER, block);

        TooltipBlockItem item = new TooltipBlockItem(block,
                new Item.Properties().setId(ResourceKey.create(Registries.ITEM, GOLDEN_APPLE_PLATTER)).useBlockDescriptionPrefix(),
                "tooltip.kaleidoscope_cookery.golden_apple_platter");
        item.registerBlocks(Item.BY_BLOCK, item);
        Registry.register(BuiltInRegistries.ITEM, GOLDEN_APPLE_PLATTER, item);
        GOLDEN_APPLE_PLATTER_ITEM = item;
    }
}
