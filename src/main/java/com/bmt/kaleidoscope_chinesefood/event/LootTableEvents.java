package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.LootPoolAccessor;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.LootTableAccessor;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceBlockMatchTool;
import com.google.common.collect.ObjectArrays;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * 战利品表追加。
 * <p>
 * 原 Forge 用 {@code LootTableLoadEvent} + 两个 accessor mixin，把加权条目拼进目标表
 * <b>第一个 pool 的 entries 数组</b>（与原条目竞争同一次 roll）。
 * Fabric 侧用 {@link LootTableEvents#ALL_LOADED} 拿到构建完成的 {@link LootDataManager}，
 * 再用同一对 accessor 对已建好的 {@link LootTable} 做同样的原地追加，保持与官方一致的概率语义。
 * <p>
 * 草方块掉茄子种子官方走 Forge 的 global loot modifier（{@code data/forge/loot_modifiers/}）。
 * 这条路在 Fabric 上走不通：Porting Lib 的 GLM 管理器按资源路径读取，而 cookery 与本模组
 * <b>各自都提供同路径的 {@code global_loot_modifiers.json}</b>，合并视图里只剩一份——
 * 实测本模组那份整份丢失（剥离掉落表条件后 {@code /loot insert} 仍是 0 个物品），modifier 从未被加载。
 * 因此改成代码侧实现，语义与官方 GLM 等价：目标 {@code minecraft:blocks/grass} 与
 * {@code blocks/tall_grass}（注意 1.20.1 里短草叫 {@code grass}，{@code short_grass} 是 1.20.3+），
 * 掉茄子种子，条件为 15% 概率、非剪刀、头部戴草帽、不被爆炸破坏，并带时运加成。
 */
public class LootTableEvents {
    private static final ResourceLocation VILLAGE_HIDE_CHEST = new ResourceLocation("kaleidoscope_cookery", "chest/village_hide_chest");
    private static final ResourceLocation CHEF_GIFT = new ResourceLocation("kaleidoscope_cookery", "gameplay/hero_of_the_village/chef_gift");
    private static final ResourceLocation FISHING_FISH = new ResourceLocation("minecraft", "gameplay/fishing/fish");
    private static final ResourceLocation SHORT_GRASS = new ResourceLocation("minecraft", "blocks/grass");
    private static final ResourceLocation TALL_GRASS = new ResourceLocation("minecraft", "blocks/tall_grass");

    /** 官方的草地茄子种子掉落概率。 */
    private static final float EGGPLANT_SEED_CHANCE = 0.15F;

    public LootTableEvents() {
    }

    public static void register() {
        net.fabricmc.fabric.api.loot.v2.LootTableEvents.ALL_LOADED.register((resourceManager, lootManager) -> {
            addEntries(
                lootManager,
                VILLAGE_HIDE_CHEST,
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
            addEntries(
                lootManager,
                CHEF_GIFT,
                weighted(ModItems.TWICE_COOKED_PORK_RICE, 1),
                weighted(ModItems.STIR_FRIED_YELLOW_BEEF_RICE, 1),
                weighted(ModItems.BEEF_WITH_SCRAMBLED_EGGS_RICE, 1),
                weighted(ModItems.STIR_FRIED_THREE_FRESH_VEGETABLES_RICE, 1)
            );
            addEntries(lootManager, FISHING_FISH, weighted(ModItems.YELLOW_CROAKER, 25));

            // 镰刀扫草掉茄子种子（替代 Forge 的 GLM）
            addPool(lootManager, SHORT_GRASS, eggplantSeedPool());
            addPool(lootManager, TALL_GRASS, eggplantSeedPool());
        });
    }

    /** 原 weighted(RegistryObject&lt;Item&gt; item, int weight)；Fabric 侧物品是 eager 字段。 */
    private static LootPoolEntryContainer weighted(Item item, int weight) {
        return LootItem.lootTableItem(item).setWeight(weight).build();
    }

    /** 原 addEntries(LootTableLoadEvent event, LootPoolEntryContainer... additions)：拼进第一个 pool。 */
    private static void addEntries(LootDataManager lootManager, ResourceLocation id, LootPoolEntryContainer... additions) {
        LootTable table = lootManager.getLootTable(id);
        if (table == null) {
            return;
        }

        LootPool[] pools = ((LootTableAccessor) table).getPools();
        if (pools.length == 0) {
            return;
        }

        LootPoolAccessor pool = (LootPoolAccessor) pools[0];
        pool.setEntries(ObjectArrays.concat(pool.getEntries(), additions, LootPoolEntryContainer.class));
    }

    /** 追加一个独立 pool（草地茄子种子）。 */
    private static void addPool(LootDataManager lootManager, ResourceLocation id, LootPool pool) {
        LootTable table = lootManager.getLootTable(id);
        if (table == null) {
            return;
        }

        LootTableAccessor accessor = (LootTableAccessor) table;
        accessor.setPools(ObjectArrays.concat(accessor.getPools(), pool));
    }

    /**
     * 与官方 {@code grass_eggplant_drop} 掉落表等价的 pool：15% 概率、非剪刀、
     * 头部为草帽（用 cookery 的槽位条件，与它自己的种子掉落一致）、不被爆炸破坏，并带时运加成。
     */
    private static LootPool eggplantSeedPool() {
        Item strawHat = BuiltInRegistries.ITEM.get(new ResourceLocation("kaleidoscope_cookery", "straw_hat"));
        Item strawHatFlower = BuiltInRegistries.ITEM.get(new ResourceLocation("kaleidoscope_cookery", "straw_hat_flower"));
        ItemPredicate hatPredicate = ItemPredicate.Builder.item().of(strawHat, strawHatFlower).build();

        return LootPool.lootPool()
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(ModItems.EGGPLANT_SEED))
            .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2))
            .when(LootItemRandomChanceCondition.randomChance(EGGPLANT_SEED_CHANCE))
            .when(InvertedLootItemCondition.invert(MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS))))
            .when(AdvanceBlockMatchTool.toolMatches(EquipmentSlot.HEAD, hatPredicate))
            .when(ExplosionCondition.survivesExplosion())
            .build();
    }
}
