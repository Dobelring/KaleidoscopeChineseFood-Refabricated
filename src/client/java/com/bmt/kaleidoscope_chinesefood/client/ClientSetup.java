package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.client.gui.FreezerScreen;
import com.bmt.kaleidoscope_chinesefood.client.renderer.CoupletBlockEntityRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.HorizontalBannerBlockEntityRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.PickleJarRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.entity.KongmingLanternRender;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class ClientSetup {
    public static void init() {
        MenuScreens.register(ModMenuTypes.FREEZER_TOP_MENU, FreezerScreen::new);
        MenuScreens.register(ModMenuTypes.FREEZER_BOTTOM_MENU, FreezerScreen::new);
        BlockEntityRenderers.register(ModBlockEntities.PICKLE_JAR, PickleJarRender::new);
        EntityRenderers.register(ModEntities.FIRECRACKER, ThrownItemRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.COUPLET_BLOCK_ENTITY, CoupletBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlockEntities.HORIZONTAL_BANNER, HorizontalBannerBlockEntityRender::new);
        EntityRenderers.register(ModEntities.KONGMING_LANTERN, KongmingLanternRender::new);
        // 26.1 渲染层自动判定：烘焙期按贴图 alpha 计算每 quad 层（α=0 → cutout），
        // BlockRenderLayerMap 已从 Fabric API 移除，cutout 方块无需（也无法）手动注册
    }
}
