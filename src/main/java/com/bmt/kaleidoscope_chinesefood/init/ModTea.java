package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry.TeacupData;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeacupItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * 茶杯注册：数据（TeacupData）+ 方块/物品。
 * <p>
 * cookery 1.3.0.9 只在其自身 ModBlocks/ModItems 里注册它自己的茶杯；
 * 我们的茶杯需要自行创建方块与物品，否则创造栏/配方拿到的是不存在的物品。
 * 效果通过 Supplier 懒加载，注册期不读取 cookery 的效果 Holder，因此本类可在 main 阶段调用。
 */
public class ModTea {
    public static Identifier LAPSANG;
    public static Identifier HK_MILK_TEA;

    public static void init() {
        LAPSANG = id("lapsang");
        TeacupRegistry.TEACUP_DATA_MAP.put(
                LAPSANG,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.TUNDRA_STRIDER, 9600), 1.0F)
        );
        HK_MILK_TEA = id("hk_milk_tea");
        TeacupRegistry.TEACUP_DATA_MAP.put(
                HK_MILK_TEA,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SULFUR, 9600), 1.0F)
        );

        registerTeacup(LAPSANG);
        registerTeacup(HK_MILK_TEA);
    }

    private static void registerTeacup(Identifier id) {
        if (BuiltInRegistries.BLOCK.getOptional(id).isPresent()) {
            return; // 防御：避免重复注册
        }
        TeacupData data = TeacupRegistry.TEACUP_DATA_MAP.get(id);
        Properties properties = Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id));
        TeacupBlock block = new TeacupBlock(properties, data.getMaxCount());
        Registry.register(BuiltInRegistries.BLOCK, id, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        TeacupItem item = new TeacupItem(block, data.getEffects(),
                new Item.Properties().stacksTo(16).useBlockDescriptionPrefix().setId(itemKey));
        item.registerBlocks(Item.BY_BLOCK, item);
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }

    public static Identifier id(String name) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeChineseFood.MODID, name);
    }
}
