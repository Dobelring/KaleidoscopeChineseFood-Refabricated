package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry.TeacupData;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeacupItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModTea {
    public static Identifier LAPSANG;
    public static Identifier HK_MILK_TEA;
    public static Item LAPSANG_ITEM;
    public static Item HK_MILK_TEA_ITEM;

    public static void init() {
        LAPSANG = KaleidoscopeChineseFood.id("lapsang");
        LAPSANG_ITEM = registerTeacup(
                LAPSANG,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.TUNDRA_STRIDER, 9600), 0.9999F)
        );
        HK_MILK_TEA = KaleidoscopeChineseFood.id("hk_milk_tea");
        HK_MILK_TEA_ITEM = registerTeacup(
                HK_MILK_TEA,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SULFUR, 9600), 0.9999F)
        );
    }

    private static Item registerTeacup(Identifier id, TeacupData data) {
        ResourceKey<net.minecraft.world.level.block.Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        TeacupBlock block = new TeacupBlock(BlockBehaviour.Properties.of().setId(blockKey), data.getMaxCount());
        VoxelShape aabb = data.getAABB();
        if (aabb != null) {
            block.setAABB(aabb);
        }
        Registry.register(BuiltInRegistries.BLOCK, id, block);

        TeacupItem item = new TeacupItem(block, data.getEffects(),
                new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)));
        item.registerBlocks(Item.BY_BLOCK, item);
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return item;
    }
}
