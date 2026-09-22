package com.bmt.kaleidoscope_chinesefood.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class BunItem extends Item {
    private final String fakeModId;

    public BunItem(Properties properties, String fakeModId) {
        super(properties);
        this.fakeModId = fakeModId;
    }

    // TODO(fabric): 原为 Forge IForgeItem#getCreatorModId(ItemStack) 的覆写（供 JEI/创造模式物品归属显示使用），
    // Fabric 无对应钩子，故保留为普通方法以不丢失该字段的语义。
    public String getCreatorModId(ItemStack stack) {
        return this.fakeModId;
    }
}
