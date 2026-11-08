package com.bmt.kaleidoscope_chinesefood.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BunItem extends Item {
    private final String fakeModId;

    public BunItem(Properties properties, String fakeModId) {
        super(properties);
        this.fakeModId = fakeModId;
    }

    /** 1.2.0：冻馒头 tooltip 从事件类内联到物品（原 client.event.TooltipEvents），twilight 未加载时显示。 */
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("kaleidoscope_twilight")) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (com.bmt.kaleidoscope_chinesefood.init.kaleidoscope_twilight.KTItems.FROZEN_BUN_ID.equals(itemId)) {
                tooltip.add(Component.translatable("item.kaleidoscope_twilight.frozen_bun.tooltip").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            }
        }
    }

    // TODO(fabric): 原为 Forge IForgeItem#getCreatorModId(ItemStack) 的覆写（供 JEI/创造模式物品归属显示使用），
    // Fabric 无对应钩子，故保留为普通方法以不丢失该字段的语义。
    public String getCreatorModId(ItemStack stack) {
        return this.fakeModId;
    }
}
