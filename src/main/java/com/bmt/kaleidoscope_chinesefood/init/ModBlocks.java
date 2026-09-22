package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.block.BowlStackBlock;
import com.bmt.kaleidoscope_chinesefood.block.CoupletBlock;
import com.bmt.kaleidoscope_chinesefood.block.FirecrackerBlock;
import com.bmt.kaleidoscope_chinesefood.block.FreezerBlock;
import com.bmt.kaleidoscope_chinesefood.block.FuCharacterBlock;
import com.bmt.kaleidoscope_chinesefood.block.HorizontalBannerBlock;
import com.bmt.kaleidoscope_chinesefood.block.KongmingLanternBlock;
import com.bmt.kaleidoscope_chinesefood.block.MooncakeBlock;
import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.block.SaltBlock;
import com.bmt.kaleidoscope_chinesefood.block.crop.EggplantCropBlock;
import com.bmt.kaleidoscope_chinesefood.block.misc.CornBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.StackableFoodBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModBlocks {
    public static final Block CORN_RISTRA = new CornBlock();
    public static final Block FREEZER = newFreezer();
    public static final Item FREEZER_ITEM = new BlockItem(FREEZER, new Item.Properties());
    public static final Block FREEZER_GREEN = newFreezer();
    public static final Item FREEZER_GREEN_ITEM = new BlockItem(FREEZER_GREEN, new Item.Properties());
    public static final Block FREEZER_ORANGE = newFreezer();
    public static final Item FREEZER_ORANGE_ITEM = new BlockItem(FREEZER_ORANGE, new Item.Properties());
    public static final Block FREEZER_PINK = newFreezer();
    public static final Item FREEZER_PINK_ITEM = new BlockItem(FREEZER_PINK, new Item.Properties());
    public static final Block FREEZER_LIGHT_BLUE = newFreezer();
    public static final Item FREEZER_LIGHT_BLUE_ITEM = new BlockItem(FREEZER_LIGHT_BLUE, new Item.Properties());
    public static final Block FREEZER_YELLOW = newFreezer();
    public static final Item FREEZER_YELLOW_ITEM = new BlockItem(FREEZER_YELLOW, new Item.Properties());
    public static final Block BOWL_STACK = new BowlStackBlock(
        Properties.of().mapColor(MapColor.WOOD).strength(1.0F, 3.0F).sound(SoundType.WOOD).noOcclusion()
    );
    public static final Block PICKLE_JAR = new PickleJarBlock(
        Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.DECORATED_POT).noOcclusion()
    );
    public static final Block COUPLET = new CoupletBlock(
        Properties.of().strength(0.0F).mapColor(MapColor.FIRE).noOcclusion().sound(SoundType.WOOL)
    );
    public static final Block HORIZONTAL_BANNER = new HorizontalBannerBlock(
        Properties.of().mapColor(MapColor.FIRE).strength(0.0F).noOcclusion().sound(SoundType.WOOL)
    );
    public static final Block FU_CHARACTER = new FuCharacterBlock(
        Properties.of().mapColor(MapColor.FIRE).strength(0.0F).noOcclusion().sound(SoundType.WOOL)
    );
    public static final Block SALT_BLOCK = new SaltBlock(
        Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(0.1F).noOcclusion().sound(SoundType.GLASS)
    );
    public static final Block MOONCAKE_BLOCK = new MooncakeBlock(
        Properties.of().strength(0.1F, 0.8F).mapColor(MapColor.WOOD).sound(SoundType.WOOD).noOcclusion()
    );
    public static final Block FIRECRACKER = new FirecrackerBlock(
        Properties.of().mapColor(MapColor.FIRE).strength(0.1F).sound(SoundType.CANDLE).noOcclusion()
    );
    public static final Block KONGMING_LANTERN = new KongmingLanternBlock(
        Properties.of().mapColor(MapColor.FIRE).strength(0.1F, 0.8F).noOcclusion().sound(SoundType.WOOL)
    );
    public static final Block BAMBOO_STEAMED_EGG = StackableFoodBlock.create()
        .maxCount(4)
        .item(() -> ModItems.BAMBOO_STEAMED_EGG)
        .shapes(
            new VoxelShape[]{
                Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0),
                Shapes.or(Block.box(7.0, 0.0, 1.0, 15.0, 8.0, 9.0), Block.box(1.0, 0.0, 7.0, 9.0, 8.0, 15.0)),
                Shapes.or(Block.box(0.0, 0.0, 6.0, 16.0, 8.0, 15.0), Block.box(4.0, 0.0, 0.0, 12.0, 8.0, 15.0)),
                Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
            }
        )
        .build();
    public static final Block EGGPLANT_CROP = new EggplantCropBlock(() -> ModItems.EGGPLANT, () -> ModItems.EGGPLANT_SEED);

    public ModBlocks() {
    }

    private static Block newFreezer() {
        return new FreezerBlock(Properties.of().mapColor(MapColor.METAL).strength(3.0F, 1200.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", name), new BlockItem(block, new Item.Properties()));
    }

    public static void registerBlocks() {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "corn_ristra"), CORN_RISTRA);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "freezer"), FREEZER);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "freezer_green"), FREEZER_GREEN);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "freezer_orange"), FREEZER_ORANGE);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "freezer_pink"), FREEZER_PINK);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "freezer_light_blue"), FREEZER_LIGHT_BLUE);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "freezer_yellow"), FREEZER_YELLOW);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "bowl_stack"), BOWL_STACK);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "pickle_jar"), PICKLE_JAR);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "couplet"), COUPLET);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "horizontal_scroll"), HORIZONTAL_BANNER);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "fu_character"), FU_CHARACTER);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "salt"), SALT_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "mooncake"), MOONCAKE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "firecracker"), FIRECRACKER);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "kongming_lantern"), KONGMING_LANTERN);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "bamboo_steamed_egg"), BAMBOO_STEAMED_EGG);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("kaleidoscope_chinesefood", "eggplant_crop"), EGGPLANT_CROP);

        // 方块物品（原 Forge 的 registerBlock/registerFreezerItem 会顺带把方块物品注册进 ModItems.ITEMS，
        // Fabric 侧统一在这里显式注册；mooncake / firecracker / bamboo_steamed_egg / eggplant_crop
        // 的方块物品是自定义 Item，在 ModItems 里注册）
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "corn_ristra"), new BlockItem(CORN_RISTRA, new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "freezer"), FREEZER_ITEM);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "freezer_green"), FREEZER_GREEN_ITEM);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "freezer_orange"), FREEZER_ORANGE_ITEM);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "freezer_pink"), FREEZER_PINK_ITEM);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "freezer_light_blue"), FREEZER_LIGHT_BLUE_ITEM);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("kaleidoscope_chinesefood", "freezer_yellow"), FREEZER_YELLOW_ITEM);
        registerBlockItem("bowl_stack", BOWL_STACK);
        registerBlockItem("pickle_jar", PICKLE_JAR);
        registerBlockItem("couplet", COUPLET);
        registerBlockItem("horizontal_scroll", HORIZONTAL_BANNER);
        registerBlockItem("fu_character", FU_CHARACTER);
        registerBlockItem("salt", SALT_BLOCK);
        registerBlockItem("kongming_lantern", KONGMING_LANTERN);
    }
}
