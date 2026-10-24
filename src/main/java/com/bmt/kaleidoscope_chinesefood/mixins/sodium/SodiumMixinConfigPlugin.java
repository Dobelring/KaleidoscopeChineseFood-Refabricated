package com.bmt.kaleidoscope_chinesefood.mixins.sodium;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Sodium 存在才应用 sodium 目标 mixin（结构同 tavern 插件门控）。
 * 1.20.1 的 Sodium 0.5.x 自带光照管线，不回调原版 LevelRenderer.getLightColor，
 * 必须挂它自己的 LightDataAccess 才能让动态光进地形。
 */
public final class SodiumMixinConfigPlugin implements IMixinConfigPlugin {
   private static final String SODIUM_LIGHT_DATA = "me/jellysquid/mods/sodium/client/model/light/data/LightDataAccess.class";

   public SodiumMixinConfigPlugin() {
   }

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      return resourceExists("me/jellysquid/mods/sodium/client/model/light/data/LightDataAccess.class");
   }

   private static boolean resourceExists(String resourceName) {
      ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
      if (contextLoader != null && contextLoader.getResource(resourceName) != null) {
         return true;
      } else {
         ClassLoader pluginLoader = SodiumMixinConfigPlugin.class.getClassLoader();
         return pluginLoader != null && pluginLoader.getResource(resourceName) != null;
      }
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return Collections.emptyList();
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
