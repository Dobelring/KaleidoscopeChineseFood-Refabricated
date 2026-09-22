package com.bmt.kaleidoscope_chinesefood.integration;

import com.github.ysbbbbbb.kaleidoscopedoll.block.DollBlock;
import com.github.ysbbbbbb.kaleidoscopedoll.event.ModRegisterEvent;
import com.github.ysbbbbbb.kaleidoscopedoll.item.DollItem;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 玩偶联动的实际实现（只有玩偶模组存在时才会被加载）。
 * <p>
 * Forge 版依赖 {@code RegisterEvent}（BLOCK / ITEM 两次）与
 * {@code BuildCreativeModeTabContentsEvent}；Fabric 无 mod 总线，改为在这里一次性注册
 * 方块 + 物品（eager + Registry.register），并把条目写进玩偶模组的三个静态表。
 */
class KaleidoscopeDollIntegrationImpl {
    private static final Map<ResourceLocation, Block> DOLL_BLOCKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Item> DOLL_ITEMS = new LinkedHashMap<>();
    private static final Map<String, String> AUTHOR_DOLL_DEFINITIONS = Map.of(
        "doll_0",
        "contributor_0",
        "doll_1",
        "contributor_1",
        "doll_2",
        "contributor_2",
        "doll_3",
        "contributor_3",
        "doll_4",
        "contributor_4",
        "doll_5",
        "contributor_5"
    );

    KaleidoscopeDollIntegrationImpl() {
    }

    /**
     * 原 Forge 版是 {@code register(IEventBus)} + 两个 {@code @SubscribeEvent(RegisterEvent)}。
     * 注意原方法名 {@code isDollModLoaded()} 的语义是反的（返回「未加载」），此处保留原样。
     */
    static void register() {
        AUTHOR_DOLL_DEFINITIONS.forEach((dollId, tooltipKey) -> {
            ResourceLocation id = new ResourceLocation("kaleidoscope_chinesefood", dollId);

            // 原 registerBlocks(RegisterEvent)：注册方块
            DollBlock block = new DollBlock();
            Registry.register(BuiltInRegistries.BLOCK, id, block);
            DOLL_BLOCKS.put(id, block);

            // 原 registerItems(RegisterEvent)：注册物品
            DollItem item = new DollItem(block, tooltipKey);
            Registry.register(BuiltInRegistries.ITEM, id, item);
            DOLL_ITEMS.put(id, item);

            // 原 Forge：ModRegisterEvent.SPECIAL_TOOLTIPS.put(id, tooltipKey)
            //  Fabric 版玩偶模组的玩偶表由这三个静态表驱动（ModRegisterEvent#registerBlocks 只处理
            //  kaleidoscope_doll:doll_* 自己的玩偶）：
            //   - DOLL_ITEMS + SPECIAL_TOOLTIPS 里都存在的物品 → 进入玩偶的 player_doll 标签页
            //     （等价于原 Forge 往 AUTHOR_DOLL_TAB 里 accept）；
            //   - DOLL_BLOCKS → 玩偶客户端会为这些方块注册 cutout 渲染层，并参与幻影等逻辑。
            ModRegisterEvent.DOLL_BLOCKS.put(id, block);
            ModRegisterEvent.DOLL_ITEMS.add(item);
            ModRegisterEvent.SPECIAL_TOOLTIPS.put(id, tooltipKey);
        });
    }
}
