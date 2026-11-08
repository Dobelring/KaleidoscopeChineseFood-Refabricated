package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.api.IPickleJar;
import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.util.forge.ItemStackHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 1.2.0：存取逻辑重做——黑名单 tag、容器剩余物判定（仅玻璃瓶）、
 * 取出需手持对应容器（need_container 提示）、背包空间预检查。
 */
public class PickleJarBlockEntity extends BlockEntity implements IPickleJar {
    public static final int SLOT_LIMIT = 4;
    public static final int TOTAL_SLOTS = 4;
    public static final TagKey<Item> PICKLE_JAR_BLACKLIST = TagKey.create(Registries.ITEM, KaleidoscopeChineseFood.id("pickle_jar_blacklist"));
    private boolean hasValidRecipe = false;
    public final ItemStackHandler inventory = new ItemStackHandler(4) {
        protected void onContentsChanged(int slot) {
            PickleJarBlockEntity.this.refresh();
            if (PickleJarBlockEntity.this.level != null && !PickleJarBlockEntity.this.level.isClientSide) {
                PickleJarBlockEntity.this.level.updateNeighbourForOutputSignal(
                    PickleJarBlockEntity.this.worldPosition, PickleJarBlockEntity.this.getBlockState().getBlock());
                PickleJarBlockEntity.this.checkForValidRecipeAndTryStartFermenting();
            }
        }

        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            return !(Boolean)state.getValue(PickleJarBlock.FERMENTING)
                && !stack.is(PICKLE_JAR_BLACKLIST)
                && ItemUtils.getContainerItem(stack) == Items.AIR;
        }

