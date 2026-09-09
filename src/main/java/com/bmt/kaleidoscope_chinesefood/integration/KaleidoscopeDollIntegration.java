package com.bmt.kaleidoscope_chinesefood.integration;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.DollBlock;
import com.bmt.kaleidoscope_chinesefood.item.DollItem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * 贡献者玩偶联动（1.21.11 无 kaleidoscope_doll 模组，国味自实现，与 liquor 1.21.11 同方案）：
 * doll_0..5 以 kaleidoscope_chinesefood 命名空间无条件注册（贡献者收藏品，无合成配方）。
 * 原版 1.1.10 的"仅 doll 在场且无 nether/liquor 时注册"选择器与 Impl 内部的
 * "doll 在场即跳过"守卫互相矛盾（死代码），故此处不复刻条件。
 * 作者名 tooltip 键 contributor_0..5 在国味 lang 中已有。
 */
public final class KaleidoscopeDollIntegration {
    /** 注册后的全部玩偶方块（客户端渲染层注册用） */
    public static final List<Block> DOLL_BLOCKS = new ArrayList<>();

    private KaleidoscopeDollIntegration() {
    }

    public static void register() {
        Map<String, String> authorTooltips = new LinkedHashMap<>();
        authorTooltips.put("doll_0", "tooltip.kaleidoscope_doll.doll.contributor_0");
        authorTooltips.put("doll_1", "tooltip.kaleidoscope_doll.doll.contributor_1");
        authorTooltips.put("doll_2", "tooltip.kaleidoscope_doll.doll.contributor_2");
        authorTooltips.put("doll_3", "tooltip.kaleidoscope_doll.doll.contributor_3");
        authorTooltips.put("doll_4", "tooltip.kaleidoscope_doll.doll.contributor_4");
        authorTooltips.put("doll_5", "tooltip.kaleidoscope_doll.doll.contributor_5");

        for (Map.Entry<String, String> entry : authorTooltips.entrySet()) {
            String name = entry.getKey();
            Identifier blockId = KaleidoscopeChineseFood.id(name);
            Block block = Registry.register(BuiltInRegistries.BLOCK, blockId,
                    new DollBlock(BlockBehaviour.Properties.of()
                            .setId(ResourceKey.create(Registries.BLOCK, blockId))
                            .mapColor(MapColor.COLOR_LIGHT_GRAY)
                            .strength(0.0F, 10.0F)
                            .sound(SoundType.WOOL)
                            .pushReaction(PushReaction.DESTROY)
                            .noOcclusion()));
            DOLL_BLOCKS.add(block);
            Item item = new DollItem(block, entry.getValue(),
                    new Item.Properties().useBlockDescriptionPrefix().setId(ResourceKey.create(Registries.ITEM, blockId)));
            ((BlockItem) item).registerBlocks(Item.BY_BLOCK, item);
            Registry.register(BuiltInRegistries.ITEM, blockId, item);
        }
    }
}
