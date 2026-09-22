package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.recipe.ModRecipes;
import com.bmt.kaleidoscope_chinesefood.recipe.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.util.forge.ItemStackHandler;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PickleJarBlockEntity extends BlockEntity {
    public static final int SLOT_LIMIT = 4;
    public static final int TOTAL_SLOTS = 4;
    private boolean hasValidRecipe = false;
    public final ItemStackHandler inventory = new ItemStackHandler(4) {
        protected void onContentsChanged(int slot) {
            PickleJarBlockEntity.this.setChanged();
            if (PickleJarBlockEntity.this.level != null && !PickleJarBlockEntity.this.level.isClientSide) {
                PickleJarBlockEntity.this.level
                    .sendBlockUpdated(PickleJarBlockEntity.this.worldPosition, PickleJarBlockEntity.this.getBlockState(), PickleJarBlockEntity.this.getBlockState(), 3);
                PickleJarBlockEntity.this.level.updateNeighbourForOutputSignal(PickleJarBlockEntity.this.worldPosition, PickleJarBlockEntity.this.getBlockState().getBlock());
                PickleJarBlockEntity.this.checkForValidRecipeAndTryStartFermenting();
            }
        }

        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            return !(Boolean)state.getValue(PickleJarBlock.FERMENTING);
        }

        public int getSlotLimit(int slot) {
            return 4;
        }

        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            if ((Boolean)state.getValue(PickleJarBlock.FERMENTING)) {
                return stack;
            } else if (stack.isEmpty()) {
                return stack;
            } else {
                ItemStack targetSlot = this.getStackInSlot(slot);
                if (!targetSlot.isEmpty()) {
                    if (ItemStack.isSameItemSameTags(targetSlot, stack) && targetSlot.getCount() < 4) {
                        int canAdd = 4 - targetSlot.getCount();
                        int addCount = Math.min(stack.getCount(), canAdd);
                        ItemStack remaining = stack.copy();
                        if (addCount > 0) {
                            if (!simulate) {
                                targetSlot.grow(addCount);
                                this.setStackInSlot(slot, targetSlot);
                            }

                            remaining.shrink(addCount);
                        }

                        return remaining;
                    } else {
                        return stack;
                    }
                } else {
                    ItemStack remaining = stack.copy();

                    for (int i = 0; i < 4; i++) {
                        ItemStack existing = this.getStackInSlot(i);
                        if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, remaining) && existing.getCount() < 4) {
                            int canAdd = 4 - existing.getCount();
                            int addCount = Math.min(remaining.getCount(), canAdd);
                            if (addCount > 0) {
                                if (!simulate) {
                                    existing.grow(addCount);
                                    this.setStackInSlot(i, existing);
                                }

                                remaining.shrink(addCount);
                                if (remaining.isEmpty()) {
                                    break;
                                }
                            }
                        }
                    }

                    if (!remaining.isEmpty()) {
                        int insertCount = Math.min(remaining.getCount(), 4);
                        if (!simulate) {
                            this.setStackInSlot(slot, remaining.copyWithCount(insertCount));
                        }

                        remaining.shrink(insertCount);
                    }

                    return remaining;
                }
            }
        }

        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            if ((Boolean)state.getValue(PickleJarBlock.FERMENTING)) {
                return ItemStack.EMPTY;
            } else {
                ItemStack stack = this.getStackInSlot(slot);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                } else {
                    int extractAmount = Math.min(amount, stack.getCount());
                    ItemStack extractStack = stack.copyWithCount(extractAmount);
                    if (!simulate) {
                        ItemStack remaining = stack.copy();
                        remaining.shrink(extractAmount);
                        this.setStackInSlot(slot, remaining);
                    }

                    return extractStack;
                }
            }
        }
    };
    private int progress = 0;
    private int maxProgress = 0;

    public PickleJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PICKLE_JAR, pos, state);
    }

    public ItemStackHandler getItemHandler() {
        return this.inventory;
    }

    /**
     * 原 Forge 用 BlockEntity#onLoad 作为「实体被加入世界」的钩子，1.20.1 原版没有该方法。
     * 这里用 setLevel 作等价时机：它在 load(NBT) 之后、方块实体进入世界时被调用，
     * 对未经过 NBT 的新放置方块同样会触发。
     */
    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (this.level != null && !this.level.isClientSide) {
            this.checkForValidRecipe();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PickleJarBlockEntity be) {
        if (!level.isClientSide) {
            if (!(Boolean)state.getValue(PickleJarBlock.OPEN) && (Boolean)state.getValue(PickleJarBlock.FERMENTING)) {
                be.progress++;
                if (be.progress >= be.maxProgress) {
                    be.finishFermenting(state, level, pos);
                }

                if (be.progress % 20 == 0) {
                    setChanged(level, pos, state);
                    level.sendBlockUpdated(pos, state, state, 3);
                }
            }
        }
    }

    public void checkForValidRecipe() {
        if (this.level != null && !this.level.isClientSide) {
            boolean oldHasValidRecipe = this.hasValidRecipe;
            this.hasValidRecipe = this.getCurrentRecipe().isPresent();
            if (oldHasValidRecipe != this.hasValidRecipe) {
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    public void checkForValidRecipeAndTryStartFermenting() {
        if (this.level != null && !this.level.isClientSide) {
            this.checkForValidRecipe();
            BlockState state = this.getBlockState();
            if (!(Boolean)state.getValue(PickleJarBlock.OPEN) && !(Boolean)state.getValue(PickleJarBlock.FERMENTING) && this.hasValidRecipe) {
                this.getCurrentRecipe()
                    .ifPresent(
                        recipe -> {
                            this.maxProgress = recipe.getFermentTime();
                            this.progress = 0;
                            this.level
                                .setBlock(
                                    this.worldPosition, (BlockState)((BlockState)state.setValue(PickleJarBlock.FERMENTING, true)).setValue(PickleJarBlock.DONE, false), 3
                                );
                            this.setChanged();
                            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
                        }
                    );
            }
        }
    }

    private Optional<PickleJarRecipe> getCurrentRecipe() {
        if (this.level == null) {
            return Optional.empty();
        } else {
            SimpleContainer container = new SimpleContainer(4);

            for (int i = 0; i < 4; i++) {
                container.setItem(i, this.inventory.getStackInSlot(i));
            }

            return this.level.getRecipeManager().getRecipeFor(ModRecipes.PICKLE_JAR_TYPE, container, this.level);
        }
    }

    public boolean tryStartFermenting() {
        return this.hasValidRecipe ? this.getCurrentRecipe().map(recipe -> {
            this.maxProgress = recipe.getFermentTime();
            this.progress = 0;
            this.setChanged();
            return true;
        }).orElse(false) : false;
    }

    private void finishFermenting(BlockState state, Level level, BlockPos pos) {
        this.getCurrentRecipe().ifPresent(recipe -> {
            for (int i = 0; i < 4; i++) {
                this.inventory.setStackInSlot(i, ItemStack.EMPTY);
            }

            ItemStack baseResult = recipe.getResultItem(level.registryAccess()).copy();
            int totalOutput = Math.min(baseResult.getCount() * 4, 16);
            int remaining = totalOutput;

            for (int slot = 0; remaining > 0 && slot < 4; slot++) {
                int count = Math.min(remaining, 4);
                this.inventory.setStackInSlot(slot, baseResult.copyWithCount(count));
                remaining -= count;
            }

            level.setBlock(pos, (BlockState)((BlockState)state.setValue(PickleJarBlock.FERMENTING, false)).setValue(PickleJarBlock.DONE, true), 3);
            this.checkForValidRecipeAndTryStartFermenting();
        });
    }

    public void resetProgress() {
        this.progress = this.maxProgress = 0;
        this.setChanged();
    }

    public void insertItem(ItemStack stack, Player player) {
        if (!stack.isEmpty()) {
            ItemStack remaining = stack.copy();
            int totalInserted = 0;

            for (int i = 0; i < 4 && totalInserted < 4; i++) {
                ItemStack existing = this.inventory.getStackInSlot(i);
                if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, stack) && existing.getCount() < 4) {
                    int canAdd = 4 - existing.getCount();
                    int addCount = Math.min(Math.min(stack.getCount(), 4 - totalInserted), canAdd);
                    if (addCount > 0) {
                        existing.grow(addCount);
                        this.inventory.setStackInSlot(i, existing);
                        totalInserted += addCount;
                    }
                }
            }

            if (totalInserted > 0) {
                if (!player.isCreative()) {
                    stack.shrink(totalInserted);
                }
            } else {
                for (int ix = 0; ix < 4; ix++) {
                    if (this.inventory.getStackInSlot(ix).isEmpty()) {
                        int insertCount = Math.min(stack.getCount(), 4);
                        this.inventory.setStackInSlot(ix, stack.copyWithCount(insertCount));
                        if (!player.isCreative()) {
                            stack.shrink(insertCount);
                        }

                        return;
                    }
                }
            }
        }
    }

    public void extractItem(Player player) {
        for (int i = 3; i >= 0; i--) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                ItemStack giveStack = stack.copy();
                if (!player.addItem(giveStack)) {
                    this.inventory.setStackInSlot(i, giveStack);
                } else {
                    this.inventory.setStackInSlot(i, ItemStack.EMPTY);
                }

                return;
            }
        }
    }

    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", this.inventory.serializeNBT());
        tag.putInt("Progress", this.progress);
        tag.putInt("MaxProgress", this.maxProgress);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.inventory.deserializeNBT(tag.getCompound("Inventory"));
        this.progress = tag.getInt("Progress");
        this.maxProgress = tag.getInt("MaxProgress");
        if (this.level != null && !this.level.isClientSide) {
            this.checkForValidRecipe();
        }
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public boolean hasValidRecipe() {
        return this.hasValidRecipe;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.setChanged();
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }
}
