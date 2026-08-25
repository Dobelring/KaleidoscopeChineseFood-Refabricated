package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.minecraft.advancements.criterion.EntityEquipmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * Fabric replacements for the NeoForge data maps / global loot modifiers:
 * - compostables data map (eggplant, eggplant seeds)
 * - "add_grass_drops" loot modifier (eggplant seeds from short/tall grass)
 * <p>
 * 与原版行为对齐（NeoForge loot_modifiers/add_grass_drops，与 26.2 分支一致）：
 * 破坏草丛掉落茄子种子需要非剪刀工具、头戴厨房乐事草帽
 * （#kaleidoscope_cookery:straw_hat，含 straw_hat / straw_hat_flower）、15% 概率。
 */
public class DataMapsEvents {
    private static final Identifier SHORT_GRASS = Identifier.withDefaultNamespace("blocks/short_grass");
    private static final Identifier TALL_GRASS = Identifier.withDefaultNamespace("blocks/tall_grass");
    /** 厨房乐事草帽标签，由 cookery 提供（straw_hat / straw_hat_flower 两顶） */
    private static final TagKey<Item> STRAW_HATS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("kaleidoscope_cookery", "straw_hat"));

    public static void register() {
        CompostableRegistry.INSTANCE.add(ModItems.EGGPLANT, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.EGGPLANT_SEED, 0.3F);

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (source.isBuiltin()) {
                Identifier loc = key.identifier();
                if (SHORT_GRASS.equals(loc) || TALL_GRASS.equals(loc)) {
                    // 26.1 的 ItemPredicate.Builder.of 需要 HolderGetter<Item>，从回调的 registries 获取
                    HolderGetter<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            // 15% 概率
                            .when(LootItemRandomChanceCondition.randomChance(0.15F))
                            // 剪刀剪下的草不掉种子（inverted match_tool shears）
                            .when(InvertedLootItemCondition.invert(
                                    MatchTool.toolMatches(ItemPredicate.Builder.item().of(itemLookup, Items.SHEARS))))
                            // 头戴厨房乐事草帽才掉落（entity_properties equipment.head 匹配标签）
                            .when(LootItemEntityPropertyCondition.hasProperties(
                                    LootContext.EntityTarget.THIS,
                                    EntityPredicate.Builder.entity()
                                            .equipment(EntityEquipmentPredicate.Builder.equipment()
                                                    .head(ItemPredicate.Builder.item().of(itemLookup, STRAW_HATS)))
                                            .build()))
                            .when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(ModItems.EGGPLANT_SEED)));
                }
            }
        });
    }
}
