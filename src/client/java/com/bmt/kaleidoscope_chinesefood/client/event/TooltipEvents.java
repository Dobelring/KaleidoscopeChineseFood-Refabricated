package com.bmt.kaleidoscope_chinesefood.client.event;

import com.bmt.kaleidoscope_chinesefood.block.CoupletBlock;
import com.bmt.kaleidoscope_chinesefood.block.FuCharacterBlock;
import com.bmt.kaleidoscope_chinesefood.block.HorizontalBannerBlock;
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
        ItemTooltipCallback.EVENT.register((stack, context, tooltipType, lines) -> {
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
                lines.add(Component.translatable("item.kaleidoscope_chinesefood.salt.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (stack.is(ModItems.FIRECRACKER)) {
                lines.add(Component.translatable("item.kaleidoscope_chinesefood.firecracker.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (stack.is(ModItems.MOONCAKE_MOLD)) {
                lines.add(Component.translatable("item.kaleidoscope_chinesefood.mooncake_mold.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            if (stack.is(ModItems.MOONCAKE)) {
                lines.add(Component.translatable("item.kaleidoscope_chinesefood.mooncake.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }

            // 方块描述文案在客户端提示回调中补充
            if (stack.getItem() instanceof BlockItem blockItem) {
                MutableComponent desc = null;
                if (blockItem.getBlock() instanceof FuCharacterBlock) {
                    desc = Component.translatable("block.kaleidoscope_chinesefood.fu_character.desc");
                } else if (blockItem.getBlock() instanceof CoupletBlock) {
                    desc = Component.translatable("block.kaleidoscope_chinesefood.couplet_block.desc");
                } else if (blockItem.getBlock() instanceof HorizontalBannerBlock) {
                    desc = Component.translatable("block.kaleidoscope_chinesefood.horizontal_banner.desc");
                } else if (blockItem.getBlock() instanceof KongmingLanternBlock) {
                    desc = Component.translatable("block.kaleidoscope_chinesefood.kongming_lantern.desc");
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
