package com.bmt.kaleidoscope_chinesefood.client.renderer.entity;

import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class KongmingLanternRender extends EntityRenderer<KongmingLanternEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public KongmingLanternRender(Context context) {
        super(context);
        this.shadowRadius = 0.3F;
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    public void render(KongmingLanternEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float rotation = entity.getVisualYaw(partialTicks);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(-0.5, 0.0, -0.5);
        BlockState state = ModBlocks.KONGMING_LANTERN.defaultBlockState();
        this.blockRenderer.renderSingleBlock(state, poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    public ResourceLocation getTextureLocation(KongmingLanternEntity entity) {
        return null;
    }
}
