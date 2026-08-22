package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.block.BowlStackBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.util.SimpleItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BowlStackBlockEntity extends BlockEntity implements Container {
    public static final int MAX_BOWL_COUNT = 3;
    private static final int AUTO_PULL_INTERVAL = 10;
    private int pullCooldown = 0;
    private final SimpleItemHandler itemHandler = new SimpleItemHandler(1) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(Items.BOWL);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 3;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            BowlStackBlockEntity.this.setChanged();
            if (BowlStackBlockEntity.this.level != null && !BowlStackBlockEntity.this.level.isClientSide) {
                BlockState currentState = BowlStackBlockEntity.this.getBlockState();
                int newCount = this.getStackInSlot(0).getCount();
                int currentCount = currentState.getValue(BowlStackBlock.BOWL_COUNT);
                if (newCount != currentCount) {
                    BowlStackBlockEntity.this.level
                            .setBlock(BowlStackBlockEntity.this.worldPosition, currentState.setValue(BowlStackBlock.BOWL_COUNT, newCount), 3);
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
            if (!(belowBE instanceof HopperBlockEntity) && belowBE instanceof Container belowContainer) {
                for (int i = 0; i < belowContainer.getContainerSize(); i++) {
                    ItemStack stackInSlot = belowContainer.getItem(i);
                    if (stackInSlot.is(Items.BOWL)) {
                        if (this.itemHandler.insertItem(0, new ItemStack(Items.BOWL), true).isEmpty()) {
                            ItemStack extracted = belowContainer.removeItem(i, 1);
                            if (!extracted.isEmpty()) {
                                this.itemHandler.insertItem(0, extracted, false);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void initializeFromBlockState(BlockState state) {
        int count = state.getValue(BowlStackBlock.BOWL_COUNT);
        this.itemHandler.setStackInSlot(0, new ItemStack(Items.BOWL, count));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        this.pullCooldown = tag.getInt("PullCooldown");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", this.itemHandler.serializeNBT(registries));
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

    // ----- Container delegation (enables hopper interaction) -----
    @Override
    public int getContainerSize() {
        return this.itemHandler.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.itemHandler.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.itemHandler.getItem(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return this.itemHandler.removeItem(slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return this.itemHandler.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.itemHandler.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.itemHandler.stillValid(player);
    }

    @Override
    public void clearContent() {
        this.itemHandler.clearContent();
    }
}
