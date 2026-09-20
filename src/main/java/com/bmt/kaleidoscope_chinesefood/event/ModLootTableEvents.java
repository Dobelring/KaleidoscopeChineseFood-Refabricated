package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.LootPoolAccessor;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.LootTableAccessor;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

/**
 * 对应官方的 {@code LootTableEvents}：往既有战利品表的第一个池里追加条目。
 * <p>
 * （类名带 Mod 前缀是为了避开 Fabric API 的同名事件类。）
 * Fabric 侧用 {@link LootTableEvents#REPLACE} 拿到已构建好的 {@link LootTable} 再改，
 * 语义与官方一致；{@code MODIFY} 只能新增独立池，概率分布会不一样。
 */
public class ModLootTableEvents {
    private static final ResourceKey<LootTable> VILLAGE_HIDE_CHEST = key("kaleidoscope_cookery", "chest/village_hide_chest");
    private static final ResourceKey<LootTable> CHEF_GIFT = key("kaleidoscope_cookery", "gameplay/hero_of_the_village/chef_gift");
    private static final ResourceKey<LootTable> FISHING_FISH = key("minecraft", "gameplay/fishing/fish");

    public static void register() {
        LootTableEvents.REPLACE.register((key, original, source, registries) -> {
            if (VILLAGE_HIDE_CHEST.equals(key)) {
                addEntries(
                        original,
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
            } else if (CHEF_GIFT.equals(key)) {
                addEntries(
                        original,
                        weighted(ModItems.TWICE_COOKED_PORK_RICE, 1),
                        weighted(ModItems.STIR_FRIED_YELLOW_BEEF_RICE, 1),
                        weighted(ModItems.BEEF_WITH_SCRAMBLED_EGGS_RICE, 1),
                        weighted(ModItems.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE, 1)
                );
            } else if (FISHING_FISH.equals(key)) {
                addEntries(original, weighted(ModItems.YELLOW_CROAKER, 25));
            }
            return original;
        });
    }

    private static ResourceKey<LootTable> key(String namespace, String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private static LootPoolEntryContainer weighted(Item item, int weight) {
        return LootItem.lootTableItem(item).setWeight(weight).build();
    }

    private static void addEntries(LootTable table, LootPoolEntryContainer... additions) {
        List<LootPool> pools = ((LootTableAccessor) table).getPools();
        if (pools.isEmpty()) {
            return;
        }
        LootPoolAccessor pool = (LootPoolAccessor) pools.get(0);
        List<LootPoolEntryContainer> entries = new ArrayList<>(pool.getEntries());
        entries.addAll(List.of(additions));
        pool.setEntries(entries);
    }
}
