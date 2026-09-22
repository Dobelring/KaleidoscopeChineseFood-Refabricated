package com.bmt.kaleidoscope_chinesefood.init;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;

public class ModCompostables {
    public ModCompostables() {
    }

    // 原 Forge 的 @EventBusSubscriber + FMLCommonSetupEvent#enqueueWork，
    // Fabric 直接在 ModInitializer 里调用；ComposterBlock.COMPOSTABLES 在 1.20.1 是公开静态字段
    public static void init() {
        Object2FloatMap<ItemLike> compostMap = ComposterBlock.COMPOSTABLES;
        compostMap.put(ModItems.EGGPLANT, 0.65F);
        compostMap.put(ModItems.EGGPLANT_SEED, 0.3F);
    }
}
