package com.bmt.kaleidoscope_chinesefood.client.event;

import com.bmt.kaleidoscope_chinesefood.block.CoupletBlock;
import com.bmt.kaleidoscope_chinesefood.block.FuCharacterBlock;
import com.bmt.kaleidoscope_chinesefood.block.HorizontalScrollBlock;
import com.bmt.kaleidoscope_chinesefood.block.KongmingLanternBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight.KTItems;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class TooltipEvents {
    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            if (stack.is(ModItems.CENTURY_EGG)) {
                lines.add(Component.translatable("item.kaleidoscope_chinesefood.century_egg.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (stack.is(ModItems.CHINESE_SAUERKRAUT)) {
                lines.add(Component.translatable("item.kaleidoscope_chinesefood.chinese_sauerkraut.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (stack.is(ModItems.SALTED_EGG)) {
                lines.add(Component.translatable("item.kaleidoscope_chinesefood.salted_egg.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (stack.is(ModItems.SALT)) {
                // 1.2.0：盐块方块 tooltip 键随 id 改名（官方键为 block.*.tooltip）
                lines.add(Component.translatable("block.kaleidoscope_chinesefood.salt.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (stack.is(ModItems.FIRECRACKER)) {
                lines.add(Component.translatable("item.kaleidoscope_chinesefood.firecracker.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (stack.is(ModItems.MOONCAKE_MOLD)) {
                lines.add(Component.translatable("item.kaleidoscope_chinesefood.mooncake_mold.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (stack.is(ModItems.MOONCAKE)) {
                // 1.2.0：月饼 tooltip 从 item 键改为 block 键（对齐官方：tooltip 挂在方块上）
                lines.add(Component.translatable("block.kaleidoscope_chinesefood.mooncake.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            // 方块描述文案在客户端提示回调中补充
            if (stack.getItem() instanceof BlockItem blockItem) {
                MutableComponent desc = null;
                if (blockItem.getBlock() instanceof FuCharacterBlock) {
                    desc = Component.translatable("block.kaleidoscope_chinesefood.fu_character.tooltip");
                } else if (blockItem.getBlock() instanceof CoupletBlock) {
                    desc = Component.translatable("block.kaleidoscope_chinesefood.couplet.tooltip");
                } else if (blockItem.getBlock() instanceof HorizontalScrollBlock) {
                    desc = Component.translatable("block.kaleidoscope_chinesefood.horizontal_scroll.tooltip");
                } else if (blockItem.getBlock() instanceof KongmingLanternBlock) {
                    desc = Component.translatable("block.kaleidoscope_chinesefood.kongming_lantern.tooltip");
                } else if (blockItem.getBlock() instanceof com.bmt.kaleidoscope_chinesefood.block.BowlStackBlock) {
                    desc = Component.translatable("block.kaleidoscope_chinesefood.bowl_stack.tooltip");
                }

                if (desc != null) {
                    lines.add(desc.withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                }
            }

            if (!FabricLoader.getInstance().isModLoaded("kaleidoscope_twilight")) {
                Identifier itemId = stack.getItem().builtInRegistryHolder().key().identifier();
                if (KTItems.FROZEN_BUN_ID.equals(itemId)) {
                    lines.add(Component.translatable("item.kaleidoscope_twilight.frozen_bun.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
                }
            }
        });
    }
}
