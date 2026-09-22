package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.client.event.TooltipEvents;
import com.bmt.kaleidoscope_chinesefood.client.gui.FreezerScreen;
import com.bmt.kaleidoscope_chinesefood.client.renderer.CoupletRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.CustomRenderType;
import com.bmt.kaleidoscope_chinesefood.client.renderer.EnchantedPlateBlockEntityRenderer;
import com.bmt.kaleidoscope_chinesefood.client.renderer.HorizontalBannerRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.PickleJarRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.entity.KongmingLanternRender;
import com.bmt.kaleidoscope_chinesefood.client.renderer.entity.YellowCroakerRenderer;
import com.bmt.kaleidoscope_chinesefood.compat.ponder.init.ClientSetupEvent;
import com.bmt.kaleidoscope_chinesefood.config.ModConfigScreenHandler;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModMenuTypes;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.RenderBuffersAccessor;
import com.bmt.kaleidoscope_chinesefood.network.ClientPacketHandler;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.SortedMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

/**
 * 客户端入口点。
 * <p>
 * 原 Forge 的 {@code client.ModClientEvents} / {@code client.ModScreens} 两个
 * {@code @EventBusSubscriber} 类的注册内容全部合并到这里（Fabric 无 mod 总线事件）。
 */
@Environment(EnvType.CLIENT)
public class KaleidoscopeChineseFoodClient implements ClientModInitializer {
    public KaleidoscopeChineseFoodClient() {
    }

    @Override
    public void onInitializeClient() {
        // 方块实体渲染器（原 RegisterRenderers 事件）
        BlockEntityRenderers.register(ModBlockEntities.PICKLE_JAR, PickleJarRender::new);
        BlockEntityRenderers.register(ModBlockEntities.COUPLET_BLOCK_ENTITY, CoupletRender::new);
        BlockEntityRenderers.register(ModBlockEntities.HORIZONTAL_BANNER, HorizontalBannerRender::new);
        BlockEntityRenderers.register(ModBlockEntities.ENCHANTED_PLATE, EnchantedPlateBlockEntityRenderer::new);

        // 实体渲染器（原 RegisterRenderers 事件）
        EntityRendererRegistry.register(ModEntities.KONGMING_LANTERN, KongmingLanternRender::new);
        EntityRendererRegistry.register(ModEntities.YELLOW_CROAKER, YellowCroakerRenderer::new);
        EntityRendererRegistry.register(ModEntities.FIRECRACKER, ThrownItemRenderer::new);

        // 菜单屏幕（原 ModScreens.onClientSetup / MenuScreens.register）
        // Fabric 1.20.1 由 Fabric Transitive Access Wideners 放开原版 HandledScreens.register
        MenuScreens.register(ModMenuTypes.FREEZER_MENU, FreezerScreen::new);

        // tooltip（原 Forge ItemTooltipEvent）
        TooltipEvents.register();

        // 网络客户端接收器（原 ClientPacketHandler）
        ClientPacketHandler.registerClient();

        // 配置界面（原 Forge ConfigScreenFactory 扩展点）
        ModConfigScreenHandler.register();

        // Ponder 场景注册：原 Forge 是 mod 总线事件，Fabric 必须手工接线
        ClientSetupEvent.onClientSetup();


        // 渲染层：1.20.1 原版 BlockModel 没有 render_type 字段（那是 Forge 补丁），
        // 所以模型里声明的 cutout 在 Fabric 上会被忽略、透明像素渲染成黑。
        // 这里按各模型实际声明的 render_type 逐个登记 Fabric 的方块渲染层。
        applyRenderLayers(CUTOUT_BLOCKS, RenderType.cutout());
        applyRenderLayers(CUTOUT_MIPPED_BLOCKS, RenderType.cutoutMipped());

        // 原 FMLLoadCompleteEvent：把自定义 glint 渲染类型登记进 RenderBuffers 的固定缓冲表
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            SortedMap<RenderType, BufferBuilder> layers = ((RenderBuffersAccessor) client.renderBuffers()).getFixedBuffers();
            layers.put(CustomRenderType.GLINT, new BufferBuilder(CustomRenderType.GLINT.bufferSize()));
        });
    }
    /** 模型声明为 cutout 的方块（含 cookery 依据本模组数据表代注册的菜/茶方块）。 */
    private static final String[] CUTOUT_BLOCKS = {
        "bowl_stack",
        "corn_ristra",
        "dianhong_tea",
        "dry_pot_chicken",
        "dry_pot_potatoes",
        "dry_pot_spare_ribs",
        "eggplant_crop",
        "enchanted_golden_apple_platter",
        "firecracker",
        "fu_character",
        "golden_apple_platter",
        "hk_milk_tea",
        "kongming_lantern",
        "red_rice_roll",
        "salt",
        "sichuan_boiled_fish",
        "sichuan_boiled_pork_slices",
        "yellow_croaker_soup",
        "yellow_croaker_tofu_soup",
    };

    /** 模型声明为 cutout_mipped 的方块（玩偶；未注册时查表得到空气，自动跳过）。 */
    private static final String[] CUTOUT_MIPPED_BLOCKS = {
        "doll_0",
        "doll_1",
        "doll_2",
        "doll_3",
        "doll_4",
        "doll_5",
    };

    private static void applyRenderLayers(String[] ids, RenderType layer) {
        for (String id : ids) {
            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(KaleidoscopeChineseFood.MODID, id));
            if (block != Blocks.AIR) {
                BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
            }
        }
    }
}
