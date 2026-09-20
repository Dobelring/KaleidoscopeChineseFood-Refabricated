package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.EnchantedPlateRenderState;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.BlockModelRenderStateAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * 附魔金苹果拼盘：把拼盘方块模型用原版附魔光效渲染类型再画一遍。
 * <p>
 * 26.1.2 已无 {@code RenderType.create} 公开入口，也无需自建渲染类型 / Access Widener——
 * {@code RenderTypes.glint()} 就是原版那支用 {@code enchanted_glint_item} 贴图的 GLINT 渲染类型。
 * 模型部件在 extract 阶段解析（与其它方块实体一致），submit 阶段只负责提交。
 */
public class EnchantedPlateBlockEntityRenderer
        implements BlockEntityRenderer<EnchantedPlateBlockEntity, EnchantedPlateRenderState> {
    private final BlockModelResolver blockModelResolver;

    public EnchantedPlateBlockEntityRenderer(Context context) {
        this.blockModelResolver = context.blockModelResolver();
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
        this.blockModelResolver.update(state.model, blockEntity.getBlockState(), BlockDisplayContext.create());
    }

    @Override
    public void submit(EnchantedPlateRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        List<BlockStateModelPart> parts = ((BlockModelRenderStateAccessor) state.model).kaleidoscope_chinesefood$getModelParts();
        if (parts == null || parts.isEmpty()) {
            return;
        }
        collector.submitBlockModel(
                poseStack,
                RenderTypes.glint(),
                new ObjectArrayList<>(parts),
                BlockModelRenderState.EMPTY_TINTS,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                0
        );
    }
}
