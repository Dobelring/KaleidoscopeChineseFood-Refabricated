package com.bmt.kaleidoscope_chinesefood.api.blockentity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IPickleJar {
   int getProgress();

   int getMaxProgress();

   boolean tryStartFermenting(Level var1);

   void resetProgress();

   void insertItem(ItemStack var1, Player var2);

   void extractItem(Player var1);

   boolean isEmpty();
}
