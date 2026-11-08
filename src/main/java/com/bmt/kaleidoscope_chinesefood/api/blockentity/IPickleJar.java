package com.bmt.kaleidoscope_chinesefood.api.blockentity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IPickleJar {
   int getProgress();

   int getMaxProgress();

   boolean tryStartFermenting(Level var1);

   void resetProgress();

   boolean insertItem(ItemStack var1, Player var2);

   boolean extractItem(Player var1, ItemStack var2);

   boolean isEmpty();
}
