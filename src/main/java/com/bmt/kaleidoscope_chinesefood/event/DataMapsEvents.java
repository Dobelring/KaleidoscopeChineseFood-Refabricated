package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.advancements.criterion.EntityEquipmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
    private static final Identifier SHORT_GRASS = Identifier.withDefaultNamespace("blocks/short_grass");
    private static final Identifier TALL_GRASS = Identifier.withDefaultNamespace("blocks/tall_grass");

    public static void register() {
        CompostingChanceRegistry.INSTANCE.add(ModItems.EGGPLANT, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.EGGPLANT_SEED, 0.3F);

        LootTableEvents.MODIFY.register((key, tableBuilder, source, conditions) -> {
            if (source.isBuiltin()) {
                Identifier loc = key.identifier();
                if (SHORT_GRASS.equals(loc) || TALL_GRASS.equals(loc)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            // 15% 概率
                            .when(LootItemRandomChanceCondition.randomChance(0.15F))
                            // 剪刀剪下的草不掉种子（原版 inverted match_tool shears）
                            .when(InvertedLootItemCondition.invert(
                                    MatchTool.toolMatches(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM, Items.SHEARS))))
                            // 头戴厨房乐事草帽才掉落（原版 advance_block_match_tool slot=head）
                            // 1.21.11: ItemPredicate 的 TagKey 重载会在战利品表加载阶段急切解析标签，
                            // 此时物品标签尚未绑定，会抛 Missing tag 并卡死世界加载；
                            // 该标签内容就是两顶草帽，故直接按物品匹配，语义完全等价。
                            .when(LootItemEntityPropertyCondition.hasProperties(
                                    LootContext.EntityTarget.THIS,
                                    EntityPredicate.Builder.entity()
                                            .equipment(EntityEquipmentPredicate.Builder.equipment()
                                                    .head(ItemPredicate.Builder.item().of(BuiltInRegistries.ITEM,
                                                            com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.STRAW_HAT,
                                                            com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.STRAW_HAT_FLOWER)))
                                            .build()))
                            .when(ExplosionCondition.survivesExplosion())
                            .add(LootItem.lootTableItem(ModItems.EGGPLANT_SEED)));
                }
            }
        });
    }
}
