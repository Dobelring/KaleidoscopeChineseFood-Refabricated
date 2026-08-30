package com.bmt.kaleidoscope_chinesefood.inventory;

import com.bmt.kaleidoscope_chinesefood.block.FreezerBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.FreezerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModMenuTypes;
import com.bmt.kaleidoscope_chinesefood.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FreezerMenu extends AbstractContainerMenu {
   private final Container container;
   private final boolean isTop;
   private final DataSlot[] progressSlots;
   private final DataSlot[] totalTimeSlots;

   public FreezerMenu(int id, Inventory playerInv, Container container) {
      super(container.getContainerSize() == 36 ? ModMenuTypes.FREEZER_TOP_MENU : ModMenuTypes.FREEZER_BOTTOM_MENU, id);
      checkContainerSize(container, container.getContainerSize());
      this.container = container;
      this.isTop = container.getContainerSize() == 36;
      container.startOpen(playerInv.player);
      // 在菜单构造器中处理开门声音和方块状态（绕过 ContainerUser 覆写导致的卡死）
      if (container instanceof FreezerBlockEntity be) {
         Level beLevel = be.getLevel();
         if (beLevel != null && !beLevel.isClientSide()) {
            BlockPos bePos = be.getBlockPos();
            BlockState state = beLevel.getBlockState(bePos);
            boolean isTop = state.getValue(FreezerBlock.TOP);
            if (isTop) {
               beLevel.setBlock(bePos, state.setValue(FreezerBlock.UPPER_OPEN, true), 3);
            } else {
               beLevel.setBlock(bePos, state.setValue(FreezerBlock.LOWER_OPEN, true), 3);
               BlockPos upperPos = bePos.above();
               BlockState upperState = beLevel.getBlockState(upperPos);
               if (upperState.getBlock() instanceof FreezerBlock && upperState.getValue(FreezerBlock.TOP)) {
                  beLevel.setBlock(upperPos, upperState.setValue(FreezerBlock.LOWER_OPEN, true), 3);
               }
            }
            beLevel.playSound(null, bePos, ModSounds.FREEZER_OPEN, SoundSource.BLOCKS, 0.5F, 1.0F);
         }
      }
      this.addSlots(playerInv);
      int size = container.getContainerSize();
      this.progressSlots = new DataSlot[size];
      this.totalTimeSlots = new DataSlot[size];

      for (int i = 0; i < size; i++) {
         this.progressSlots[i] = DataSlot.standalone();
         this.totalTimeSlots[i] = DataSlot.standalone();
         this.addDataSlot(this.progressSlots[i]);
         this.addDataSlot(this.totalTimeSlots[i]);
      }
   }

   private void addSlots(Inventory playerInv) {
      int rows = this.container.getContainerSize() / 9;

      for (int row = 0; row < rows; row++) {
         for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(this.container, col + row * 9, 8 + col * 18, 18 + row * 18));
         }
      }

      int playerY;
      if (this.isTop) {
         playerY = 103;
      } else {
         playerY = 140;
      }

      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, playerY + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.addSlot(new Slot(playerInv, col, 8 + col * 18, playerY + 58));
      }
   }

   public boolean stillValid(@NotNull Player pPlayer) {
      return this.container.stillValid(pPlayer);
   }

   @NotNull
   public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(pIndex);
      if (slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (pIndex < this.container.getContainerSize()) {
            if (!this.moveItemStackTo(itemstack1, this.container.getContainerSize(), this.slots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 0, this.container.getContainerSize(), false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(pPlayer, itemstack1);
      }

      return itemstack;
   }

   public void removed(@NotNull Player pPlayer) {
      super.removed(pPlayer);
      this.container.stopOpen(pPlayer);
      if (this.container instanceof FreezerBlockEntity be) {
         be.removeOpenMenu(this);
         Level beLevel = be.getLevel();
         if (beLevel != null && !beLevel.isClientSide()) {
            BlockPos bePos = be.getBlockPos();
            BlockState state = beLevel.getBlockState(bePos);
            boolean isTop = state.getValue(FreezerBlock.TOP);
            if (isTop) {
               beLevel.setBlock(bePos, state.setValue(FreezerBlock.UPPER_OPEN, false), 3);
            } else {
               beLevel.setBlock(bePos, state.setValue(FreezerBlock.LOWER_OPEN, false), 3);
               BlockPos upperPos = bePos.above();
               BlockState upperState = beLevel.getBlockState(upperPos);
               if (upperState.getBlock() instanceof FreezerBlock && upperState.getValue(FreezerBlock.TOP)) {
                  beLevel.setBlock(upperPos, upperState.setValue(FreezerBlock.LOWER_OPEN, false), 3);
               }
            }
            beLevel.playSound(null, bePos, ModSounds.FREEZER_CLOSE, SoundSource.BLOCKS, 0.5F, 1.0F);
         }
      }
   }

   public Container getContainer() {
      return this.container;
   }

   public boolean isTop() {
      return this.isTop;
   }

   public void setProgress(int slot, int value) {
      if (slot >= 0 && slot < this.progressSlots.length) {
         this.progressSlots[slot].set(value);
      }
   }

   public void setTotalTime(int slot, int value) {
      if (slot >= 0 && slot < this.totalTimeSlots.length) {
         this.totalTimeSlots[slot].set(value);
      }
   }

   public int getProgress(int slot) {
      return this.progressSlots[slot].get();
   }

   public int getTotalTime(int slot) {
      return this.totalTimeSlots[slot].get();
   }
}
