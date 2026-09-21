package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.LootPoolAccessor;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.LootTableAccessor;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

/**
 * 往既有战利品表里追加国味条目（对应官方的 {@code LootTableEvents}）。
 * <p>
 * 用 Fabric 的 {@code LootTableEvents.REPLACE}（拿得到已构建的 {@link LootTable} 本体）直接改 pool 0 的
 * entries——语义与官方一致。不能用 {@code MODIFY}：它只能 withPool 加一个【独立池】，
 * 概率语义与"往原池追加"不同。
 * <p>
 * 类名不能叫 LootTableEvents（与 Fabric API 同名类冲突）。
 */
public final class ModLootTableEvents {
    private static final Identifier VILLAGE_HIDE_CHEST =
            Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "chest/village_hide_chest");
    private static final Identifier CHEF_GIFT =
            Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "gameplay/hero_of_the_village/chef_gift");
    private static final Identifier FISHING_FISH =
            Identifier.fromNamespaceAndPath("minecraft", "gameplay/fishing/fish");

    private ModLootTableEvents() {
    }

    public static void register() {
        LootTableEvents.REPLACE.register((key, original, source, registries) -> {
            Identifier id = key.identifier();
            if (id.equals(VILLAGE_HIDE_CHEST)) {
                return addEntries(original,
                        weighted(ModItems.WONTON_NOODLES, 10),
                        weighted(ModItems.SEAWEED_EGG_DROP_SOUP, 10),
                        weighted(ModItems.TOMATO_EGG_DROP_SOUP, 10),
                        weighted(ModItems.CENTURY_EGG_CONGEE, 10),
                        weighted(ModItems.PUMPKIN_PORRIDGE, 10),
                        weighted(ModItems.SAUERKRAUT_BEEF_NOODLES, 10),
                        weighted(com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.LAMB_AND_RADISH_SOUP, 10),
                        weighted(com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.BEEF_NOODLE, 10),
                        weighted(com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.HUI_NOODLE, 10),
                        weighted(com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.UDON_NOODLE, 10),
                        weighted(ModItems.YANGROU_PAOMO, 10)
                );
            }
            if (id.equals(CHEF_GIFT)) {
                return addEntries(original,
                        weighted(ModItems.TWICE_COOKED_PORK_RICE, 1),
                        weighted(ModItems.STIR_FRIED_YELLOW_BEEF_RICE, 1),
                        weighted(ModItems.BEEF_WITH_SCRAMBLED_EGGS_RICE, 1),
                        weighted(ModItems.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE, 1)
                );
            }
            if (id.equals(FISHING_FISH)) {
                return addEntries(original, weighted(ModItems.YELLOW_CROAKER, 25));
            }
            // 没改的表必须返回 null。REPLACE 一旦返回非 null，Fabric 就把该表的 source 记成
            // REPLACED（LootTableSource.REPLACED.isBuiltin() == false），此后所有以
            // source.isBuiltin() 为前提的 MODIFY 全部静默跳过——草地掉茄子种子就是这么失效的。
            return null;
        });
    }

    private static LootPoolEntryContainer weighted(ItemLike item, int weight) {
        return LootItem.lootTableItem(item).setWeight(weight).build();
    }

    /** 往第一号池的 entries 末尾追加（与官方一致），原表没有池时原样返回。 */
    private static LootTable addEntries(LootTable table, LootPoolEntryContainer... additions) {
        List<LootPool> pools = ((LootTableAccessor) table).kaleidoscope_chinesefood$getPools();
        if (pools.isEmpty()) {
            return table;
        }
        LootPoolAccessor pool = (LootPoolAccessor) pools.get(0);
        List<LootPoolEntryContainer> entries = new ArrayList<>(pool.kaleidoscope_chinesefood$getEntries());
        for (LootPoolEntryContainer addition : additions) {
            entries.add(addition);
        }
        pool.kaleidoscope_chinesefood$setEntries(entries);
        return table;
    }
}
