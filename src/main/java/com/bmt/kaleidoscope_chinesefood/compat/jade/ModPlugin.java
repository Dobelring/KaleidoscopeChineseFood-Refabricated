package com.bmt.kaleidoscope_chinesefood.compat.jade;

import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.compat.jade.block.PickleJarComponentProvider;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ModPlugin implements IWailaPlugin {
   public static final ResourceLocation PICKLE_JAR = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", "pickle_jar");

   public void registerClient(IWailaClientRegistration registration) {
      registration.registerBlockComponent(PickleJarComponentProvider.INSTANCE, PickleJarBlock.class);
   }
}
