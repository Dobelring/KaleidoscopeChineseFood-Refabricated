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
        // 使用 Fabric API 的 FabricBlockEntityTypeBuilder 注册方块实体
        FREEZER = register("freezer", FabricBlockEntityTypeBuilder.create(FreezerBlockEntity::new, ModBlocks.FREEZER, ModBlocks.FREEZER_GREEN, ModBlocks.FREEZER_ORANGE, ModBlocks.FREEZER_PINK, ModBlocks.FREEZER_LIGHT_BLUE, ModBlocks.FREEZER_YELLOW));
        PICKLE_JAR = register("pickle_jar", FabricBlockEntityTypeBuilder.create(PickleJarBlockEntity::new, ModBlocks.PICKLE_JAR));
        BOWL_STACK = register("bowl_stack", FabricBlockEntityTypeBuilder.create(BowlStackBlockEntity::new, ModBlocks.BOWL_STACK));
        FIRECRACKER = register("firecracker", FabricBlockEntityTypeBuilder.create(FirecrackerBlockEntity::new, ModBlocks.FIRECRACKER));
        COUPLET_BLOCK_ENTITY = register("couplet_block_entity", FabricBlockEntityTypeBuilder.create(CoupletBlockEntity::new, ModBlocks.COUPLET_BLOCK));
        HORIZONTAL_BANNER = register("horizontal_banner", FabricBlockEntityTypeBuilder.create(HorizontalBannerBlockEntity::new, ModBlocks.HORIZONTAL_BANNER));
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, FabricBlockEntityTypeBuilder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KaleidoscopeChineseFood.id(name), builder.build());
    }
}
