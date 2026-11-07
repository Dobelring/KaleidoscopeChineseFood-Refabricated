package com.bmt.kaleidoscope_chinesefood.client.renderer.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.CustomRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.RandomSource;

public class EnchantedPlateBlockEntityRender implements BlockEntityRenderer<EnchantedPlateBlockEntity> {
    private static final RandomSource RANDOM = RandomSource.create();
    private final BlockRenderDispatcher blockRenderDispatcher;

    public EnchantedPlateBlockEntityRender(Context context) {
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    public void render(
        EnchantedPlateBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
    ) {
        if (blockEntity.getLevel() != null) {
            poseStack.pushPose();
            Pose pose = poseStack.last();
            VertexConsumer consumer = new SheetedDecalTextureGenerator(
                bufferSource.getBuffer(CustomRenderType.GLINT), pose.pose(), pose.normal(), 0.0078125F
            );
            // 原 Forge 版是 9 参 renderBatched(..., ModelData.EMPTY, null)，
            // Fabric/原版 1.20.1 只有 7 参重载，去掉末尾的 ModelData 与 RenderType
            this.blockRenderDispatcher
                .renderBatched(blockEntity.getBlockState(), blockEntity.getBlockPos(), blockEntity.getLevel(), poseStack, consumer, true, RANDOM);
            poseStack.popPose();
        }
    }
}
