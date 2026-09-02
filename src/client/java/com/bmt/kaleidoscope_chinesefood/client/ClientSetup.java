package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.client.gui.FreezerScreen;
import com.bmt.kaleidoscope_chinesefood.client.renderer.CoupletBlockEntityRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.HorizontalBannerBlockEntityRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.PickleJarRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.entity.KongmingLanternRender;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModFoodBiteRegistry;
import com.bmt.kaleidoscope_chinesefood.init.ModMenuTypes;
import com.bmt.kaleidoscope_chinesefood.init.ModTea;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.MenuScreens;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ClientSetup {
    public static void init() {
        MenuScreens.register(ModMenuTypes.FREEZER_TOP_MENU, FreezerScreen::new);
        MenuScreens.register(ModMenuTypes.FREEZER_BOTTOM_MENU, FreezerScreen::new);
        BlockEntityRenderers.register(ModBlockEntities.PICKLE_JAR, PickleJarRender::new);
        EntityRenderers.register(ModEntities.FIRECRACKER, ThrownItemRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.COUPLET_BLOCK_ENTITY, CoupletBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlockEntities.HORIZONTAL_BANNER, HorizontalBannerBlockEntityRender::new);
        EntityRenderers.register(ModEntities.KONGMING_LANTERN, KongmingLanternRender::new);
        registerBlockRenderLayers();
    }

    // 需要 cutout 渲染的方块通过 BlockRenderLayerMap 注册到 CUTOUT 层，避免透明像素渲染成不透明底色
    private static void registerBlockRenderLayers() {
        BlockRenderLayerMap.putBlocks(
                ChunkSectionLayer.CUTOUT,
                ModBlocks.EGGPLANT_CROP,
                ModBlocks.CORN_RISTRA,
                ModBlocks.BOWL_STACK,
                ModBlocks.SALT_BLOCK,
                ModBlocks.FIRECRACKER,
                ModBlocks.FU_CHARACTER,
                ModBlocks.KONGMING_LANTERN
        );

        // 菜肴 / 盘子 / 茶杯 / 人偶方块由数据注册表驱动生成，按 id 从注册表反查后注册
        List<Identifier> cutoutIds = new ArrayList<>(List.of(
                ModFoodBiteRegistry.SICHUAN_BOILED_PORK_SLICES,
                ModFoodBiteRegistry.SICHUAN_BOILED_FISH,
                ModFoodBiteRegistry.YELLOW_CROAKER_SOUP,
                ModFoodBiteRegistry.RED_RICE_ROLL,
                ModFoodBiteRegistry.YELLOW_CROAKER_TOFU_SOUP,
                ModTea.LAPSANG,
                ModTea.HK_MILK_TEA
        ));
        for (int i = 0; i <= 5; i++) {
            cutoutIds.add(KaleidoscopeChineseFood.id("doll_" + i));
        }
        for (Identifier id : cutoutIds) {
            Block block = BuiltInRegistries.BLOCK.getValue(id);
            if (block != Blocks.AIR) {
                BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
            }
        }
    }
}
