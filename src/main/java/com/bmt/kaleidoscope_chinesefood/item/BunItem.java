package com.bmt.kaleidoscope_chinesefood.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

public class BunItem extends Item {
   private final String fakeModId;

   public BunItem(Properties properties, String fakeModId) {
      super(properties);
      this.fakeModId = fakeModId;
   }

   public String getCreatorModId(ItemStack stack) {
      return this.fakeModId;
   }

   /** 1.2.0:冻馒头 tooltip 从 TooltipEvents 移入物品类(行为不变)。 */
   @Override
   public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      super.appendHoverText(stack, context, tooltip, flag);
      tooltip.add(
         Component.translatable("item.kaleidoscope_twilight.frozen_bun.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
      );
   }
}
