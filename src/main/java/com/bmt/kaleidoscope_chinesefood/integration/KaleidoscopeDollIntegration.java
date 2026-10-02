package com.bmt.kaleidoscope_chinesefood.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * 森罗物语：玩偶（kaleidoscope_doll）联动门面，与原版 1.1.10 语义一致：
 * 仅当玩偶模组在场、且下界/名酒同族模组都不在场时，把 6 个贡献者玩偶与 6 个实体玩偶
 * 以本模组命名空间注册（复用玩偶模组的 DollBlock/DollItem/DollEntityItem 类），
 * 登记 ModRegisterEvent.SPECIAL_TOOLTIPS 使玩偶模组显示贡献者署名，
 * 并加入玩偶模组的"作者玩偶/实体玩偶"创造栏。
 *
 * <p>与官方 Forge/NeoForge 一致拆分为门面 + 实现两类：玩偶类（DollBlock/DollItem/
 * DollEntityItem）只出现在 {@link KaleidoscopeDollIntegrationImpl}，守卫通过后才首次
 * 加载该类。若两类合一，JVM 在调用本类方法前会校验全部方法体，玩偶模组缺席时将因
 * NoClassDefFoundError 在调用点直接崩溃，守卫永远无法生效。
 */
public class KaleidoscopeDollIntegration {
    private static final Map<ResourceLocation, Block> DOLL_BLOCKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Item> DOLL_ITEMS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Item> ENTITY_DOLL_ITEMS = new LinkedHashMap<>();

    private KaleidoscopeDollIntegration() {
    }

    public static void register() {
        FabricLoader loader = FabricLoader.getInstance();
        // 与原版一致：doll 在场且 nether/liquor 都不在场才注册（避免同族模组互相挤占贡献者玩偶）
        if (!loader.isModLoaded("kaleidoscope_doll")
                || loader.isModLoaded("kaleidoscope_nether")
                || loader.isModLoaded("kaleidoscope_world_liquor")) {
            return;
        }

        KaleidoscopeDollIntegrationImpl.register(DOLL_BLOCKS, DOLL_ITEMS, ENTITY_DOLL_ITEMS);
    }

    /** 联动是否生效（客户端用：注册实体玩偶的动态渲染器） */
    public static boolean isIntegrated() {
        return !DOLL_BLOCKS.isEmpty();
    }

    /** 联动注册的实体玩偶物品（客户端用） */
    public static Collection<Item> getEntityDollItems() {
        return new ArrayList<>(ENTITY_DOLL_ITEMS.values());
    }
}
