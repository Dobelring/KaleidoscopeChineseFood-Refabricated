package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.entity.EnchantedPlateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/** 把附魔拼盘的方块模型用 glint 渲染类型再画一遍，得到附魔光效。 */
public class EnchantedPlateBlockEntityRenderer implements BlockEntityRenderer<EnchantedPlateBlockEntity> {
    private static final RandomSource RANDOM = RandomSource.create();
    private final BlockRenderDispatcher blockRenderDispatcher;

    public EnchantedPlateBlockEntityRenderer(Context context) {
        this.blockRenderDispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
            EnchantedPlateBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        poseStack.pushPose();
        VertexConsumer consumer = new SheetedDecalTextureGenerator(
                bufferSource.getBuffer(CustomRenderType.GLINT), poseStack.last(), 0.0078125F
        );
        this.blockRenderDispatcher.renderBatched(
                blockEntity.getBlockState(), blockEntity.getBlockPos(), level, poseStack, consumer, true, RANDOM
        );
        poseStack.popPose();
    }
}
