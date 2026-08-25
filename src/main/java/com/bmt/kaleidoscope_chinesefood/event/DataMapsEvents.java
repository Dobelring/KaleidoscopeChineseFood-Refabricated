package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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
 * 与原版行为对齐（NeoForge loot_modifiers/add_grass_drops + blocks/grass_extra_drops）：
 * 破坏草丛掉落茄子种子需要：非剪刀工具、头戴厨房乐事草帽、15% 概率。
 */
public class DataMapsEvents {
    private static final ResourceLocation SHORT_GRASS = ResourceLocation.withDefaultNamespace("blocks/short_grass");
    private static final ResourceLocation TALL_GRASS = ResourceLocation.withDefaultNamespace("blocks/tall_grass");
    /** 厨房乐事草帽标签（straw_hat / straw_hat_flower），等价于原版条件里的两顶草帽 */
    private static final TagKey<Item> STRAW_HATS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("kaleidoscope_cookery", "straw_hat"));

    public static void register() {
        CompostingChanceRegistry.INSTANCE.add(ModItems.EGGPLANT, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.EGGPLANT_SEED, 0.3F);

        LootTableEvents.MODIFY.register((key, tableBuilder, source, conditions) -> {
            if (source.isBuiltin()) {
                ResourceLocation loc = key.location();
                if (SHORT_GRASS.equals(loc) || TALL_GRASS.equals(loc)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            // 15% 概率
                            .when(LootItemRandomChanceCondition.randomChance(0.15F))
                            // 剪刀剪下的草不掉种子（原版 inverted match_tool shears）
                            .when(InvertedLootItemCondition.invert(
                                    MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS))))
                            // 头戴厨房乐事草帽才掉落（原版 advance_block_match_tool slot=head）
                            .when(LootItemEntityPropertyCondition.hasProperties(
                                    LootContext.EntityTarget.THIS,
                                    EntityPredicate.Builder.entity()
                                            .equipment(EntityEquipmentPredicate.Builder.equipment()
                                                    .head(ItemPredicate.Builder.item().of(STRAW_HATS)))
                                            .build()))
                            .when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(ModItems.EGGPLANT_SEED)));
                }
            }
        });
    }
}
