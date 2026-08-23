package com.bmt.kaleidoscope_chinesefood.crafting;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record PickleJarInput(SimpleContainer container) implements RecipeInput {
   @NotNull
   public ItemStack getItem(int index) {
      return this.container.getItem(index);
   }

   public int size() {
      return this.container.getContainerSize();
   }
}
