package com.bmt.kaleidoscope_chinesefood.client.light;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;

public final class DynamicLightRequests {
   public static final Map<BlockPos, Integer> BLOCK_LIGHTS = new ConcurrentHashMap<>();

   private DynamicLightRequests() {
   }

   public static void request(BlockPos pos, int luminance) {
      BLOCK_LIGHTS.put(pos.immutable(), luminance);
   }

   public static void remove(BlockPos pos) {
      BLOCK_LIGHTS.remove(pos);
   }
}
