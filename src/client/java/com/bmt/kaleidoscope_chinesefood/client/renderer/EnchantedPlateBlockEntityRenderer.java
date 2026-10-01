package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.EnchantedPlateRenderState;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.RenderTypeInvoker;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * 附魔金苹果拼盘：把拼盘方块模型用附魔光效再画一遍。
 * <p>
 * <b>26.3 渲染管线要点（两轮踩坑的结论）</b>：
 * <ol>
 * <li>26.3 把带混合（BlendFunction）的管线分流进 OIT 阶段，而自定义 RenderType 没设
 * {@code setOitPipelines(...)} 就会在 {@code drawFromBufferOit} 抛
 * "does not have OIT pipelines set up"。原版对 plain {@code GLINT} 管线的做法是
 * {@code withForcedSolidModelPhase()} 强制回 solid 阶段——但 plain GLINT 深度测试是
 * EQUAL 且只有 {@code glint.fsh} 单独输出光效，覆盖不全时流光会很弱。</li>
 * <li>因此改用原版给<b>附魔物品</b>用的组合管线 {@code ITEM_CUTOUT_GLINT}
 * （ITEM_SNIPPET + GLINT_SNIPPET）：着色器内 {@code color.rgb += GlintAlpha * glint²}
 * 直接把光效<b>加亮叠加</b>在本体颜色上（与原版物品附魔光效同一强度），
 * {@code ColorTargetState.DEFAULT} 无混合 → 天然走 solid 阶段不进 OIT，
 * 深度测试默认 LEQUAL → 不需要再重画一遍本体去垫深度。</li>
 * </ol>
 * 配置与原版 {@code RenderTypes.ITEM_CUTOUT_GLINT} 逐项对齐：Sampler0=方块图集（本体）、
 * GlintSampler=光效贴图、{@link TextureTransform#GLINT_TEXTURING} 做流光 UV 滚动
 * （物品 UV 本来就是图集坐标，原版着色器直接换算，无需手动缩放）。
 * 另加 {@link LayeringTransform#VIEW_OFFSET_Z_LAYERING} 朝相机方向的 Z 偏移，
 * 让 LEQUAL 稳定胜过区块层画出的同位置拼盘，跨阶段深度精度差 1 ULP 也不会丢像素。
 */
public class EnchantedPlateBlockEntityRenderer
        implements BlockEntityRenderer<EnchantedPlateBlockEntity, EnchantedPlateRenderState> {

    /** 懒建：渲染类型要在客户端资源就绪后才构造。 */
    private static final class Types {
        /** 本体 + 光效单趟组合：原版附魔物品同款管线，一次提交同时得到底图与流光。 */
        private static final RenderType PLATE_GLINT = RenderTypeInvoker.kaleidoscope_chinesefood$create(
                "kaleidoscope_chinesefood_plate_glint",
                RenderSetup.builder(RenderPipelines.ITEM_CUTOUT_GLINT)
                        .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                        .withTexture("GlintSampler", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
                        .setTextureTransform(TextureTransform.GLINT_TEXTURING)
                        .useLightmap()
                        .useOverlay()
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .withForcedSolidModelPhase()
                        .createRenderSetup()
        );
    }

    public EnchantedPlateBlockEntityRenderer(Context context) {
    }

    @Override
    public EnchantedPlateRenderState createRenderState() {
        return new EnchantedPlateRenderState();
    }

    @Override
    public void extractRenderState(
            EnchantedPlateBlockEntity blockEntity,
            EnchantedPlateRenderState state,
            float partialTick,
            Vec3 cameraPos,
            @Nullable CrumblingOverlay crumblingOverlay
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPos, crumblingOverlay);
        state.blockState = blockEntity.getBlockState();
    }

    @Override
    public void submit(EnchantedPlateRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        List<BlockStateModelPart> parts = collectParts(state);
        if (parts == null || parts.isEmpty()) {
            return;
        }
        // 组合管线单趟提交：底图（图集 UV）+ 流光（着色器内叠加）。快照避免复用渲染状态时被清空。
        collector.submitBlockModel(
                poseStack, Types.PLATE_GLINT, new ObjectArrayList<>(parts),
                BlockModelRenderState.EMPTY_TINTS, state.lightCoords, OverlayTexture.NO_OVERLAY, 0
        );
    }

    /** 直接向烘焙好的方块模型取部件（不经 {@code BlockModelResolver}，那条链在 26.1.2 上取不到部件）。 */
    private static List<BlockStateModelPart> collectParts(EnchantedPlateRenderState state) {
        if (state.blockState == null) {
            return null;
        }
        BlockStateModelSet modelSet = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
        BlockStateModel model = modelSet.get(state.blockState);
        List<BlockStateModelPart> parts = new ObjectArrayList<>();
        model.collectParts(RandomSource.create(42L), parts);
        return parts;
    }
}
