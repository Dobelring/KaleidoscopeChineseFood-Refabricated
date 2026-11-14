package com.bmt.kaleidoscope_chinesefood.compat.jade;

import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import net.minecraft.resources.Identifier;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

// jade 入口点双端加载；26.x Jade 的 ITooltip 继承客户端 NarrationSupplier，
// provider 只能放 client 源集（专用服务器拒载 client 源集类），这里反射桥接。
// registerClient 仅客户端被调用，服务端不会触发反射。
@WailaPlugin
public class ModPlugin implements IWailaPlugin {
   public static final Identifier PICKLE_JAR = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "pickle_jar");

   @Override
   public void registerClient(IWailaClientRegistration registration) {
      try {
         Object provider = Class.forName("com.bmt.kaleidoscope_chinesefood.compat.jade.block.PickleJarComponentProvider").getField("INSTANCE").get(null);
         registration.registerBlockComponent((IBlockComponentProvider)provider, PickleJarBlock.class);
      } catch (ReflectiveOperationException e) {
         // client 源集类仅客户端环境可载；本方法在服务端不会被 Jade 调用
      }
   }
}
