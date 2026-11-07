package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.api.blockentity.IPickleJar;
import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarInput;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.bmt.kaleidoscope_chinesefood.util.SimpleItemHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

/**
 * 1.2.0：存取逻辑重做——黑名单 tag、容器剩余物判定（仅玻璃瓶）、
 * 取出需手持对应容器（need_container 提示）、背包空间预检查。
 */
public class PickleJarBlockEntity extends BlockEntity implements IPickleJar, Container {
    public static final int SLOT_LIMIT = 4;
    public static final int TOTAL_SLOTS = 4;
    public static final TagKey<Item> PICKLE_JAR_BLACKLIST =
            TagKey.create(Registries.ITEM, KaleidoscopeChineseFood.id("pickle_jar_blacklist"));
    private boolean hasValidRecipe = false;

    public final SimpleItemHandler inventory = new SimpleItemHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            PickleJarBlockEntity.this.refresh();
            if (PickleJarBlockEntity.this.level instanceof ServerLevel serverLevel) {
                BlockPos pos = PickleJarBlockEntity.this.worldPosition;
                BlockState state = PickleJarBlockEntity.this.getBlockState();
                // 罐内槽位数变化要推给比较器
                PickleJarBlockEntity.this.level.updateNeighbourForOutputSignal(pos, state.getBlock());
                // 向附近玩家显式推送 BE 数据包，确保腌菜罐内容物渲染立即刷新
                ClientboundBlockEntityDataPacket packet =
                        ClientboundBlockEntityDataPacket.create(PickleJarBlockEntity.this);
                for (ServerPlayer player : serverLevel.players()) {
                    if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64.0 * 64.0) {
                        player.connection.send(packet);
                    }
                }
                PickleJarBlockEntity.this.checkForValidRecipeAndTryStartFermenting();
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            return !state.getValue(PickleJarBlock.FERMENTING)
                    && !stack.is(PICKLE_JAR_BLACKLIST)
                    && ItemUtils.getContainerItem(stack) == Items.GLASS_BOTTLE;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 4;
        }

        @Override
        @NotNull
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            if (state.getValue(PickleJarBlock.FERMENTING)) {
                return stack;
            }
            if (stack.isEmpty() || stack.is(PICKLE_JAR_BLACKLIST)
                    || ItemUtils.getContainerItem(stack) != Items.GLASS_BOTTLE) {
                return stack;
            }
            // 官方 1.1.16：每槽上限从固定 4 改为 min(4, 物品最大堆叠)
            int slotLimit = maxInSlot(stack);
            ItemStack targetSlot = this.getStackInSlot(slot);
            if (!targetSlot.isEmpty()) {
                if (ItemStack.isSameItemSameComponents(targetSlot, stack) && targetSlot.getCount() < slotLimit) {
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
                    if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, remaining)
                            && existing.getCount() < slotLimit) {
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

        @Override
        @NotNull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            if (state.getValue(PickleJarBlock.FERMENTING)) {
                return ItemStack.EMPTY;
            }
            ItemStack stack = this.getStackInSlot(slot);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            // 1.2.0：容器剩余物不是玻璃瓶的产物不允许漏斗抽走
            if (ItemUtils.getContainerItem(stack) != Items.GLASS_BOTTLE) {
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

    /** 1.2.0：统一刷新入口（对齐官方 refresh）。 */
    public void refresh() {
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (this.level != null && !this.level.isClientSide()) {
            // 26.2 死锁修复：clearRemoved 在区块 postLoad 期间执行，此阶段调用 setChanged →
            // blockEntityChanged → getChunk 会同步等待"自身区块加载完成"，永久卡死地形加载。
            // hasValidRecipe 不持久化，加载期只需静默刷新内存标记（配方查询不碰区块，安全）；
            // setChanged/sendBlockUpdated 由发酵 tick 或交互路径（checkForValidRecipeAndTryStartFermenting）提交。
            this.hasValidRecipe = this.getCurrentRecipe().isPresent();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PickleJarBlockEntity be) {
        if (level.isClientSide()) {
            return;
        }
        if (!state.getValue(PickleJarBlock.OPEN) && state.getValue(PickleJarBlock.FERMENTING)) {
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
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        boolean oldHasValidRecipe = this.hasValidRecipe;
        this.hasValidRecipe = this.getCurrentRecipe().isPresent();
        if (oldHasValidRecipe != this.hasValidRecipe) {
            this.refresh();
        }
    }

    public void checkForValidRecipeAndTryStartFermenting() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }
        this.checkForValidRecipe();
        BlockState state = this.getBlockState();
        if (!state.getValue(PickleJarBlock.OPEN) && !state.getValue(PickleJarBlock.FERMENTING) && this.hasValidRecipe) {
            this.getCurrentRecipe().ifPresent(recipe -> {
                this.maxProgress = recipe.getFermentTime();
                this.progress = 0;
                this.level.setBlock(
                        this.worldPosition,
                        state.setValue(PickleJarBlock.FERMENTING, true).setValue(PickleJarBlock.DONE, false),
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
        PickleJarInput input = new PickleJarInput(container);
        return ((ServerLevel) this.level).recipeAccess().getRecipeFor(ModRecipes.PICKLE_JAR_TYPE, input, this.level).map(RecipeHolder::value);
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
            ItemStack baseResult = r.getOutput().copy();
            // 总产出 = 单次配方产出 × 4（每格最多 4），上限 16，按槽位分配
            int totalOutput = Math.min(baseResult.getCount() * 4, 16);
            int remaining = totalOutput;
            for (int slot = 0; remaining > 0 && slot < 4; slot++) {
                int count = Math.min(remaining, 4);
                this.inventory.setStackInSlot(slot, baseResult.copyWithCount(count));
                remaining -= count;
            }
        }
        level.setBlock(pos, state.setValue(PickleJarBlock.FERMENTING, false).setValue(PickleJarBlock.DONE, recipe.isPresent()), 3);
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

    /** 官方 1.1.16：单槽容量 = min(4, 物品最大堆叠)。 */
    private static int maxInSlot(ItemStack stack) {
        return Math.min(4, stack.getMaxStackSize());
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
            if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < slotLimit) {
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
        if (container != Items.GLASS_BOTTLE) {
            ItemUtils.getItemToLivingEntity(player, container.getDefaultInstance().copyWithCount(totalInserted));
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
        if (container != Items.GLASS_BOTTLE) {
            // 1.2.0：非玻璃瓶容器产物需手持对应容器取出，否则提示 need_container
            if (!held.is(container)) {
                if (held.isEmpty() || held.is(TagMod.INGREDIENT_CONTAINER)) {
                    player.sendOverlayMessage(Component.translatable(
                            "message.kaleidoscope_chinesefood.pickle_jar.need_container",
                            container.getDefaultInstance().getHoverName()));
                }
                return false;
            }
            extractCount = player.isCreative() ? stack.getCount() : Math.min(stack.getCount(), held.getCount());
        } else {
            // 1.2.0：无容器产物须空手取
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
            // 1.2.0：取出前检查背包空间
            return false;
        }
        if (container != Items.GLASS_BOTTLE && !player.isCreative()) {
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
            if (slot.isEmpty() || !ItemStack.isSameItemSameComponents(slot, stack) || slot.getCount() >= slot.getMaxStackSize()) {
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
        if (count > 0 && inventory.getItem(inventory.getSelectedSlot()).isEmpty()) {
            int move = Math.min(count, maxStackSize);
            if (!simulate) {
                inventory.setItem(inventory.getSelectedSlot(), stack.copyWithCount(move));
            }
            count -= move;
        }
        return count;
    }

    @Override
    public int getProgress() {
        return this.progress;
    }

    @Override
    public int getMaxProgress() {
        return this.maxProgress;
    }

    @NotNull
    public CompoundTag getUpdateTag(@NotNull Provider registries) {
        return this.saveCustomOnly(registries);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        this.inventory.serializeNBT(output.child("Inventory"));
        output.putInt("Progress", this.progress);
        output.putInt("MaxProgress", this.maxProgress);
    }

    @Override
    protected void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.inventory.deserializeNBT(input.childOrEmpty("Inventory"));
        this.progress = input.getIntOr("Progress", 0);
        this.maxProgress = input.getIntOr("MaxProgress", 0);
        if (this.level != null && !this.level.isClientSide()) {
            this.checkForValidRecipe();
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        // 容器内容掉落迁移到 BlockEntity.preRemoveSideEffects
        if (this.level != null && !this.level.isClientSide()) {
            for (int i = 0; i < 4; i++) {
                ItemStack stack = this.inventory.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    net.minecraft.world.Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
            this.level.updateNeighbourForOutputSignal(pos, state.getBlock());
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide()) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    // ---- Container (hopper support) ----
    @Override
    public int getContainerSize() {
        return this.inventory.getContainerSize();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.inventory.getItem(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        // 1.2.0：容器剩余物不是玻璃瓶的产物不允许漏斗抽走
        ItemStack current = this.getItem(slot);
        if (!current.isEmpty() && ItemUtils.getContainerItem(current) != Items.GLASS_BOTTLE) {
            return ItemStack.EMPTY;
        }
        return this.inventory.removeItem(slot, amount);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return this.inventory.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.inventory.setItem(slot, stack);
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        BlockState state = this.getBlockState();
        return !state.getValue(PickleJarBlock.FERMENTING)
                && !stack.is(PICKLE_JAR_BLACKLIST)
                && ItemUtils.getContainerItem(stack) == Items.GLASS_BOTTLE;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.inventory.clearContent();
    }
}
