package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.api.IPickleJar;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * 1.2.0:存取逻辑重做——pickle_jar_blacklist tag、有容器剩余物的物品(碗装/瓶装)禁止放入、
 * 取出需手持对应容器(need_container 提示,取出量按手持容器数)、无容器产物须空手取、
 * 取出前检查背包空间、蜂蜜瓶等容器判定走 cookery ItemUtils.getContainerItem(无容器 = AIR)。
 */
public class PickleJarBlockEntity extends BlockEntity implements IPickleJar, Container {
    public static final int SLOT_LIMIT = 4;
    public static final int TOTAL_SLOTS = 4;
    public static final TagKey<Item> PICKLE_JAR_BLACKLIST = TagKey.create(Registries.ITEM, KaleidoscopeChineseFood.id("pickle_jar_blacklist"));
    private boolean hasValidRecipe = false;
    private boolean pendingRecipeCheck = false;

    public final SimpleItemHandler inventory = new SimpleItemHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            PickleJarBlockEntity.this.refresh();
            if (PickleJarBlockEntity.this.level instanceof ServerLevel) {
                BlockPos pos = PickleJarBlockEntity.this.worldPosition;
                BlockState state = PickleJarBlockEntity.this.getBlockState();
                PickleJarBlockEntity.this.level.updateNeighbourForOutputSignal(pos, state.getBlock());
                PickleJarBlockEntity.this.checkForValidRecipeAndTryStartFermenting();
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            return !state.getValue(PickleJarBlock.FERMENTING)
                && !stack.is(PICKLE_JAR_BLACKLIST)
                && ItemUtils.getContainerItem(stack) == Items.AIR;
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
            if (stack.isEmpty() || stack.is(PICKLE_JAR_BLACKLIST) || ItemUtils.getContainerItem(stack) != Items.AIR) {
                return stack;
            }
            // 官方 1.1.16:每槽上限从固定 4 改为 min(4, 物品最大堆叠)
            int slotLimit = maxInSlot(stack);
            ItemStack targetSlot = this.getStackInSlot(slot);
            if (!targetSlot.isEmpty()) {
                if (targetSlot.getItem() == stack.getItem() && targetSlot.getCount() < slotLimit) {
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
                    if (!existing.isEmpty() && existing.getItem() == remaining.getItem() && existing.getCount() < slotLimit) {
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
            } else {
                ItemStack stack = this.getStackInSlot(slot);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                } else {
                    // 1.2.0:有容器剩余物的产物不允许漏斗抽走
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
            }
        }
    };
    private int progress = 0;
    private int maxProgress = 0;

    public PickleJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PICKLE_JAR, pos, state);
    }

    /** 统一刷新入口:setChanged + 状态同步 + 向附近玩家显式推送 BE 数据包(渲染立即刷新)。 */
    public void refresh() {
        this.setChanged();
        if (this.level instanceof ServerLevel serverLevel) {
            BlockPos pos = this.worldPosition;
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(pos, state, state, 3);
            ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
            for (ServerPlayer player : serverLevel.players()) {
                if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64.0 * 64.0) {
                    player.connection.send(packet);
                }
            }
        }
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        // clearRemoved 在区块加载(promotePendingBlockEntity)期间被调用,此时对世界的任何同步 getChunk
        // 都可能 getChunkBlocking 永久阻塞(watchdog 死锁崩溃),因此只打标记,延迟到下一个 tick 再查配方
        if (this.level != null && !this.level.isClientSide) {
            this.pendingRecipeCheck = true;
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PickleJarBlockEntity be) {
        if (!level.isClientSide) {
            if (be.pendingRecipeCheck) {
                be.pendingRecipeCheck = false;
                be.checkForValidRecipeAndTryStartFermenting();
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
    }

    public void checkForValidRecipe() {
        if (this.level != null && !this.level.isClientSide) {
            boolean oldHasValidRecipe = this.hasValidRecipe;
            this.hasValidRecipe = this.getCurrentRecipe().isPresent();
            if (oldHasValidRecipe != this.hasValidRecipe) {
                this.refresh();
            }
        }
    }

    public void checkForValidRecipeAndTryStartFermenting() {
        if (this.level != null && !this.level.isClientSide) {
            this.checkForValidRecipe();
            BlockState state = this.getBlockState();
            if (!state.getValue(PickleJarBlock.OPEN) && !state.getValue(PickleJarBlock.FERMENTING) && this.hasValidRecipe) {
                this.getCurrentRecipe()
                        .ifPresent(
                                recipe -> {
                                    this.maxProgress = recipe.getFermentTime();
                                    this.progress = 0;
                                    this.level
                                            .setBlock(
                                                    this.worldPosition,
                                                    state.setValue(PickleJarBlock.FERMENTING, true).setValue(PickleJarBlock.DONE, false),
                                                    3
                                            );
                                    this.refresh();
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

            PickleJarInput input = new PickleJarInput(container);
            return this.level.getRecipeManager().getRecipeFor(ModRecipes.PICKLE_JAR_TYPE, input, this.level).map(RecipeHolder::value);
        }
    }

    @Override
    public boolean tryStartFermenting(Level level) {
        return this.hasValidRecipe
                ? this.getCurrentRecipe()
                        .map(recipe -> {
                            this.maxProgress = recipe.getFermentTime();
                            this.progress = 0;
                            this.setChanged();
                            return true;
                        })
                        .orElse(false)
                : false;
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
            // 总产出 = 单次配方产出 × 4(每格最多 4),上限 16,按槽位分配
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
        level.updateNeighborsAt(pos, state.getBlock());
        this.checkForValidRecipeAndTryStartFermenting();
    }

    @Override
    public void resetProgress() {
        this.progress = this.maxProgress = 0;
        this.setChanged();
    }

    /** 官方 1.1.16:单槽容量 = min(4, 物品最大堆叠)。 */
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
            if (!existing.isEmpty() && existing.getItem() == stack.getItem() && existing.getCount() < slotLimit) {
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
            for (int ix = 0; ix < 4; ix++) {
                if (this.inventory.getStackInSlot(ix).isEmpty()) {
                    int insertCount = Math.min(stack.getCount(), slotLimit);
                    this.inventory.setStackInSlot(ix, stack.copyWithCount(insertCount));
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

        // 1.2.0:有容器剩余物的物品,插入后把容器部分返还玩家(如蜂蜜瓶插入后返还玻璃瓶)
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
                        Component.translatable("message.kaleidoscope_chinesefood.pickle_jar.need_container", container.getDefaultInstance().getHoverName()),
                        true
                    );
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
            if (slot.isEmpty() || !slot.is(stack.getItem()) || slot.getCount() >= slot.getMaxStackSize()) {
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

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < 4; i++) {
            if (!this.inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }

        return true;
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

    @NotNull
    public CompoundTag getUpdateTag(@NotNull Provider registries) {
        return this.saveCustomOnly(registries);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", this.inventory.serializeNBT(registries));
        tag.putInt("Progress", this.progress);
        tag.putInt("MaxProgress", this.maxProgress);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        this.progress = tag.getInt("Progress");
        this.maxProgress = tag.getInt("MaxProgress");
        if (this.level != null && !this.level.isClientSide) {
            this.checkForValidRecipe();
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.refresh();
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        this.refresh();
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
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.inventory.clearContent();
    }
}
