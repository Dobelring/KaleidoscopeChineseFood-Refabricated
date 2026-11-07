package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.entity.BowlStackBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntityType;
import com.bmt.kaleidoscope_chinesefood.block.entity.FirecrackerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.FreezerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalScrollBlockEntity;
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
    public static BlockEntityType<CoupletBlockEntity> COUPLET;
    public static BlockEntityType<HorizontalScrollBlockEntity> HORIZONTAL_SCROLL;
    public static BlockEntityType<EnchantedPlateBlockEntity> ENCHANTED_PLATE;

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
        // 1.21.1 官方 1.2.0 改 id:couplet_block_entity→couplet
        COUPLET = register("couplet", Builder.of(CoupletBlockEntity::new, new Block[]{ModBlocks.COUPLET}).build(null));
        // 注:官方 1.21.1 的 1.2.0 中横批 BE id 仍为 "horizontal_banner"(1.2.0 只改了方块 id)
        HORIZONTAL_SCROLL = register(
                "horizontal_banner", Builder.of(HorizontalScrollBlockEntity::new, new Block[]{ModBlocks.HORIZONTAL_SCROLL}).build(null)
        );
        ENCHANTED_PLATE = register("enchanted_plate", new EnchantedPlateBlockEntityType());
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KaleidoscopeChineseFood.id(name), type);
    }
}
