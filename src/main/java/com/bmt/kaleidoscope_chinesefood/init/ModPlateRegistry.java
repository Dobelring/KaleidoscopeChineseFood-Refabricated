package com.bmt.kaleidoscope_chinesefood.init;

import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry.PlateData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public class ModPlateRegistry {
   public static ResourceLocation GOLDEN_APPLE_PLATTER;

   public static void init() {
      GOLDEN_APPLE_PLATTER = registerPlateData(
         "golden_apple_platter", PlateData.create(4).setServingItems(() -> Items.GOLDEN_APPLE).setLootItem(Items.BOWL).platterAABB()
      );
   }

   private static ResourceLocation registerPlateData(String name, PlateData data) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", name);
      PlateRegistry.PLATE_DATA_MAP.put(id, data);
      return id;
   }
}
