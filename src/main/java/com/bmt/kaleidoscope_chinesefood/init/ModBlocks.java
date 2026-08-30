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
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {
    public static CornBlock CORN_RISTRA;
    public static FreezerBlock FREEZER;
    public static FreezerBlock FREEZER_GREEN;
    public static FreezerBlock FREEZER_ORANGE;
    public static FreezerBlock FREEZER_LIGHT_GRAY;
    public static FreezerBlock FREEZER_PINK;
    public static FreezerBlock FREEZER_LIGHT_BLUE;
    public static FreezerBlock FREEZER_YELLOW;
    public static PickleJarBlock PICKLE_JAR;
    public static BowlStackBlock BOWL_STACK;
    public static SaltBlock SALT_BLOCK;
    public static FirecrackerBlock FIRECRACKER;
    public static FuCharacterBlock FU_CHARACTER;
    public static CoupletBlock COUPLET_BLOCK;
    public static HorizontalBannerBlock HORIZONTAL_BANNER;
    public static MooncakeBlock MOONCAKE_BLOCK;
    public static KongmingLanternBlock KONGMING_LANTERN;
    public static EggplantCropBlock EGGPLANT_CROP;

    public static void register() {
        // 1.21.1 移植回归修复：原版类内部属性（GRASS 音效等）随外部化 Properties 一并丢失
        CORN_RISTRA = register(
                "corn_ristra",
                p -> new CornBlock(
                        p.mapColor(MapColor.COLOR_BROWN).noCollision().instabreak().sound(SoundType.GRASS)
                                .pushReaction(PushReaction.DESTROY)
                )
        );
        FREEZER = registerFreezer("freezer");
        FREEZER_GREEN = registerFreezer("freezer_green");
        FREEZER_ORANGE = registerFreezer("freezer_orange");
        FREEZER_LIGHT_GRAY = registerFreezer("freezer_light_gray");
        FREEZER_PINK = registerFreezer("freezer_pink");
        FREEZER_LIGHT_BLUE = registerFreezer("freezer_light_blue");
        FREEZER_YELLOW = registerFreezer("freezer_yellow");
        PICKLE_JAR = register(
                "pickle_jar",
                p -> new PickleJarBlock(p.mapColor(MapColor.TERRACOTTA_RED).strength(1.5F, 2.0F).sound(SoundType.DECORATED_POT).noOcclusion())
        );
        BOWL_STACK = register(
                "bowl_stack", p -> new BowlStackBlock(p.mapColor(MapColor.WOOD).strength(1.0F, 2.0F).sound(SoundType.WOOD).noOcclusion())
        );
        SALT_BLOCK = register(
                "salt_block", p -> new SaltBlock(p.mapColor(MapColor.TERRACOTTA_WHITE).strength(0.1F).noOcclusion().sound(SoundType.GLASS))
        );
        FIRECRACKER = register(
                "firecracker", p -> new FirecrackerBlock(p.mapColor(MapColor.FIRE).strength(0.0F).sound(SoundType.CANDLE).noOcclusion())
        );
        FU_CHARACTER = register(
                "fu_character", p -> new FuCharacterBlock(p.mapColor(MapColor.FIRE).strength(0.0F).noOcclusion().sound(SoundType.WOOL))
        );
        COUPLET_BLOCK = register(
                "couplet_block", p -> new CoupletBlock(p.mapColor(MapColor.FIRE).strength(0.0F).noOcclusion().sound(SoundType.WOOL))
        );
        HORIZONTAL_BANNER = register(
                "horizontal_banner", p -> new HorizontalBannerBlock(p.mapColor(MapColor.FIRE).strength(0.0F).noOcclusion().sound(SoundType.WOOL))
        );
        MOONCAKE_BLOCK = register(
                "mooncake_block", p -> new MooncakeBlock(p.instabreak().strength(0.1F).noOcclusion().sound(SoundType.WOOD))
        );
        KONGMING_LANTERN = register(
                "kongming_lantern", p -> new KongmingLanternBlock(p.instabreak().strength(0.1F).noOcclusion().sound(SoundType.WOOD))
        );
        // 1.21.1 移植回归修复：对齐 cookery cropReg 的作物属性；缺 noCollission 时作物是实心方块，
        // 会导致 FarmBlock.canSurvive 失败→耕地退化为泥土→作物被顶掉（放水也救不回来）
        EGGPLANT_CROP = register(
                "eggplant_crop",
                p -> new EggplantCropBlock(
                        p.mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak()
                                .sound(SoundType.CROP).pushReaction(PushReaction.DESTROY),
                        () -> ModItems.EGGPLANT,
                        () -> ModItems.EGGPLANT_SEED
                )
        );
    }

    private static FreezerBlock registerFreezer(String name) {
        return register(
                name,
                p -> new FreezerBlock(
                        p.mapColor(MapColor.METAL).strength(5.0F, 1200.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()
                )
        );
    }

    private static <T extends Block> T register(String name, Function<Properties, T> factory) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, KaleidoscopeChineseFood.id(name));
        T block = factory.apply(Properties.of().setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
}
