package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.block.entity.BowlStackBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.FirecrackerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.FreezerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalBannerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.bmt.kaleidoscope_chinesefood.util.forge.ItemStackHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;

@SuppressWarnings("UnstableApiUsage")
public class ModBlockEntities {
    public static final BlockEntityType<FreezerBlockEntity> FREEZER = Builder.of(
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
        .build(null);
    public static final BlockEntityType<PickleJarBlockEntity> PICKLE_JAR = Builder.of(PickleJarBlockEntity::new, new Block[]{ModBlocks.PICKLE_JAR}).build(null);
    public static final BlockEntityType<BowlStackBlockEntity> BOWL_STACK = Builder.of(BowlStackBlockEntity::new, new Block[]{ModBlocks.BOWL_STACK}).build(null);
    public static final BlockEntityType<CoupletBlockEntity> COUPLET_BLOCK_ENTITY = Builder.of(CoupletBlockEntity::new, new Block[]{ModBlocks.COUPLET}).build(null);
    public static final BlockEntityType<HorizontalBannerBlockEntity> HORIZONTAL_BANNER = Builder.of(
        HorizontalBannerBlockEntity::new, new Block[]{ModBlocks.HORIZONTAL_BANNER}
    ).build(null);
    public static final BlockEntityType<FirecrackerBlockEntity> FIRECRACKER = Builder.of(FirecrackerBlockEntity::new, new Block[]{ModBlocks.FIRECRACKER}).build(null);
    // 注意：本字段在类初始化时就会从方块注册表里取「金苹果盘」方块，
    // 因此主类必须先执行 ModPlateRegistry.init() 并让盘装方块完成注册，再触碰 ModBlockEntities
    public static final BlockEntityType<EnchantedPlateBlockEntity> ENCHANTED_PLATE = Builder.of(
        EnchantedPlateBlockEntity::new, new Block[]{BuiltInRegistries.BLOCK.get(ModPlateRegistry.ENCHANTED_GOLDEN_APPLE_PLATTER)}
    ).build(null);

    public ModBlockEntities() {
    }

    public static void registerBlockEntities() {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "freezer"), FREEZER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "pickle_jar"), PICKLE_JAR);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "bowl_stack"), BOWL_STACK);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "couplet"), COUPLET_BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "horizontal_banner"), HORIZONTAL_BANNER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "firecracker"), FIRECRACKER);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation("kaleidoscope_chinesefood", "enchanted_plate"), ENCHANTED_PLATE);

        // Fabric：没有 Forge 能力系统，改用传输 API 暴露这两个方块实体的 ItemStackHandler，
        // 使漏斗/物流模组仍能像 Forge 版一样与它们交互
        ItemStorage.SIDED.registerForBlockEntity((be, side) -> new ItemHandlerStorage(be.getItemHandler()), PICKLE_JAR);
        ItemStorage.SIDED.registerForBlockEntity((be, side) -> new ItemHandlerStorage(be.getItemHandler()), BOWL_STACK);
    }

    /**
     * Fabric：把 {@link ItemStackHandler} 适配成传输 API 的 {@link Storage}。
     * 每个槽位是一个 {@link SingleStackStorage}（自带事务快照与回滚），容器层把
     * insert/extract 依次转发给各槽位。
     */
    private static class ItemHandlerStorage implements Storage<ItemVariant> {
        private final List<HandlerSlotStorage> slots = new ArrayList<>();

        private ItemHandlerStorage(ItemStackHandler handler) {
            for (int i = 0; i < handler.getSlots(); i++) {
                this.slots.add(new HandlerSlotStorage(handler, i));
            }
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            long inserted = 0L;
            for (HandlerSlotStorage slot : this.slots) {
                if (inserted >= maxAmount) {
                    break;
                }
                inserted += slot.insert(resource, maxAmount - inserted, transaction);
            }
            return inserted;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            long extracted = 0L;
            for (HandlerSlotStorage slot : this.slots) {
                if (extracted >= maxAmount) {
                    break;
                }
                extracted += slot.extract(resource, maxAmount - extracted, transaction);
            }
            return extracted;
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator() {
            return Collections.<StorageView<ItemVariant>>unmodifiableList(this.slots).iterator();
        }
    }

    private static class HandlerSlotStorage extends SingleStackStorage {
        private final ItemStackHandler handler;
        private final int slot;

        private HandlerSlotStorage(ItemStackHandler handler, int slot) {
            this.handler = handler;
            this.slot = slot;
        }

        @Override
        protected ItemStack getStack() {
            return this.handler.getStackInSlot(this.slot);
        }

        @Override
        protected void setStack(ItemStack stack) {
            this.handler.setStackInSlot(this.slot, stack);
        }

        @Override
        protected int getCapacity(ItemVariant variant) {
            return this.handler.getSlotLimit(this.slot);
        }

        @Override
        protected boolean canInsert(ItemVariant variant) {
            return this.handler.isItemValid(this.slot, variant.toStack());
        }

        @Override
        protected boolean canExtract(ItemVariant variant) {
            return true;
        }
    }
}
