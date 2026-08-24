package com.bmt.kaleidoscope_chinesefood.util;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
        return ContainerHelper.takeItem(this.stacks, slot);
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
        // NonNullList.clear() 会抛 UnsupportedOperationException，必须逐槽置空（1.21.1 移植交接教训）
        for (int i = 0; i < this.size; i++) {
            this.stacks.set(i, ItemStack.EMPTY);
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
    // 必须密集序列化（1.21.1 移植交接教训）：原版 ContainerHelper.saveAllItems 只写非空槽位，
    // 而 loadAllItems 只覆盖"出现的槽位"、不清空其余槽位。Fabric 的 BE 数据同步走
    // getUpdateTag/loadAdditional 路径，罐子被取空后同步包不含任何条目，客户端旧内容残留 →
    // "取出后幽灵渲染"。这里改为所有槽位都写条目：非空槽位用 ItemStack.MAP_CODEC 内联
    // id/count/components（与旧稀疏存档格式读取兼容），空槽位仅写 Slot 索引。
    public void serializeNBT(ValueOutput output) {
        ValueOutput.ValueOutputList list = output.childrenList("Items");
        for (int i = 0; i < this.size; i++) {
            ItemStack stack = this.stacks.get(i);
            ValueOutput entry = list.addChild();
            entry.putByte("Slot", (byte) i);
            if (!stack.isEmpty()) {
                entry.store(ItemStack.MAP_CODEC, stack);
            }
        }
    }

    public void deserializeNBT(ValueInput input) {
        // 加载前先全部清空：即使遇到旧存档的稀疏格式（只有非空槽位）也能正确还原，
        // 空堆栈不能直接交给 MAP_CODEC 编解码（会抛异常/得空结果），已由 isEmpty 分支规避
        for (int i = 0; i < this.size; i++) {
            this.stacks.set(i, ItemStack.EMPTY);
        }
        for (ValueInput entry : input.childrenListOrEmpty("Items")) {
            int slot = entry.getByteOr("Slot", (byte) -1);
            if (slot < 0) {
                slot = entry.getIntOr("Slot", -1);
            }
            if (slot >= 0 && slot < this.size) {
                this.stacks.set(slot, entry.read(ItemStack.MAP_CODEC).orElse(ItemStack.EMPTY));
            }
        }
    }
}
