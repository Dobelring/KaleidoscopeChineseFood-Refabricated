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
import com.bmt.kaleidoscope_chinesefood.init.ModPlateRegistry;
import com.bmt.kaleidoscope_chinesefood.init.ModTea;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.MenuScreens;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ClientSetup {
    public static void init() {
        MenuScreens.register(ModMenuTypes.FREEZER_TOP_MENU, FreezerScreen::new);
        MenuScreens.register(ModMenuTypes.FREEZER_BOTTOM_MENU, FreezerScreen::new);
        BlockEntityRenderers.register(ModBlockEntities.PICKLE_JAR, PickleJarRender::new);
        EntityRendererRegistry.register(ModEntities.FIRECRACKER, ThrownItemRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.COUPLET_BLOCK_ENTITY, CoupletBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlockEntities.HORIZONTAL_BANNER, HorizontalBannerBlockEntityRender::new);
        EntityRendererRegistry.register(ModEntities.KONGMING_LANTERN, KongmingLanternRender::new);
        registerBlockRenderLayers();
    }

    // 原 NeoForge 版通过模型 JSON 的 "render_type" 声明渲染层，该字段是 Forge/NeoForge 补丁特性，
    // Fabric 1.21.1 的原版模型加载器会静默忽略它；若不在客户端注册 BlockRenderLayerMap，
    // 作物/菜肴等需要 cutout 的方块会落在 SOLID 层，透明像素被渲染成不透明底色（如茄子作物出现灰色十字）。
    private static void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlocks(
                RenderType.cutout(),
                ModBlocks.EGGPLANT_CROP,
                ModBlocks.CORN_RISTRA,
                ModBlocks.BOWL_STACK,
                ModBlocks.SALT_BLOCK,
                ModBlocks.FIRECRACKER,
                ModBlocks.FU_CHARACTER,
                ModBlocks.KONGMING_LANTERN
        );

        // 菜肴 / 盘子 / 茶杯 / 人偶方块由数据注册表驱动生成，按 id 从注册表反查后注册
        List<ResourceLocation> cutoutIds = new ArrayList<>(List.of(
                ModFoodBiteRegistry.SICHUAN_BOILED_PORK_SLICES,
                ModFoodBiteRegistry.SICHUAN_BOILED_FISH,
                ModFoodBiteRegistry.YELLOW_CROAKER_SOUP,
                ModFoodBiteRegistry.RED_RICE_ROLL,
                ModFoodBiteRegistry.YELLOW_CROAKER_TOFU_SOUP,
                ModPlateRegistry.GOLDEN_APPLE_PLATTER,
                ModTea.LAPSANG,
                ModTea.HK_MILK_TEA
        ));
        for (int i = 0; i <= 5; i++) {
            cutoutIds.add(KaleidoscopeChineseFood.id("doll_" + i));
        }
        for (ResourceLocation id : cutoutIds) {
            Block block = BuiltInRegistries.BLOCK.get(id);
            if (block != Blocks.AIR) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
            }
        }
    }
}