        public int getSlotLimit(int slot) {
            return 4;
        }

        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            if ((Boolean)state.getValue(PickleJarBlock.FERMENTING)) {
                return stack;
            }
            if (stack.isEmpty() || stack.is(PICKLE_JAR_BLACKLIST) || ItemUtils.getContainerItem(stack) != Items.AIR) {
                return stack;
            }
            // 1.1.16：每槽上限从固定 4 改为 min(4, 物品最大堆叠)
            int slotLimit = maxInSlot(stack);
            ItemStack targetSlot = this.getStackInSlot(slot);
            if (!targetSlot.isEmpty()) {
                if (ItemStack.isSameItemSameTags(targetSlot, stack) && targetSlot.getCount() < slotLimit) {
                    int canAdd = slotLimit - targetSlot.getCount();
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
                    if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, remaining) && existing.getCount() < slotLimit) {
                        int canAdd = slotLimit - existing.getCount();
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
                    int insertCount = Math.min(remaining.getCount(), slotLimit);
                    if (!simulate) {
                        this.setStackInSlot(slot, remaining.copyWithCount(insertCount));
                    }
                    remaining.shrink(insertCount);
                }
                return remaining;
            }
        }

        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            if ((Boolean)state.getValue(PickleJarBlock.FERMENTING)) {
                return ItemStack.EMPTY;
            }
            ItemStack stack = this.getStackInSlot(slot);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            // 1.2.0：容器剩余物不是玻璃瓶的产物不允许漏斗抽走
            if (ItemUtils.getContainerItem(stack) != Items.AIR) {
                return ItemStack.EMPTY;
            }
            int extractAmount = Math.min(amount, stack.getCount());
            ItemStack extractStack = stack.copyWithCount(extractAmount);
            if (!simulate) {
                ItemStack remaining = stack.copy();
                remaining.shrink(extractAmount);
                this.setStackInSlot(slot, remaining);
            }
            return extractStack;
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

    /** 1.2.0：统一刷新入口（对齐官方 refresh）。 */
    public void refresh() {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
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
        if (level.isClientSide) {
            return;
        }
        if (!(Boolean)state.getValue(PickleJarBlock.OPEN) && (Boolean)state.getValue(PickleJarBlock.FERMENTING)) {
            be.progress++;
            if (be.progress >= be.maxProgress) {
                be.finishFermenting(state, level, pos);
                return;
            }
            if (be.progress % 20 == 0) {
                be.refresh();
            }
        }
    }

    public void checkForValidRecipe() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        boolean oldHasValidRecipe = this.hasValidRecipe;
        this.hasValidRecipe = this.getCurrentRecipe().isPresent();
        if (oldHasValidRecipe != this.hasValidRecipe) {
            this.refresh();
        }
    }

    public void checkForValidRecipeAndTryStartFermenting() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        this.checkForValidRecipe();
        BlockState state = this.getBlockState();
        if (!(Boolean)state.getValue(PickleJarBlock.OPEN) && !(Boolean)state.getValue(PickleJarBlock.FERMENTING) && this.hasValidRecipe) {
            this.getCurrentRecipe().ifPresent(recipe -> {
                this.maxProgress = recipe.getFermentTime();
                this.progress = 0;
                this.level.setBlock(
                    this.worldPosition,
                    (BlockState)((BlockState)state.setValue(PickleJarBlock.FERMENTING, true)).setValue(PickleJarBlock.DONE, false),
                    3
                );
                this.refresh();
            });
        }
    }

    private Optional<PickleJarRecipe> getCurrentRecipe() {
        if (this.level == null) {
            return Optional.empty();
        }
        SimpleContainer container = new SimpleContainer(4);
        for (int i = 0; i < 4; i++) {
            container.setItem(i, this.inventory.getStackInSlot(i));
        }
        return this.level.getRecipeManager().getRecipeFor(ModRecipes.PICKLE_JAR_TYPE, container, this.level);
    }

    @Override
    public boolean tryStartFermenting(Level level) {
        if (!this.hasValidRecipe) {
            return false;
        }
        return this.getCurrentRecipe().map(recipe -> {
            this.maxProgress = recipe.getFermentTime();
            this.progress = 0;
            this.setChanged();
            return true;
        }).orElse(false);
    }

    private void finishFermenting(BlockState state, Level level, BlockPos pos) {
        Optional<PickleJarRecipe> recipe = this.getCurrentRecipe();
        this.progress = 0;
        this.maxProgress = 0;
        if (recipe.isPresent()) {
            PickleJarRecipe r = recipe.get();
            for (int i = 0; i < 4; i++) {
                this.inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
            ItemStack baseResult = r.getResultItem(level.registryAccess()).copy();
            int totalOutput = Math.min(baseResult.getCount() * 4, 16);
            int remaining = totalOutput;
            for (int slot = 0; remaining > 0 && slot < 4; slot++) {
                int count = Math.min(remaining, 4);
                this.inventory.setStackInSlot(slot, baseResult.copyWithCount(count));
                remaining -= count;
            }
        }
        level.setBlock(
            pos,
            (BlockState)((BlockState)state.setValue(PickleJarBlock.FERMENTING, false)).setValue(PickleJarBlock.DONE, recipe.isPresent()),
            3
        );
        this.refresh();
        level.updateNeighbourForOutputSignal(pos, state.getBlock());
        this.checkForValidRecipeAndTryStartFermenting();
    }

    @Override
    public void resetProgress() {
        this.maxProgress = 0;
        this.progress = 0;
        this.setChanged();
    }

    @Override
    public boolean insertItem(ItemStack stack, Player player) {
        if (stack.isEmpty() || stack.is(PICKLE_JAR_BLACKLIST)) {
            return false;
        }
        Item container = ItemUtils.getContainerItem(stack);
        int slotLimit = maxInSlot(stack);
        int totalInserted = 0;
        for (int i = 0; i < 4 && totalInserted < slotLimit; i++) {
            ItemStack existing = this.inventory.getStackInSlot(i);
            if (!existing.isEmpty() && ItemStack.isSameItemSameTags(existing, stack) && existing.getCount() < slotLimit) {
                int canAdd = slotLimit - existing.getCount();
                int addCount = Math.min(Math.min(stack.getCount(), slotLimit - totalInserted), canAdd);
                if (addCount > 0) {
                    existing.grow(addCount);
                    this.inventory.setStackInSlot(i, existing);
                    totalInserted += addCount;
                }
            }
        }
        if (totalInserted == 0) {
            for (int i = 0; i < 4; i++) {
                if (this.inventory.getStackInSlot(i).isEmpty()) {
                    int insertCount = Math.min(stack.getCount(), slotLimit);
                    this.inventory.setStackInSlot(i, stack.copyWithCount(insertCount));
                    totalInserted = insertCount;
                    break;
                }
            }
        }
        if (totalInserted <= 0) {
            return false;
        }
        if (!player.isCreative()) {
            stack.shrink(totalInserted);
        }
        // 1.2.0：容器剩余物不是玻璃瓶的物品，插入后把容器部分返还玩家
        if (container != Items.AIR) {
            ItemUtils.getItemToLivingEntity((LivingEntity)player, container.getDefaultInstance().copyWithCount(totalInserted));
        }
        return true;
    }

    @Override
    public boolean extractItem(Player player, ItemStack held) {
        int index = -1;
        for (int i = 3; i >= 0; i--) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            return false;
        }
        ItemStack stack = this.inventory.getStackInSlot(index);
        Item container = ItemUtils.getContainerItem(stack);
        int extractCount;
        if (container != Items.AIR) {
            if (!held.is(container)) {
                if (held.isEmpty() || held.is(TagMod.INGREDIENT_CONTAINER)) {
                    player.displayClientMessage(
                        Component.translatable("message.kaleidoscope_chinesefood.pickle_jar.need_container", container.getDefaultInstance().getHoverName()), true);
                }
                return false;
            }
            extractCount = player.isCreative() ? stack.getCount() : Math.min(stack.getCount(), held.getCount());
        } else {
            if (!held.isEmpty()) {
                return false;
            }
            extractCount = stack.getCount();
        }
        if (extractCount <= 0) {
            return false;
        }
        ItemStack extracted = stack.copyWithCount(extractCount);
        if (!fitsInInventory(player, extracted)) {
            return false;
        }
        if (container != Items.AIR && !player.isCreative()) {
            held.shrink(extractCount);
        }
        ItemStack remaining = stack.copy();
        remaining.shrink(extractCount);
        this.inventory.setStackInSlot(index, remaining.isEmpty() ? ItemStack.EMPTY : remaining);
        giveToPlayer(player, extracted);
        return true;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < 4; i++) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static boolean fitsInInventory(Player player, ItemStack stack) {
        return moveToInventory(player.getInventory(), stack, true) <= 0;
    }

    private static void giveToPlayer(Player player, ItemStack stack) {
        moveToInventory(player.getInventory(), stack, false);
    }

    private static int moveToInventory(Inventory inventory, ItemStack stack, boolean simulate) {
        int count = stack.getCount();
        int maxStackSize = stack.getMaxStackSize();
        int mainSlotCount = 36;
        for (int i = 0; i < mainSlotCount && count > 0; i++) {
            ItemStack slot = inventory.getItem(i);
            if (slot.isEmpty() || !ItemStack.isSameItemSameTags(slot, stack) || slot.getCount() >= slot.getMaxStackSize()) {
                continue;
            }
            int move = Math.min(count, slot.getMaxStackSize() - slot.getCount());
            if (move <= 0) {
                continue;
            }
            if (!simulate) {
                slot.grow(move);
            }
            count -= move;
        }
        if (count > 0 && inventory.getItem(inventory.selected).isEmpty()) {
            int move = Math.min(count, maxStackSize);
            if (!simulate) {
                inventory.setItem(inventory.selected, stack.copyWithCount(move));
            }
            count -= move;
        }
        // 1.2.0.2:遍历背包全部空槽(修复 fitsInInventory 主手非空时恒 false 导致无法取出)
        for (int i = 0; i < mainSlotCount && count > 0; i++) {
            if (i == inventory.selected || !inventory.getItem(i).isEmpty()) {
                continue;
            }
            int move = Math.min(count, maxStackSize);
            if (!simulate) {
                inventory.setItem(i, stack.copyWithCount(move));
            }
            count -= move;
        }
        return count;
    }

    /** 官方 1.1.16：单槽容量 = min(4, 物品最大堆叠)。 */
    private static int maxInSlot(ItemStack stack) {
        return Math.min(4, stack.getMaxStackSize());
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

    @Override
    public int getProgress() {
        return this.progress;
    }

    @Override
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
