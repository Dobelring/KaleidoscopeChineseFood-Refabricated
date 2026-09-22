package com.bmt.kaleidoscope_chinesefood.client.event;

import com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight.KTItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class TooltipEvents {
    public TooltipEvents() {
    }

    // 原 Forge 是 @EventBusSubscriber(Bus.FORGE) + ItemTooltipEvent，
    // Fabric 改为 ItemTooltipCallback，并在客户端入口点里调用本方法注册
    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, tooltipFlag, lines) -> {
            if (!FabricLoader.getInstance().isModLoaded("kaleidoscope_twilight")) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                if (KTItems.FROZEN_BUN_ID.equals(itemId)) {
                    lines.add(
                        Component.translatable("item.kaleidoscope_twilight.frozen_bun.tooltip")
                            .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
                    );
                }
            }
        });
    }
}
