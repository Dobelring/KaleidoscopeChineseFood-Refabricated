package com.bmt.kaleidoscope_chinesefood.client;

import com.github.ysbbbbbb.kaleidoscopedoll.render.DollEntityItemRender;
import java.util.Collection;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.world.item.Item;

/**
 * 玩偶联动客户端渲染注册实现类。
 * 持有 kaleidoscopedoll 的 {@link DollEntityItemRender} 引用，仅供
 * {@link ClientSetup#registerDollIntegrationClient()} 在确认联动已生效
 * （玩偶模组在场且已注册玩偶）后调用——本类的加载与校验推迟到彼时，
 * 玩偶模组缺席时永不触及，避免 NoClassDefFoundError。
 */
final class DollClientIntegration {
    private DollClientIntegration() {
    }

    static void register(Collection<Item> entityDollItems) {
        DollEntityItemRender renderer = new DollEntityItemRender();
        for (Item item : entityDollItems) {
            BuiltinItemRendererRegistry.INSTANCE.register(item, renderer);
        }
    }
}
