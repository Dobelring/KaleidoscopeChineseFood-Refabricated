package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.EnchantedPlateRenderState;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.RenderTypeInvoker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
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
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * 附魔金苹果拼盘：把拼盘方块模型用附魔光效再画一遍。
 * <p>
 * <b>为什么要自建渲染类型</b>：原版 {@code RenderTypes.glint()} 的深度测试是 {@code CompareOp.EQUAL}，
 * 只落在<b>深度逐位相同</b>的像素上。方块本体由区块层绘制、光效在 feature 层绘制，两边矩阵组合精度不同，
 * 深度差 1 个 ULP 就会随镜头移动时通过时不通过——表现为闪烁（重画一遍本体去"铺深度"也没用：
 * 重画的那遍本身就在和区块层比深度，同样会抖）。
 * <p>
 * 这里改用 {@code ENTITY_TRANSLUCENT} 管线（默认 {@code LEQUAL} + 透明混合）自建一支渲染类型，
 * 并加 {@link LayeringTransform#VIEW_OFFSET_Z_LAYERING}（朝相机方向的 Z 偏移，原版给实体做 Z 偏移
 * 防闪用的就是它）。偏移后光效稳定落在拼盘之前，{@code LEQUAL} 必然通过，不依赖深度逐位相等。
 * 管线仍是原版的，因此不需要新的 RenderPipeline，也就无需管线注册。
 * <p>
 * 光效贴图按 {@link TextureTransform#GLINT_TEXTURING} 做 UV 滚动，保持原版的"流光"观感；
 * 贴图本身不 clamp（只 blur），UV 取方块图集坐标即可，会自然平铺。
 */
public class EnchantedPlateBlockEntityRenderer
        implements BlockEntityRenderer<EnchantedPlateBlockEntity, EnchantedPlateRenderState> {
    private static final Direction[] DIRECTIONS = Direction.values();

    /** 懒建：渲染类型要在客户端资源就绪后才构造。 */
    private static final class Types {
        /** 本体：方块图集 cutout + 朝相机的 Z 偏移，用于可靠地写入与光效一致的深度。 */
        private static final RenderType PLATE_BASE = RenderTypeInvoker.kaleidoscope_chinesefood$create(
                "kaleidoscope_chinesefood_plate_base",
                RenderSetup.builder(RenderPipelines.ENTITY_CUTOUT_CULL)
                        .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
                        .useLightmap()
                        .useOverlay()
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .createRenderSetup()
        );
        /** 光效：原版 GLINT 管线（真正的流光着色器）+ 同一 Z 偏移。 */
        private static final RenderType GLINT = RenderTypeInvoker.kaleidoscope_chinesefood$create(
                "kaleidoscope_chinesefood_plate_glint",
                RenderSetup.builder(RenderPipelines.GLINT)
                        .withTexture("Sampler0", ItemFeatureRenderer.ENCHANTED_GLINT_ITEM)
                        .setTextureTransform(TextureTransform.GLINT_TEXTURING)
                        .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
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
        // 渲染发生在后续批次里，先快照，避免复用渲染状态时被清空
        List<BlockStateModelPart> snapshot = new ObjectArrayList<>(parts);

        // 1) 本体：带同一个 Z 偏移重画一遍（block 提交先于 custom 提交，缓冲切换时先被冲刷）。
        //    带偏移后 LEQUAL 稳定胜过区块层，可靠写入深度，不再抖动。
        collector.submitBlockModel(
                poseStack, Types.PLATE_BASE, new ObjectArrayList<>(snapshot),
                BlockModelRenderState.EMPTY_TINTS, state.lightCoords, OverlayTexture.NO_OVERLAY, 0
        );

        // 2) 光效：同一批顶点 + 同一个 pose + 同一个 Z 偏移 → 深度逐位相等，EQUAL 稳定命中
        collector.submitCustomGeometry(poseStack, Types.GLINT, (pose, buffer) -> {
            QuadInstance quadInstance = new QuadInstance();
            quadInstance.setColor(-1);
            quadInstance.setLightCoords(state.lightCoords);
            quadInstance.setOverlayCoords(OverlayTexture.NO_OVERLAY);
            for (BakedQuad quad : scaledQuads(snapshot)) {
                buffer.putBakedQuad(pose, quad, quadInstance);
            }
        });
    }

    /**
     * 光效贴图是 128×128，而方块模型的 UV 是<b>图集像素坐标</b>（几百到几千）；
     * {@link TextureTransform#GLINT_TEXTURING} 还会把 UV 再乘 8（它是按物品那种 0..1 的 UV 设计的）。
     * 不缩放的话 UV 频率高到只剩一片纯色，所以按 1/128 先缩到贴图空间（官方 1.21.1 用的同一系数）。
     */
    private static final float GLINT_UV_SCALE = 0.0078125F;

    private static List<BakedQuad> scaledQuads(List<BlockStateModelPart> parts) {
        List<BakedQuad> out = new ObjectArrayList<>();
        for (BlockStateModelPart part : parts) {
            for (Direction direction : DIRECTIONS) {
                addScaled(out, part.getQuads(direction));
            }
            addScaled(out, part.getQuads(null));
        }
        return out;
    }

    private static void addScaled(List<BakedQuad> out, List<BakedQuad> source) {
        for (BakedQuad quad : source) {
            out.add(new BakedQuad(
                    quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                    scaleUv(quad.packedUV0()), scaleUv(quad.packedUV1()), scaleUv(quad.packedUV2()), scaleUv(quad.packedUV3()),
                    quad.direction(), quad.materialInfo()
            ));
        }
    }

    private static long scaleUv(long packed) {
        return UVPair.pack(UVPair.unpackU(packed) * GLINT_UV_SCALE, UVPair.unpackV(packed) * GLINT_UV_SCALE);
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
