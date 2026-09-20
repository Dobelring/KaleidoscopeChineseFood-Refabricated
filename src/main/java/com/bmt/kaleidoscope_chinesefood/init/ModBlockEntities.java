package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.entity.BowlStackBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
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
    public static BlockEntityType<EnchantedPlateBlockEntity> ENCHANTED_PLATE;

    public static void register() {
        // 使用 Fabric API 的 FabricBlockEntityTypeBuilder 注册方块实体
        FREEZER = register("freezer", FabricBlockEntityTypeBuilder.create(FreezerBlockEntity::new, ModBlocks.FREEZER, ModBlocks.FREEZER_GREEN, ModBlocks.FREEZER_ORANGE, ModBlocks.FREEZER_PINK, ModBlocks.FREEZER_LIGHT_BLUE, ModBlocks.FREEZER_YELLOW));
        PICKLE_JAR = register("pickle_jar", FabricBlockEntityTypeBuilder.create(PickleJarBlockEntity::new, ModBlocks.PICKLE_JAR));
        BOWL_STACK = register("bowl_stack", FabricBlockEntityTypeBuilder.create(BowlStackBlockEntity::new, ModBlocks.BOWL_STACK));
        FIRECRACKER = register("firecracker", FabricBlockEntityTypeBuilder.create(FirecrackerBlockEntity::new, ModBlocks.FIRECRACKER));
        COUPLET_BLOCK_ENTITY = register("couplet_block_entity", FabricBlockEntityTypeBuilder.create(CoupletBlockEntity::new, ModBlocks.COUPLET));
        HORIZONTAL_BANNER = register("horizontal_banner", FabricBlockEntityTypeBuilder.create(HorizontalBannerBlockEntity::new, ModBlocks.HORIZONTAL_BANNER));
    }

    /**
     * 附魔金苹果拼盘的方块实体。
     * <p>
     * 必须晚于 {@link ModPlateRegistry#init()} 调用：拼盘方块是本模组在 food phase 自己注册的，
     * 此时才拿得到真实方块引用。26.1.2 的 BlockEntityType 构造器是 private，无法用
     * "子类 + 覆写 isValid" 的方式绕开时序（1.21.1 的写法在此不适用）。
     */
    public static void registerEnchantedPlate() {
        if (ENCHANTED_PLATE != null) {
            return;
        }
        Block plate = BuiltInRegistries.BLOCK.getValue(ModPlateRegistry.ENCHANTED_GOLDEN_APPLE_PLATTER);
        ENCHANTED_PLATE = register("enchanted_plate", FabricBlockEntityTypeBuilder.create(EnchantedPlateBlockEntity::new, plate));
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, FabricBlockEntityTypeBuilder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KaleidoscopeChineseFood.id(name), builder.build());
    }
}
