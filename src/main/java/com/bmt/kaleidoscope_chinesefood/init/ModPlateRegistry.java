package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry.PlateData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

/**
 * 盘子（拼盘）数据注册。
 * <p>
 * cookery 的 {@code PlateRegistry.registerPlateData(String, PlateData)} 会把 id 强制塞进 cookery 命名空间，
 * 所以这里直接往 {@code PLATE_DATA_MAP} 里放自己命名空间的 key；cookery 的
 * {@code CommonRegistry.registerPlateBlocks()} 会按 map 的 key 代注册方块与物品，
 * 因此必须在 cookery 初始化之前执行（chinesefood 按 mod id 字母序先于 cookery）。
 */
public class ModPlateRegistry {
   public static ResourceLocation GOLDEN_APPLE_PLATTER;

   public static void init() {
      GOLDEN_APPLE_PLATTER = registerPlateData(
         "golden_apple_platter", PlateData.create(4).setServingItems(() -> Items.GOLDEN_APPLE).setLootItem(Items.BOWL).platterAABB()
      );
   }

   private static ResourceLocation registerPlateData(String name, PlateData data) {
      ResourceLocation id = KaleidoscopeChineseFood.id(name);
      PlateRegistry.PLATE_DATA_MAP.put(id, data);
      return id;
   }
}
