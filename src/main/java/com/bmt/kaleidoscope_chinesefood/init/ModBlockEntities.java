package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.entity.BowlStackBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.FirecrackerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.FreezerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalBannerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

public class ModBlockEntities {
    public static BlockEntityType<FreezerBlockEntity> FREEZER;
    public static BlockEntityType<PickleJarBlockEntity> PICKLE_JAR;
    public static BlockEntityType<BowlStackBlockEntity> BOWL_STACK;
    public static BlockEntityType<FirecrackerBlockEntity> FIRECRACKER;
    public static BlockEntityType<CoupletBlockEntity> COUPLET_BLOCK_ENTITY;
    public static BlockEntityType<HorizontalBannerBlockEntity> HORIZONTAL_BANNER;

    public static void register() {
        FREEZER = register(
                "freezer",
                Builder.of(
                                FreezerBlockEntity::new,
                                new Block[]{
                                        ModBlocks.FREEZER,
                                        ModBlocks.FREEZER_GREEN,
                                        ModBlocks.FREEZER_ORANGE,
                                        ModBlocks.FREEZER_PINK,
                                        ModBlocks.FREEZER_LIGHT_BLUE,
                                        ModBlocks.FREEZER_YELLOW
                                }
                        )
                        .build(null)
        );
        PICKLE_JAR = register("pickle_jar", Builder.of(PickleJarBlockEntity::new, new Block[]{ModBlocks.PICKLE_JAR}).build(null));
        BOWL_STACK = register("bowl_stack", Builder.of(BowlStackBlockEntity::new, new Block[]{ModBlocks.BOWL_STACK}).build(null));
        FIRECRACKER = register("firecracker", Builder.of(FirecrackerBlockEntity::new, new Block[]{ModBlocks.FIRECRACKER}).build(null));
        COUPLET_BLOCK_ENTITY = register(
                "couplet_block_entity", Builder.of(CoupletBlockEntity::new, new Block[]{ModBlocks.COUPLET_BLOCK}).build(null)
        );
        HORIZONTAL_BANNER = register(
                "horizontal_banner", Builder.of(HorizontalBannerBlockEntity::new, new Block[]{ModBlocks.HORIZONTAL_BANNER}).build(null)
        );
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KaleidoscopeChineseFood.id(name), type);
    }
}
