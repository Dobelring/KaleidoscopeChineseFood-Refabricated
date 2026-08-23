package com.bmt.kaleidoscope_chinesefood.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

/**
 * 26.1: Block/BlockBehaviour no longer declares appendHoverText; block tooltip lines
 * are provided by the item via the new TooltipDisplay + Consumer signature.
 */
public class TooltipBlockItem extends BlockItem {
    private final String translationKey;

    public TooltipBlockItem(Block block, Properties properties, String translationKey) {
        super(block, properties);
        this.translationKey = translationKey;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);
        tooltipAdder.accept(Component.translatable(translationKey)
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
