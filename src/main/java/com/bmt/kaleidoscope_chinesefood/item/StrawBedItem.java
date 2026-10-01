package com.bmt.kaleidoscope_chinesefood.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * 稻草床物品（官方 1.1.14）。官方在方块 appendHoverText 上加 tooltip，
 * 26.x 起方块侧不再有 appendHoverText（tooltip 全在物品侧），故在此实现。
 */
public class StrawBedItem extends BedItem {
    public StrawBedItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flag);
        tooltip.accept(Component.translatable("item.kaleidoscope_chinesefood.straw_bed.tooltip")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
