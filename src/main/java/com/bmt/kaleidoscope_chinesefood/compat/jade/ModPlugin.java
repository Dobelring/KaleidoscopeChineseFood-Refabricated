package com.bmt.kaleidoscope_chinesefood.compat.jade;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.compat.jade.block.PickleJarComponentProvider;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;

// 插件入口点已在 fabric.mod.json 的 jade 中配置，无需 @WailaPlugin 注解
public class ModPlugin implements IWailaPlugin {
    public static final ResourceLocation PICKLE_JAR = KaleidoscopeChineseFood.id("pickle_jar");

    public ModPlugin() {
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(PickleJarComponentProvider.INSTANCE, PickleJarBlock.class);
    }
}
