package com.bmt.kaleidoscope_chinesefood;

import com.bmt.kaleidoscope_chinesefood.config.ModConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;

/**
 * 内置资源包 / 数据包注册。
 * <p>
 * 原 Forge 走 {@code AddPackFindersEvent}（mod 总线）从模组自身 jar 里挂载两个包；
 * Fabric 侧用 {@code ResourceManagerHelper.registerBuiltinResourcePack}。
 * 该 API 固定把包目录解析为 {@code resourcepacks/<id path>}，因此：
 * <ul>
 *   <li>{@code old_fridge} 在 {@code resourcepacks/old_fridge}，正好符合约定，用新 API；</li>
 *   <li>{@code fuzzy_cooking_recipes} 在 {@code datapacks/} 下，只能用带显式 subPath 的重载
 *       （已标注 deprecated，但仍是官方公开 API，避免直接调 impl 类）。</li>
 * </ul>
 */
public class ModBuiltInResourcePacks {
    public ModBuiltInResourcePacks() {
    }

    /**
     * 读取配置开关。入口点阶段配置可能尚未加载完成（Forge Config API Port 的 spec
     * 在 register 之后才 build），此时按默认值 true 处理，与官方行为一致。
     */
    private static boolean isCustomPacksEnabled() {
        try {
            return ModConfig.ENABLE_CUSTOM_PACKS.get();
        } catch (IllegalStateException | NullPointerException e) {
            return true;
        }
    }

    public static void register() {
        ModContainer container = FabricLoader.getInstance().getModContainer(KaleidoscopeChineseFood.MODID).orElse(null);
        if (container == null) {
            return;
        }

        // 原：PackType.CLIENT_RESOURCES / required=false / PackSource.BUILT_IN
        ResourceManagerHelper.registerBuiltinResourcePack(
            KaleidoscopeChineseFood.id("old_fridge"),
            container,
            Component.translatable("resourcepack.kaleidoscope_chinesefood.old_fridge"),
            ResourcePackActivationType.NORMAL
        );

        // 原：PackType.SERVER_DATA / required=true / PackSource.WORLD，受配置开关控制。
        // 差异：Fabric 这条重载只提供 NORMAL/DEFAULT_ENABLED，没有「强制启用」档，
        // 因此语义取「默认启用」而非「不可关闭」。
        if (isCustomPacksEnabled()) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                KaleidoscopeChineseFood.id("fuzzy_cooking_recipes"),
                "datapacks/fuzzy_cooking_recipes",
                container,
                true
            );
        }
    }
}
