package com.bmt.kaleidoscope_chinesefood.compat.jade.block;

import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.bmt.kaleidoscope_chinesefood.compat.jade.ModPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum PickleJarComponentProvider implements IBlockComponentProvider {
   INSTANCE;

   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
      if ((Boolean)accessor.getBlockState().getValue(PickleJarBlock.FERMENTING)) {
         if (accessor.getBlockEntity() instanceof PickleJarBlockEntity be) {
            int remainingTicks = Math.max(0, be.getMaxProgress() - be.getProgress());
            MutableComponent timeText = Component.literal(StringUtil.formatTickDuration(remainingTicks, 20.0F));
            tooltip.add(Component.translatable("jade.kaleidoscope_chinesefood.pickle_jar.remaining_time", new Object[]{timeText}));
         }
      }
   }

   public ResourceLocation getUid() {
      return ModPlugin.PICKLE_JAR;
   }
}
