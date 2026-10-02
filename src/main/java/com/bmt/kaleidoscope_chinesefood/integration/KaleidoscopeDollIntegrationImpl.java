package com.bmt.kaleidoscope_chinesefood.integration;

import com.github.ysbbbbbb.kaleidoscopedoll.block.DollBlock;
import com.github.ysbbbbbb.kaleidoscopedoll.event.ModRegisterEvent;
import com.github.ysbbbbbb.kaleidoscopedoll.item.DollEntityItem;
import com.github.ysbbbbbb.kaleidoscopedoll.item.DollItem;
import java.util.Map;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 森罗物语：玩偶联动实现类。
 * 持有全部 kaleidoscopedoll 类引用，仅供 {@link KaleidoscopeDollIntegration#register()}
 * 在确认玩偶模组已加载、且 nether/liquor 均不在场后调用——本类的加载与校验推迟到彼时，
 * 玩偶模组缺席时永不触及。
 */
class KaleidoscopeDollIntegrationImpl {
    private static final Map<String, String> DOLL_DEFINITIONS = Map.of(
            "doll_0", "contributor_0",
            "doll_1", "contributor_1",
            "doll_2", "contributor_2",
            "doll_3", "contributor_3",
            "doll_4", "contributor_4",
            "doll_5", "contributor_5"
    );

    private KaleidoscopeDollIntegrationImpl() {
    }

    static void register(Map<ResourceLocation, Block> dollBlocks,
                         Map<ResourceLocation, Item> dollItems,
                         Map<ResourceLocation, Item> entityDollItems) {
        DOLL_DEFINITIONS.forEach((dollId, tooltipKey) -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", dollId);
            DollBlock block = new DollBlock();
            dollBlocks.put(id, block);
            Registry.register(BuiltInRegistries.BLOCK, id, block);
            ModRegisterEvent.SPECIAL_TOOLTIPS.put(id, tooltipKey);
            DollItem item = new DollItem(block, tooltipKey);
            dollItems.put(id, item);
            Registry.register(BuiltInRegistries.ITEM, id, item);

            ResourceLocation entityDollId = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", "entity_" + dollId);
            Item entityDollItem = Registry.register(BuiltInRegistries.ITEM, entityDollId, new DollEntityItem());
            entityDollItems.put(id, entityDollItem);
        });

        // 玩偶模组的两个创造栏内容是注册时固定的 lambda（只扫它自己的 DOLL_ITEMS/DOLL_BLOCKS），
        // 联动物品不会被自动收录，需要用 ItemGroupEvents 手动加入（等价原版 BuildCreativeModeTabContentsEvent）
        ItemGroupEvents.modifyEntriesEvent(playerDollTabKey()).register(entries ->
                dollItems.values().forEach(entries::accept));
        ItemGroupEvents.modifyEntriesEvent(entityDollTabKey()).register(entries ->
                entityDollItems.forEach((blockId, entityDollItem) -> {
                    Block block = dollBlocks.get(blockId);
                    if (block != null) {
                        entries.accept(DollEntityItem.createItemWithBlockState(block.defaultBlockState()));
                    }
                }));
    }

    private static ResourceKey<CreativeModeTab> playerDollTabKey() {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath("kaleidoscope_doll", "player_doll"));
    }

    private static ResourceKey<CreativeModeTab> entityDollTabKey() {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath("kaleidoscope_doll", "entity_doll"));
    }
}
