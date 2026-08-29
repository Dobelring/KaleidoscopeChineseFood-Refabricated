package com.bmt.kaleidoscope_chinesefood.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class BunItem extends Item {
   private final String fakeModId;

   public BunItem(Properties properties, String fakeModId) {
      super(properties);
      this.fakeModId = fakeModId;
   }

   public String getCreatorModId(ItemStack stack) {
      return this.fakeModId;
   }
}
