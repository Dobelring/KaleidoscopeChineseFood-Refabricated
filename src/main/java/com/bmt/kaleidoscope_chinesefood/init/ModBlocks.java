package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
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
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;

public class ModBlocks {
    public static CornBlock CORN_RISTRA;
    public static FreezerBlock FREEZER;
    public static FreezerBlock FREEZER_GREEN;
    public static FreezerBlock FREEZER_ORANGE;
    public static FreezerBlock FREEZER_PINK;
    public static FreezerBlock FREEZER_LIGHT_BLUE;
    public static FreezerBlock FREEZER_YELLOW;
    public static PickleJarBlock PICKLE_JAR;
    public static BowlStackBlock BOWL_STACK;
    public static SaltBlock SALT_BLOCK;
    public static FirecrackerBlock FIRECRACKER;
    public static FuCharacterBlock FU_CHARACTER;
    public static CoupletBlock COUPLET;
    public static HorizontalBannerBlock HORIZONTAL_BANNER;
    public static MooncakeBlock MOONCAKE_BLOCK;
    public static KongmingLanternBlock KONGMING_LANTERN;
    public static Block BAMBOO_STEAMED_EGG;
    public static EggplantCropBlock EGGPLANT_CROP;

    public static void register() {
        CORN_RISTRA = register("corn_ristra", CornBlock::new);
        FREEZER = registerFreezer("freezer");
        FREEZER_GREEN = registerFreezer("freezer_green");
        FREEZER_ORANGE = registerFreezer("freezer_orange");
        FREEZER_PINK = registerFreezer("freezer_pink");
        FREEZER_LIGHT_BLUE = registerFreezer("freezer_light_blue");
        FREEZER_YELLOW = registerFreezer("freezer_yellow");
        PICKLE_JAR = register(
                "pickle_jar",
                () -> new PickleJarBlock(Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 3.0F).sound(SoundType.DECORATED_POT).noOcclusion())
        );
        BOWL_STACK = register(
                "bowl_stack", () -> new BowlStackBlock(Properties.of().mapColor(MapColor.WOOD).strength(1.0F, 3.0F).sound(SoundType.WOOD).noOcclusion())
        );
        SALT_BLOCK = register(
                "salt_block", () -> new SaltBlock(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(0.1F).noOcclusion().sound(SoundType.GLASS))
        );
        FIRECRACKER = register(
                "firecracker", () -> new FirecrackerBlock(Properties.of().mapColor(MapColor.FIRE).strength(0.1F).sound(SoundType.CANDLE).noOcclusion())
        );
        FU_CHARACTER = register(
                "fu_character", () -> new FuCharacterBlock(Properties.of().mapColor(MapColor.FIRE).strength(0.0F).noOcclusion().sound(SoundType.WOOL))
        );
        COUPLET = register(
                "couplet", () -> new CoupletBlock(Properties.of().mapColor(MapColor.FIRE).strength(0.0F).noOcclusion().sound(SoundType.WOOL))
        );
        HORIZONTAL_BANNER = register(
                "horizontal_banner", () -> new HorizontalBannerBlock(Properties.of().mapColor(MapColor.FIRE).strength(0.0F).noOcclusion().sound(SoundType.WOOL))
        );
        MOONCAKE_BLOCK = register(
                "mooncake_block", () -> new MooncakeBlock(Properties.of().mapColor(MapColor.WOOD).strength(0.1F).noOcclusion().sound(SoundType.WOOD))
        );
        KONGMING_LANTERN = register(
                "kongming_lantern", () -> new KongmingLanternBlock(Properties.of().mapColor(MapColor.FIRE).strength(0.1F, 0.8F).noOcclusion().sound(SoundType.WOOL))
        );
        BAMBOO_STEAMED_EGG = register(
                "bamboo_steamed_egg",
                () -> StackableFoodBlock.create()
                        .maxCount(4)
                        .item(() -> ModItems.BAMBOO_STEAMED_EGG)
                        .shapes(
                                Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0),
                                Shapes.or(Block.box(7.0, 0.0, 1.0, 15.0, 8.0, 9.0), Block.box(1.0, 0.0, 7.0, 9.0, 8.0, 15.0)),
                                Shapes.or(Block.box(0.0, 0.0, 6.0, 16.0, 8.0, 15.0), Block.box(4.0, 0.0, 0.0, 12.0, 8.0, 15.0)),
                                Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
                        )
                        .build()
        );
        // lazy suppliers break the crop-block <-> seed-item registration cycle
        EGGPLANT_CROP = register("eggplant_crop", () -> new EggplantCropBlock(() -> ModItems.EGGPLANT, () -> ModItems.EGGPLANT_SEED));
    }

    private static FreezerBlock registerFreezer(String name) {
        return register(
                name,
                () -> new FreezerBlock(
                        Properties.of().mapColor(MapColor.METAL).strength(5.0F, 1200.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()
                )
        );
    }

    private static <T extends Block> T register(String name, Supplier<T> block) {
        return Registry.register(BuiltInRegistries.BLOCK, KaleidoscopeChineseFood.id(name), block.get());
    }
}
