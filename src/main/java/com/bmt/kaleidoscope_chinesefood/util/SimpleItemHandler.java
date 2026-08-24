package com.bmt.kaleidoscope_chinesefood.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Minimal Fabric replacement for NeoForge's {@code ItemStackHandler}.
 * Implements {@link Container} so hoppers can interact with the owning block entity.
 */
public class SimpleItemHandler implements Container {
    private final NonNullList<ItemStack> stacks;
    private final int size;

    public SimpleItemHandler(int size) {
        this.size = size;
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    // ----- Container -----
    @Override
    public int getContainerSize() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.stacks.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack result = ContainerHelper.removeItem(this.stacks, slot, amount);
        if (!result.isEmpty()) {
            this.onContentsChanged(slot);
        }
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack taken = ContainerHelper.takeItem(this.stacks, slot);
        if (!taken.isEmpty()) {
            this.onContentsChanged(slot);
        }
        return taken;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.stacks.set(slot, stack);
        this.onContentsChanged(slot);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        // NonNullList.withSize 是固定长度列表，clear() 会直接抛 UnsupportedOperationException；
        // 逐槽置空并走 setItem 以触发 onContentsChanged 通知
        for (int i = 0; i < this.size; i++) {
            this.setItem(i, ItemStack.EMPTY);
        }
    }

    // ----- ItemStackHandler-like API -----
    public int getSlots() {
        return this.getContainerSize();
    }

    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.getItem(slot);
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.setItem(slot, stack);
    }

    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !this.isItemValid(slot, stack)) {
            return stack;
        }
        ItemStack existing = this.stacks.get(slot);
        int limit = this.getSlotLimit(slot);
        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(existing, stack)) {
                return stack;
            }
            int space = limit - existing.getCount();
            if (space <= 0) {
                return stack;
            }
            int add = Math.min(space, stack.getCount());
            if (!simulate) {
                existing.grow(add);
                this.onContentsChanged(slot);
            }
            ItemStack remaining = stack.copy();
            remaining.shrink(add);
            return remaining;
        } else {
            int add = Math.min(limit, stack.getCount());
            if (!simulate) {
                this.stacks.set(slot, stack.copyWithCount(add));
                this.onContentsChanged(slot);
            }
            ItemStack remaining = stack.copy();
            remaining.shrink(add);
            return remaining;
        }
    }

    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack existing = this.stacks.get(slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int take = Math.min(amount, existing.getCount());
        ItemStack result = existing.copyWithCount(take);
        if (!simulate) {
            ItemStack remaining = existing.copy();
            remaining.shrink(take);
            this.stacks.set(slot, remaining);
            this.onContentsChanged(slot);
        }
        return result;
    }

    public int getSlotLimit(int slot) {
        return 64;
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    protected void onContentsChanged(int slot) {
    }

    // ----- NBT -----
    // 注意：不能用 ContainerHelper.saveAllItems/loadAllItems 的稀疏格式做同步！
    // 稀疏格式跳过空槽位，而加载端只覆盖出现的槽位——取出物品后客户端会永久残留旧内容（幽灵渲染）。
    // 这里改为密集格式：每个槽位都写条目；加载前先全量清空。对旧存档的稀疏标签同样兼容。
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Size", this.size);
        net.minecraft.nbt.ListTag items = new net.minecraft.nbt.ListTag();
        for (int i = 0; i < this.size; i++) {
            CompoundTag entry = new CompoundTag();
            entry.putByte("Slot", (byte) i);
            ItemStack stack = this.stacks.get(i);
            // 注意：save() 返回编码后的新标签（prefix 合并结果），必须用返回值，不能复用原 entry！
            items.add(stack.isEmpty() ? entry : stack.save(registries, entry));
        }
        tag.put("Items", items);
        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag tag) {
        for (int i = 0; i < this.size; i++) {
            this.stacks.set(i, ItemStack.EMPTY);
        }
        net.minecraft.nbt.ListTag items = tag.getList("Items", net.minecraft.nbt.Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag entry = items.getCompound(i);
            int slot = entry.getByte("Slot") & 255;
            if (slot < this.size) {
                this.stacks.set(slot, ItemStack.parse(registries, entry).orElse(ItemStack.EMPTY));
            }
        }
    }
}
