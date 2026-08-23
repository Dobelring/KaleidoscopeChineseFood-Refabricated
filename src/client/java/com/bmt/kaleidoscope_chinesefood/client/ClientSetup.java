package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.client.gui.FreezerScreen;
import com.bmt.kaleidoscope_chinesefood.client.renderer.CoupletBlockEntityRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.HorizontalBannerBlockEntityRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.PickleJarRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.entity.KongmingLanternRender;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModMenuTypes;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class ClientSetup {
    public static void init() {
        MenuScreens.register(ModMenuTypes.FREEZER_TOP_MENU, FreezerScreen::new);
        MenuScreens.register(ModMenuTypes.FREEZER_BOTTOM_MENU, FreezerScreen::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.PICKLE_JAR, PickleJarRender::new);
        EntityRendererRegistry.register(ModEntities.FIRECRACKER, ThrownItemRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.COUPLET_BLOCK_ENTITY, CoupletBlockEntityRender::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.HORIZONTAL_BANNER, HorizontalBannerBlockEntityRender::new);
        EntityRendererRegistry.register(ModEntities.KONGMING_LANTERN, KongmingLanternRender::new);
    }
}
