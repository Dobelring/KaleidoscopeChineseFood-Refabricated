package com.bmt.kaleidoscope_chinesefood.mixins.tavern;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * 酒馆联动 mixin 的守卫：未装酒馆（或目标类不存在）时整份配置不生效，避免 NoClassDefFoundError。
 */
public final class TavernMixinConfigPlugin implements IMixinConfigPlugin {
    private static final String TAVERN_CLASS = "com/github/ysbbbbbb/kaleidoscopetavern/block/brew/TapBlock.class";

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!resourceExists(TAVERN_CLASS)) {
            return false;
        }
        return resourceExists(targetClassName.replace('.', '/') + ".class");
    }

    private static boolean resourceExists(String resourceName) {
        ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader != null && contextLoader.getResource(resourceName) != null) {
            return true;
        }
        ClassLoader pluginLoader = TavernMixinConfigPlugin.class.getClassLoader();
        return pluginLoader != null && pluginLoader.getResource(resourceName) != null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return Collections.emptyList();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
