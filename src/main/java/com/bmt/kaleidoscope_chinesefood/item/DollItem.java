package com.bmt.kaleidoscope_chinesefood.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/**
 * 贡献者玩偶物品（1.21.11 无 kaleidoscope_doll 模组，国味自实现，与 liquor 同款）：
 * 显示名固定 block.kaleidoscope_doll.doll（"玩偶"，与 doll 模组原版行为一致），
 * tooltip 追加"作者：xxx"（键在国味 lang，contributor_0..5 已有）。
 */
public class DollItem extends BlockItem {
    private static final Component DOLL_NAME = Component.translatable("block.kaleidoscope_doll.doll");
    private final String authorKey;

    public DollItem(Block block, String authorKey, Properties properties) {
        super(block, properties);
        this.authorKey = authorKey;
    }

    @Override
    public Component getName(ItemStack stack) {
        return DOLL_NAME;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(this.authorKey)
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
