package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.block.FreezerBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipeTypes;
import com.bmt.kaleidoscope_chinesefood.init.ModSounds;
import com.bmt.kaleidoscope_chinesefood.menu.FreezerMenu;
import com.bmt.kaleidoscope_chinesefood.recipe.FreezingRecipe;
import com.bmt.kaleidoscope_chinesefood.recipe.RefrigeratingRecipe;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FreezerBlockEntity extends RandomizableContainerBlockEntity implements MenuProvider, ExtendedScreenHandlerFactory {
    private NonNullList<ItemStack> items;
    private int[] progress;
    private int[] totalTime;
    private boolean initialized = false;
    private final Set<FreezerMenu> openMenus = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private int syncCounter = 0;
    private int tickCount = 0;

    public FreezerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FREEZER, pos, state);
    }

    /**
     * 原 Forge 用 BlockEntity#onLoad 作为「实体被加入世界」的钩子，1.20.1 原版没有该方法。
     * 这里用 setLevel 作等价时机：它在 load(NBT) 之后、方块实体进入世界时被调用，
     * 对未经过 NBT 的新放置方块同样会触发。
     */
    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (!this.level.isClientSide && !this.initialized) {
            this.initializeCapacity();
            this.initialized = true;
        }
    }

    private void initializeCapacity() {
        int size = this.isTop() ? 36 : 54;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
        this.progress = new int[size];
        this.totalTime = new int[size];
    }

    public boolean isTop() {
        return (Boolean)this.getBlockState().getValue(FreezerBlock.TOP);
    }

    @NotNull
    protected NonNullList<ItemStack> getItems() {
        if (this.items == null) {
            this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        }

        return this.items;
    }

    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < items.size() && i < this.items.size(); i++) {
            this.getItems().set(i, (ItemStack)items.get(i));
        }
    }

    @NotNull
    protected Component getDefaultName() {
        return Component.translatable(this.isTop() ? "container.kaleidoscope_chinesefood.freezer_top" : "container.kaleidoscope_chinesefood.freezer_bottom");
    }

    @Nullable
    protected AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory) {
        FreezerMenu menu = new FreezerMenu(pContainerId, pPlayerInventory, this);
        this.openMenus.add(menu);
        return menu;
    }

    /**
     * 原 Forge 用 NetworkHooks.openScreen(player, be, buf -> buf.writeInt(be.getContainerSize()))
     * 传容器大小；Fabric 走 ExtendedScreenHandlerFactory，等价。
     */
    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeInt(this.getContainerSize());
    }

    public int getContainerSize() {
        if (this.level != null && !this.level.isClientSide && this.initialized) {
            return this.items.size();
        } else {
            return this.isTop() ? 36 : 54;
        }
    }

    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (!this.initialized) {
            this.initializeCapacity();
            this.initialized = true;
        }

        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items);
        }

        int[] p = tag.getIntArray("Progress");
        int[] t = tag.getIntArray("TotalTime");
        System.arraycopy(p, 0, this.progress, 0, Math.min(p.length, this.getContainerSize()));
        System.arraycopy(t, 0, this.totalTime, 0, Math.min(t.length, this.getContainerSize()));
    }

    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items);
        }

        tag.putIntArray("Progress", this.progress);
        tag.putIntArray("TotalTime", this.totalTime);
    }

    public void drops() {
        if (this.level != null && !this.level.isClientSide) {
            for (ItemStack stack : this.getItems()) {
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
                }
            }
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FreezerBlockEntity be) {
        if (!level.isClientSide) {
            be.tickCount++;
            boolean dirty = false;
            boolean isTop = be.isTop();

            for (int i = 0; i < be.getItems().size(); i++) {
                ItemStack stack = (ItemStack)be.getItems().get(i);
                if (stack.isEmpty()) {
                    if (be.progress[i] != 0 || be.totalTime[i] != 0) {
                        be.progress[i] = 0;
                        be.totalTime[i] = 0;
                        dirty = true;
                    }
                } else {
                    FreezerBlockEntity.ProcessingResult result = be.processSlot(stack, i, isTop);
                    if (result.updated) {
                        dirty = true;
                    }
                }
            }

            if (dirty) {
                be.setChanged();
            }

            be.syncCounter++;
            if (be.syncCounter >= 2) {
                be.syncCounter = 0;
                be.syncProgressToMenus();
            }
        }
    }

    private void syncProgressToMenus() {
        if (!this.openMenus.isEmpty()) {
            for (FreezerMenu menu : this.openMenus) {
                for (int i = 0; i < this.getContainerSize(); i++) {
                    menu.setProgress(i, this.progress[i]);
                    menu.setTotalTime(i, this.totalTime[i]);
                }
            }
        }
    }

    private FreezerBlockEntity.ProcessingResult processSlot(ItemStack stack, int slot, boolean isTop) {
        SimpleContainer inv = new SimpleContainer(new ItemStack[]{stack});
        boolean hasRecipe = false;
        int recipeTime = 100;
        if (isTop) {
            Optional<RefrigeratingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.REFRIGERATING, inv, this.level);
            if (recipe.isPresent()) {
                RefrigeratingRecipe r = recipe.get();
                hasRecipe = true;
                recipeTime = r.calculateProcessingTime(stack.getCount());
                if (this.progress[slot] >= recipeTime) {
                    ItemStack result = r.assemble(inv, this.level.registryAccess());
                    result.setCount(stack.getCount());
                    this.getItems().set(slot, result);
                    this.progress[slot] = 0;
                    this.totalTime[slot] = 0;
                    return new FreezerBlockEntity.ProcessingResult(true);
                }
            }
        } else {
            Optional<FreezingRecipe> recipe = this.level.getRecipeManager().getRecipeFor(ModRecipeTypes.FREEZING, inv, this.level);
            if (recipe.isPresent()) {
                FreezingRecipe r = recipe.get();
                hasRecipe = true;
                recipeTime = r.calculateProcessingTime(stack.getCount());
                if (this.progress[slot] >= recipeTime) {
                    ItemStack result = r.assemble(inv, this.level.registryAccess());
                    result.setCount(stack.getCount());
                    this.getItems().set(slot, result);
                    this.progress[slot] = 0;
                    this.totalTime[slot] = 0;
                    return new FreezerBlockEntity.ProcessingResult(true);
                }
            }
        }

        if (hasRecipe) {
            this.totalTime[slot] = recipeTime;
            this.progress[slot]++;
            return new FreezerBlockEntity.ProcessingResult(true);
        } else if (this.progress[slot] == 0 && this.totalTime[slot] == 0) {
            return new FreezerBlockEntity.ProcessingResult(false);
        } else {
            this.progress[slot] = 0;
            this.totalTime[slot] = 0;
            return new FreezerBlockEntity.ProcessingResult(true);
        }
    }

    public void removeOpenMenu(FreezerMenu menu) {
        this.openMenus.remove(menu);
    }

    public void startOpen(Player player) {
        super.startOpen(player);
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.getBlockState();
            boolean isTop = (Boolean)state.getValue(FreezerBlock.TOP);
            if (isTop) {
                if (!(Boolean)state.getValue(FreezerBlock.UPPER_OPEN)) {
                    this.level.setBlock(this.worldPosition, (BlockState)state.setValue(FreezerBlock.UPPER_OPEN, true), 3);
                    this.level.playSound(null, this.worldPosition, ModSounds.FREEZER_OPEN, SoundSource.BLOCKS, 0.3F, 1.0F);
                }
            } else if (!(Boolean)state.getValue(FreezerBlock.LOWER_OPEN)) {
                this.level.setBlock(this.worldPosition, (BlockState)state.setValue(FreezerBlock.LOWER_OPEN, true), 3);
                BlockPos upperPos = this.worldPosition.above();
                BlockState upperState = this.level.getBlockState(upperPos);
                if (upperState.getBlock() instanceof FreezerBlock && (Boolean)upperState.getValue(FreezerBlock.TOP)) {
                    this.level.setBlock(upperPos, (BlockState)upperState.setValue(FreezerBlock.LOWER_OPEN, true), 3);
                }

                this.level.playSound(null, this.worldPosition, ModSounds.FREEZER_OPEN, SoundSource.BLOCKS, 0.3F, 1.0F);
            }
        }
    }

    public void stopOpen(Player player) {
        super.stopOpen(player);
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.getBlockState();
            boolean isTop = (Boolean)state.getValue(FreezerBlock.TOP);
            if (isTop) {
                if ((Boolean)state.getValue(FreezerBlock.UPPER_OPEN)) {
                    this.level.setBlock(this.worldPosition, (BlockState)state.setValue(FreezerBlock.UPPER_OPEN, false), 3);
                    this.level.playSound(null, this.worldPosition, ModSounds.FREEZER_CLOSE, SoundSource.BLOCKS, 0.3F, 1.0F);
                }
            } else if ((Boolean)state.getValue(FreezerBlock.LOWER_OPEN)) {
                this.level.setBlock(this.worldPosition, (BlockState)state.setValue(FreezerBlock.LOWER_OPEN, false), 3);
                BlockPos upperPos = this.worldPosition.above();
                BlockState upperState = this.level.getBlockState(upperPos);
                if (upperState.getBlock() instanceof FreezerBlock && (Boolean)upperState.getValue(FreezerBlock.TOP)) {
                    this.level.setBlock(upperPos, (BlockState)upperState.setValue(FreezerBlock.LOWER_OPEN, false), 3);
                }

                this.level.playSound(null, this.worldPosition, ModSounds.FREEZER_CLOSE, SoundSource.BLOCKS, 0.3F, 1.0F);
            }
        }
    }

    private static class ProcessingResult {
        final boolean updated;

        ProcessingResult(boolean updated) {
            this.updated = updated;
        }
    }
}
