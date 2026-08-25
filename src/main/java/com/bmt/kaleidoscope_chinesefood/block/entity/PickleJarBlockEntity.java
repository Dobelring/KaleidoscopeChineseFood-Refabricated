package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.api.blockentity.IPickleJar;
import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarInput;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.bmt.kaleidoscope_chinesefood.util.SimpleItemHandler;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class PickleJarBlockEntity extends BlockEntity implements IPickleJar, Container {
    public static final int SLOT_LIMIT = 4;
    public static final int TOTAL_SLOTS = 4;
    private boolean hasValidRecipe = false;
    public final SimpleItemHandler inventory = new SimpleItemHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            PickleJarBlockEntity.this.setChanged();
            if (PickleJarBlockEntity.this.level != null && !PickleJarBlockEntity.this.level.isClientSide()) {
                PickleJarBlockEntity.this.level
                        .sendBlockUpdated(
                                PickleJarBlockEntity.this.worldPosition,
                                PickleJarBlockEntity.this.getBlockState(),
                                PickleJarBlockEntity.this.getBlockState(),
                                3
                        );
                // sendBlockUpdated 只广播方块状态；渲染器读取的是客户端 BE 数据，
                // 必须显式构造数据包推送给附近玩家，否则取出物品后客户端仍渲染旧内容。
                PickleJarBlockEntity.this.pushDataPacket();
                PickleJarBlockEntity.this.checkForValidRecipeAndTryStartFermenting();
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            BlockState state = PickleJarBlockEntity.this.getBlockState();
            return !state.getValue(PickleJarBlock.FERMENTING);
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
            } else if (stack.isEmpty()) {
                return stack;
            } else {
                ItemStack targetSlot = this.getStackInSlot(slot);
                if (!targetSlot.isEmpty()) {
                    if (targetSlot.getItem() == stack.getItem() && targetSlot.getCount() < 4) {
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
                        if (!existing.isEmpty() && existing.getItem() == remaining.getItem() && existing.getCount() < 4) {
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

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (this.level != null && !this.level.isClientSide()) {
            this.checkForValidRecipe();
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PickleJarBlockEntity be) {
        if (!level.isClientSide()) {
            if (!state.getValue(PickleJarBlock.OPEN) && state.getValue(PickleJarBlock.FERMENTING)) {
                be.progress++;
                if (be.progress >= be.maxProgress) {
                    be.finishFermenting(state, level, pos);
                    return;
                }

                if (be.progress % 20 == 0) {
                    setChanged(level, pos, state);
                    level.sendBlockUpdated(pos, state, state, 3);
                }
            }
        }
    }

    public void checkForValidRecipe() {
        if (this.level != null && !this.level.isClientSide()) {
            boolean oldHasValidRecipe = this.hasValidRecipe;
            this.hasValidRecipe = this.getCurrentRecipe().isPresent();
            if (oldHasValidRecipe != this.hasValidRecipe) {
                this.setChanged();
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
    }

    public void checkForValidRecipeAndTryStartFermenting() {
        if (this.level != null && !this.level.isClientSide()) {
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
                                    this.setChanged();
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
            return ((ServerLevel)this.level).recipeAccess().getRecipeFor(ModRecipes.PICKLE_JAR_TYPE, input, this.level).map(RecipeHolder::value);
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
            int originalCount = baseResult.getCount();
            int perSlotCount = Math.min(originalCount, 4);

            for (int i = 0; i < 4; i++) {
                ItemStack slotResult = baseResult.copyWithCount(perSlotCount);
                this.inventory.setStackInSlot(i, slotResult);
            }
        }

        level.setBlock(pos, state.setValue(PickleJarBlock.FERMENTING, false).setValue(PickleJarBlock.DONE, recipe.isPresent()), 3);
        setChanged(level, pos, state);
        level.sendBlockUpdated(pos, state, this.getBlockState(), 3);
        this.pushDataPacket();
        level.updateNeighborsAt(pos, state.getBlock());
        this.checkForValidRecipeAndTryStartFermenting();
    }

    @Override
    public void resetProgress() {
        this.progress = this.maxProgress = 0;
        this.setChanged();
    }

    @Override
    public void insertItem(ItemStack stack, Player player) {
        if (!stack.isEmpty()) {
            ItemStack remaining = stack.copy();
            int totalInserted = 0;

            for (int i = 0; i < 4 && totalInserted < 4; i++) {
                ItemStack existing = this.inventory.getStackInSlot(i);
                if (!existing.isEmpty() && existing.getItem() == stack.getItem() && existing.getCount() < 4) {
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

    @Override
    public void extractItem(Player player) {
        for (int i = 3; i >= 0; i--) {
            ItemStack stack = this.inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                int extractCount = stack.getCount();
                ItemStack giveStack = stack.copyWithCount(extractCount);
                player.addItem(giveStack);
                this.inventory.setStackInSlot(i, ItemStack.EMPTY);
                return;
            }
        }
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

    @NotNull
    public CompoundTag getUpdateTag(@NotNull Provider registries) {
        return this.saveCustomOnly(registries);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /**
     * 显式把 BE 数据包推送给附近的玩家。sendBlockUpdated 只同步方块状态，
     * 客户端渲染器读取的是 BlockEntity 数据，需要这条额外的同步链路。
     */
    private void pushDataPacket() {
        if (this.level instanceof ServerLevel serverLevel) {
            ClientboundBlockEntityDataPacket packet = this.getUpdatePacket();
            BlockPos pos = this.worldPosition;
            for (ServerPlayer player : serverLevel.players()) {
                if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64.0 * 64.0) {
                    player.connection.send(packet);
                }
            }
        }
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
