package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.block.BowlStackBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.util.forge.IItemHandler;
import com.bmt.kaleidoscope_chinesefood.util.forge.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class BowlStackBlockEntity extends BlockEntity {
    public static final int MAX_BOWL_COUNT = 3;
    private static final int AUTO_PULL_INTERVAL = 10;
    private int pullCooldown = 0;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(Items.BOWL);
        }

        public int getSlotLimit(int slot) {
            return 3;
        }

        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            BowlStackBlockEntity.this.setChanged();
            if (BowlStackBlockEntity.this.level != null && !BowlStackBlockEntity.this.level.isClientSide) {
                BlockState currentState = BowlStackBlockEntity.this.level.getBlockState(BowlStackBlockEntity.this.worldPosition);
                int newCount = this.getStackInSlot(0).getCount();
                int currentCount = (Integer)currentState.getValue(BowlStackBlock.BOWL_COUNT);
                if (newCount != currentCount) {
                    BowlStackBlockEntity.this.level
                        .setBlock(BowlStackBlockEntity.this.worldPosition, (BlockState)currentState.setValue(BowlStackBlock.BOWL_COUNT, newCount), 3);
                }
            }
        }
    };

    public BowlStackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOWL_STACK, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BowlStackBlockEntity be) {
        if (!level.isClientSide) {
            if (be.pullCooldown > 0) {
                be.pullCooldown--;
            } else {
                be.tryPullBowlFromBelow();
                be.pullCooldown = AUTO_PULL_INTERVAL;
            }
        }
    }

    private void tryPullBowlFromBelow() {
        if (this.getBowlCount() < MAX_BOWL_COUNT) {
            BlockPos belowPos = this.worldPosition.below();
            BlockEntity belowBE = this.level.getBlockEntity(belowPos);
            if (belowBE != null && !(belowBE instanceof HopperBlockEntity)) {
                // Fabric：无 Forge 能力系统。本模组方块实体直接读取暴露的 ItemStackHandler，
                // 其余（原版容器与已注册 storage 的模组方块）改用 Fabric 传输 API 查找
                if (belowBE instanceof PickleJarBlockEntity pickleJar) {
                    this.pullBowlFromHandler(pickleJar.getItemHandler());
                } else {
                    this.pullBowlFromStorage(ItemStorage.SIDED.find(this.level, belowPos, Direction.UP));
                }
            }
        }
    }

    private void pullBowlFromHandler(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stackInSlot = handler.getStackInSlot(i);
            if (stackInSlot.is(Items.BOWL) && handler.extractItem(i, 1, true).is(Items.BOWL)) {
                ItemStack remaining = this.itemHandler.insertItem(0, new ItemStack(Items.BOWL), false);
                if (remaining.isEmpty()) {
                    handler.extractItem(i, 1, false);
                    this.setChanged();
                    break;
                }
            }
        }
    }

    private void pullBowlFromStorage(@Nullable Storage<ItemVariant> storage) {
        if (storage == null) {
            return;
        }

        for (StorageView<ItemVariant> view : storage) {
            ItemVariant resource = view.getResource();
            if (!resource.isOf(Items.BOWL) || view.getAmount() < 1) {
                continue;
            }

            if (!this.itemHandler.insertItem(0, new ItemStack(Items.BOWL), true).isEmpty()) {
                continue;
            }

            try (Transaction transaction = Transaction.openOuter()) {
                if (storage.extract(resource, 1, transaction) == 1) {
                    transaction.commit();
                    this.itemHandler.insertItem(0, new ItemStack(Items.BOWL), false);
                    this.setChanged();
                    break;
                }
            }
        }
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    public void initializeFromBlockState(BlockState state) {
        int count = (Integer)state.getValue(BowlStackBlock.BOWL_COUNT);
        this.itemHandler.setStackInSlot(0, new ItemStack(Items.BOWL, count));
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        this.pullCooldown = tag.getInt("PullCooldown");
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", this.itemHandler.serializeNBT());
        tag.putInt("PullCooldown", this.pullCooldown);
    }

    public int getBowlCount() {
        return this.itemHandler.getStackInSlot(0).getCount();
    }

    public boolean addBowl() {
        return this.itemHandler.insertItem(0, new ItemStack(Items.BOWL), false).isEmpty();
    }

    public ItemStack removeBowl() {
        return this.itemHandler.extractItem(0, 1, false);
    }
}
