package com.bmt.kaleidoscope_chinesefood.compat.jade;

import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.compat.jade.block.PickleJarComponentProvider;
import net.minecraft.resources.Identifier;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ModPlugin implements IWailaPlugin {
   public static final Identifier PICKLE_JAR = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "pickle_jar");

   @Override
   public void registerClient(IWailaClientRegistration registration) {
      registration.registerBlockComponent(PickleJarComponentProvider.INSTANCE, PickleJarBlock.class);
   }
}
