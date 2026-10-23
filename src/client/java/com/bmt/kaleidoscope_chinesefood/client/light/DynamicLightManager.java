package com.bmt.kaleidoscope_chinesefood.client.light;

import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class DynamicLightManager {
   private static final List<DynamicLightManager.LightSource> SOURCES = new CopyOnWriteArrayList<>();
   private static final Map<UUID, DynamicLightManager.TrackedSource> TRACKED = new HashMap<>();
   private static final Map<BlockPos, Integer> BLOCK_TRACKED = new HashMap<>();

   private DynamicLightManager() {
   }

   public static void onClientTick() {
      Minecraft mc = Minecraft.getInstance();
      ClientLevel level = mc.level;
      if (level == null) {
         if (!TRACKED.isEmpty() || !BLOCK_TRACKED.isEmpty()) {
            SOURCES.clear();
            TRACKED.clear();
            BLOCK_TRACKED.clear();
            DynamicLightRequests.BLOCK_LIGHTS.clear();
         }
      } else {
         SOURCES.clear();
         Set<UUID> seen = new HashSet<>();

         for (Entity entity : level.entitiesForRendering()) {
            DynamicLightManager.LightSource source = collect(entity);
            if (source != null) {
               SOURCES.add(source);
               seen.add(entity.getUUID());
               BlockPos pos = BlockPos.containing(source.x(), source.y(), source.z());
               DynamicLightManager.TrackedSource prev = TRACKED.get(entity.getUUID());
               if (prev == null) {
                  markDirty(mc, pos, source.luminance());
               } else if (!prev.pos().equals(pos) || prev.luminance() != source.luminance()) {
                  markDirty(mc, prev.pos(), prev.luminance());
                  markDirty(mc, pos, source.luminance());
               }

               TRACKED.put(entity.getUUID(), new DynamicLightManager.TrackedSource(pos, source.luminance()));
            }
         }

         Iterator<Entry<UUID, DynamicLightManager.TrackedSource>> it = TRACKED.entrySet().iterator();

         while (it.hasNext()) {
            Entry<UUID, DynamicLightManager.TrackedSource> entry = it.next();
            if (!seen.contains(entry.getKey())) {
               markDirty(mc, entry.getValue().pos(), entry.getValue().luminance());
               it.remove();
            }
         }

         tickBlockLights(mc);
      }
   }

   private static void tickBlockLights(Minecraft mc) {
      Map<BlockPos, Integer> now = DynamicLightRequests.BLOCK_LIGHTS;

      for (Entry<BlockPos, Integer> entry : now.entrySet()) {
         BlockPos pos = entry.getKey();
         int luminance = entry.getValue();
         Integer old = BLOCK_TRACKED.put(pos, luminance);
         if (old == null) {
            markDirty(mc, pos, luminance);
         } else if (old != luminance) {
            markDirty(mc, pos, Math.max(old, luminance));
         }

         SOURCES.add(new DynamicLightManager.LightSource(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, luminance));
      }

      Iterator<Entry<BlockPos, Integer>> it = BLOCK_TRACKED.entrySet().iterator();

      while (it.hasNext()) {
         Entry<BlockPos, Integer> entry = it.next();
         if (!now.containsKey(entry.getKey())) {
            markDirty(mc, entry.getKey(), entry.getValue());
            it.remove();
         }
      }
   }

   private static DynamicLightManager.LightSource collect(Entity entity) {
      if (entity instanceof KongmingLanternEntity) {
         return new DynamicLightManager.LightSource(entity.getX(), entity.getY() + 0.5, entity.getZ(), 15);
      } else {
         if (entity instanceof ScarecrowEntity scarecrow) {
            ItemStack stack = scarecrow.getItemInHand(InteractionHand.OFF_HAND);
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof LanternBlock lantern) {
               BlockState state = lantern.defaultBlockState();
               int luminance = state.getLightEmission();
               if (luminance > 0) {
                  Vec3 pos = getHeldLanternPos(scarecrow);
                  return new DynamicLightManager.LightSource(pos.x, pos.y, pos.z, luminance);
               }
            }
         }

         return null;
      }
   }

   private static Vec3 getHeldLanternPos(ScarecrowEntity entity) {
      float yRot = entity.getYRot() * (float) (Math.PI / 180.0);
      double leftX = Math.cos(yRot);
      double leftZ = Math.sin(yRot);
      return entity.position().add(leftX * 0.8, 1.35, leftZ * 0.8);
   }

   public static int getLightLevel(BlockPos pos) {
      if (SOURCES.isEmpty()) {
         return 0;
      } else {
         double px = pos.getX() + 0.5;
         double py = pos.getY() + 0.5;
         double pz = pos.getZ() + 0.5;
         int result = 0;

         for (DynamicLightManager.LightSource source : SOURCES) {
            if (source != null) {
               double dx = source.x() - px;
               double dy = source.y() - py;
               double dz = source.z() - pz;
               int light = Mth.floor(source.luminance() - Math.sqrt(dx * dx + dy * dy + dz * dz));
               if (light > result) {
                  result = light;
                  if (light >= 15) {
                     break;
                  }
               }
            }
         }

         return result;
      }
   }

   private static void markDirty(Minecraft mc, BlockPos center, int luminance) {
      // 26.2+ 没有了 LevelRenderer.setSectionDirty，重光改走 LevelExtractor 的方块范围接口
      int r = luminance + 1;
      mc.levelExtractor.setBlocksDirty(center.getX() - r, center.getY() - r, center.getZ() - r, center.getX() + r, center.getY() + r, center.getZ() + r);
   }

   private record LightSource(double x, double y, double z, int luminance) {
   }

   private record TrackedSource(BlockPos pos, int luminance) {
   }
}
