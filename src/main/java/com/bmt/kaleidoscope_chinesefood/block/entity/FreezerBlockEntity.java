package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.block.FreezerBlock;
import com.bmt.kaleidoscope_chinesefood.crafting.FreezerInput;
import com.bmt.kaleidoscope_chinesefood.crafting.FreezingRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.bmt.kaleidoscope_chinesefood.init.ModSounds;
import com.bmt.kaleidoscope_chinesefood.inventory.FreezerMenu;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FreezerBlockEntity extends RandomizableContainerBlockEntity implements MenuProvider {
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

   public void clearRemoved() {
      super.clearRemoved();
      if (this.level != null && !this.initialized) {
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

   private void ensureInitialized() {
      if (!this.initialized) {
         this.initializeCapacity();
         this.initialized = true;
      }
   }

   public boolean isTop() {
      return (Boolean)this.getBlockState().getValue(FreezerBlock.TOP);
   }

   @NotNull
   protected NonNullList<ItemStack> getItems() {
      if (this.items == null) {
         this.ensureInitialized();
      }

      return this.items;
   }

   protected void setItems(@NotNull NonNullList<ItemStack> items) {
      this.ensureInitialized();

      for (int i = 0; i < items.size() && i < this.items.size(); i++) {
         this.items.set(i, (ItemStack)items.get(i));
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

   public int getContainerSize() {
      if (this.initialized && this.items != null) {
         return this.items.size();
      } else {
         return this.isTop() ? 36 : 54;
      }
   }

   public void loadAdditional(@NotNull ValueInput input) {
      super.loadAdditional(input);
      this.ensureInitialized();
      if (!this.tryLoadLootTable(input)) {
         ContainerHelper.loadAllItems(input, this.getItems());
      }

      int[] p = input.getIntArray("Progress").orElse(new int[0]);
      int[] t = input.getIntArray("TotalTime").orElse(new int[0]);
      System.arraycopy(p, 0, this.progress, 0, Math.min(p.length, this.getContainerSize()));
      System.arraycopy(t, 0, this.totalTime, 0, Math.min(t.length, this.getContainerSize()));
   }

   protected void saveAdditional(@NotNull ValueOutput output) {
      super.saveAdditional(output);
      this.ensureInitialized();
      if (!this.trySaveLootTable(output)) {
         ContainerHelper.saveAllItems(output, this.getItems());
      }

      output.putIntArray("Progress", this.progress);
      output.putIntArray("TotalTime", this.totalTime);
   }

   public void drops() {
      if (this.level != null && !this.level.isClientSide()) {
         for (ItemStack stack : this.getItems()) {
            if (!stack.isEmpty()) {
               Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), stack);
            }
         }
      }
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, FreezerBlockEntity be) {
      if (!level.isClientSide()) {
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
      FreezerInput input = new FreezerInput(inv);
      boolean hasRecipe = false;
      int recipeTime = 100;
      if (isTop) {
         Optional<RecipeHolder<RefrigeratingRecipe>> recipe = ((ServerLevel)this.level).recipeAccess().getRecipeFor(ModRecipes.REFRIGERATING_TYPE, input, this.level);
         if (recipe.isPresent()) {
            RefrigeratingRecipe r = (RefrigeratingRecipe)recipe.get().value();
            hasRecipe = true;
            recipeTime = r.calculateProcessingTime(stack.getCount());
            if (this.progress[slot] >= recipeTime) {
               ItemStack result = r.assemble(input, this.level.registryAccess());
               result.setCount(stack.getCount());
               this.getItems().set(slot, result);
               this.progress[slot] = 0;
               this.totalTime[slot] = 0;
               return new FreezerBlockEntity.ProcessingResult(true);
            }
         }
      } else {
         Optional<RecipeHolder<FreezingRecipe>> recipe = ((ServerLevel)this.level).recipeAccess().getRecipeFor(ModRecipes.FREEZING_TYPE, input, this.level);
         if (recipe.isPresent()) {
            FreezingRecipe r = (FreezingRecipe)recipe.get().value();
            hasRecipe = true;
            recipeTime = r.calculateProcessingTime(stack.getCount());
            if (this.progress[slot] >= recipeTime) {
               ItemStack result = r.assemble(input, this.level.registryAccess());
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
      if (this.level != null && !this.level.isClientSide()) {
         BlockState state = this.getBlockState();
         boolean isTop = (Boolean)state.getValue(FreezerBlock.TOP);
         if (isTop) {
            boolean alreadyOpen = (Boolean)state.getValue(FreezerBlock.UPPER_OPEN);
            // 残留状态自愈：方块标记为开但当前没有任何打开的菜单（异常退出导致上次未正常关闭）
            boolean stale = alreadyOpen && this.openMenus.isEmpty();
            this.level.setBlock(this.worldPosition, (BlockState)state.setValue(FreezerBlock.UPPER_OPEN, true), 3);
            if (!alreadyOpen || stale) {
               this.level.playSound(null, this.worldPosition, ModSounds.FREEZER_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
         } else {
            boolean alreadyOpen = (Boolean)state.getValue(FreezerBlock.LOWER_OPEN);
            boolean stale = alreadyOpen && this.openMenus.isEmpty();
            this.level.setBlock(this.worldPosition, (BlockState)state.setValue(FreezerBlock.LOWER_OPEN, true), 3);
            BlockPos upperPos = this.worldPosition.above();
            BlockState upperState = this.level.getBlockState(upperPos);
            if (upperState.getBlock() instanceof FreezerBlock && (Boolean)upperState.getValue(FreezerBlock.TOP)) {
               this.level.setBlock(upperPos, (BlockState)upperState.setValue(FreezerBlock.LOWER_OPEN, true), 3);
            }

            if (!alreadyOpen || stale) {
               com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood.LOGGER.info("[诊断] 冰箱开门音: pos={}", this.worldPosition.toShortString());
               this.level.playSound(null, this.worldPosition, ModSounds.FREEZER_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
         }
      }
   }

   public void stopOpen(Player player) {
      super.stopOpen(player);
      if (this.level != null && !this.level.isClientSide()) {
         BlockState state = this.getBlockState();
         boolean isTop = (Boolean)state.getValue(FreezerBlock.TOP);
         if (isTop) {
            if ((Boolean)state.getValue(FreezerBlock.UPPER_OPEN)) {
               this.level.setBlock(this.worldPosition, (BlockState)state.setValue(FreezerBlock.UPPER_OPEN, false), 3);
               this.level.playSound(null, this.worldPosition, ModSounds.FREEZER_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
         } else if ((Boolean)state.getValue(FreezerBlock.LOWER_OPEN)) {
            this.level.setBlock(this.worldPosition, (BlockState)state.setValue(FreezerBlock.LOWER_OPEN, false), 3);
            BlockPos upperPos = this.worldPosition.above();
            BlockState upperState = this.level.getBlockState(upperPos);
            if (upperState.getBlock() instanceof FreezerBlock && (Boolean)upperState.getValue(FreezerBlock.TOP)) {
               this.level.setBlock(upperPos, (BlockState)upperState.setValue(FreezerBlock.LOWER_OPEN, false), 3);
            }

            this.level.playSound(null, this.worldPosition, ModSounds.FREEZER_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
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
