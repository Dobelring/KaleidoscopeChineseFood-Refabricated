package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry.TeacupData;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeacupItem;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/**
 * 茶杯注册：数据（TeacupData）+ 方块/物品，全部自管。
 * <p>
 * 严禁把 TeacupData 塞进 cookery 的 TeacupRegistry.TEACUP_DATA_MAP：
 * cookery 1.4 的 CommonRegistry.registerTeacupBlocks 会遍历该 map 为所有条目代注册，
 * 但 setId 用 PortHelper.createItemId(path) 硬拼 cookery 命名空间——第三方条目的物品
 * 会被登记上 cookery:\<path\> 的数据组件回放 key 而实际注册在本模组命名空间，
 * 26.1 资源重载期 DataComponentInitializers.getOrThrow 直接炸世界加载（验收轮 3 实锤）。
 * <p>
 * TeacupBlock/TeacupItem 构造后自持数据（maxCount/effects），不回查注册表 map，
 * 因此数据放本类的 map 即可。茶杯展示在国味创造栏（ModCreativeModeTabs.acceptTeaIfRegistered）。
 */
public class ModTea {
    private static final Map<Identifier, TeacupData> DATA_MAP = new LinkedHashMap<>();
    public static Identifier LAPSANG;
    public static Identifier HK_MILK_TEA;

    public static void init() {
        LAPSANG = id("lapsang");
        DATA_MAP.put(LAPSANG,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.TUNDRA_STRIDER, 9600), 1.0F));
        HK_MILK_TEA = id("hk_milk_tea");
        DATA_MAP.put(HK_MILK_TEA,
                TeacupData.create(4).addEffect(() -> new MobEffectInstance(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.SULFUR, 9600), 1.0F));
    }

    /** 在 food phase 调用：此时 cookery 的效果 Holder 已就绪 */
    public static void registerTeacupBlocksAndItems() {
        registerTeacup(LAPSANG);
        registerTeacup(HK_MILK_TEA);
    }

    private static void registerTeacup(Identifier id) {
        if (BuiltInRegistries.BLOCK.getOptional(id).isPresent()) {
            return; // 防御：避免重复注册
        }
        TeacupData data = DATA_MAP.get(id);
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
