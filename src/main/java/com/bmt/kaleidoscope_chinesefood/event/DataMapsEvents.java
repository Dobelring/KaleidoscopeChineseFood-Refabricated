package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * Fabric replacements for the NeoForge data maps / global loot modifiers:
 * - compostables data map (eggplant, eggplant seeds)
 * - "add_grass_drops" loot modifier (eggplant seeds from short/tall grass)
 */
public class DataMapsEvents {
    private static final Identifier SHORT_GRASS = Identifier.withDefaultNamespace("blocks/short_grass");
    private static final Identifier TALL_GRASS = Identifier.withDefaultNamespace("blocks/tall_grass");

    public static void register() {
        CompostableRegistry.INSTANCE.add(ModItems.EGGPLANT, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.EGGPLANT_SEED, 0.3F);

        LootTableEvents.MODIFY.register((key, tableBuilder, source, conditions) -> {
            if (source.isBuiltin()) {
                Identifier loc = key.identifier();
                if (SHORT_GRASS.equals(loc) || TALL_GRASS.equals(loc)) {
                    tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(LootItemRandomChanceCondition.randomChance(0.15F))
                            .add(LootItem.lootTableItem(ModItems.EGGPLANT_SEED)));
                }
            }
        });
    }
}
