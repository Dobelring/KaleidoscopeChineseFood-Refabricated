package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.entity.BowlStackBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.FirecrackerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.FreezerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalBannerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

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
                FabricBlockEntityTypeBuilder.create(
                                FreezerBlockEntity::new,
                                new Block[]{
                                        ModBlocks.FREEZER,
                                        ModBlocks.FREEZER_GREEN,
                                        ModBlocks.FREEZER_ORANGE,
                                        ModBlocks.FREEZER_LIGHT_GRAY,
                                        ModBlocks.FREEZER_PINK,
                                        ModBlocks.FREEZER_LIGHT_BLUE,
                                        ModBlocks.FREEZER_YELLOW
                                }
                        )
                        .build()
        );
        PICKLE_JAR = register("pickle_jar", FabricBlockEntityTypeBuilder.create(PickleJarBlockEntity::new, new Block[]{ModBlocks.PICKLE_JAR}).build());
        BOWL_STACK = register("bowl_stack", FabricBlockEntityTypeBuilder.create(BowlStackBlockEntity::new, new Block[]{ModBlocks.BOWL_STACK}).build());
        FIRECRACKER = register("firecracker", FabricBlockEntityTypeBuilder.create(FirecrackerBlockEntity::new, new Block[]{ModBlocks.FIRECRACKER}).build());
        COUPLET_BLOCK_ENTITY = register(
                "couplet_block_entity", FabricBlockEntityTypeBuilder.create(CoupletBlockEntity::new, new Block[]{ModBlocks.COUPLET_BLOCK}).build()
        );
        HORIZONTAL_BANNER = register(
                "horizontal_banner", FabricBlockEntityTypeBuilder.create(HorizontalBannerBlockEntity::new, new Block[]{ModBlocks.HORIZONTAL_BANNER}).build()
        );
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KaleidoscopeChineseFood.id(name), type);
    }
}
