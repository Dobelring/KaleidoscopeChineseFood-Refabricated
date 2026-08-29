package com.bmt.kaleidoscope_chinesefood.client.renderer.entity;

import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.KongmingLanternRenderState;
import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Block;

public class KongmingLanternRender extends EntityRenderer<KongmingLanternEntity, KongmingLanternRenderState> {
   public KongmingLanternRender(Context context) {
      super(context);
      this.shadowRadius = 0.3F;
   }

   public KongmingLanternRenderState createRenderState() {
      return new KongmingLanternRenderState();
   }

   public void extractRenderState(KongmingLanternEntity entity, KongmingLanternRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.yRot = entity.getYRot() + (entity.getYRot() - entity.yRotO) * partialTick;
   }

   public void submit(KongmingLanternRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
      poseStack.pushPose();
      poseStack.translate(-0.5, 0.0, -0.5);
      poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
      Block block = ModBlocks.KONGMING_LANTERN;
      // 常亮：光源值取满，方块自身渲染经由 submit 节点收集
      submitNodeCollector.submitBlock(poseStack, block.defaultBlockState(), 15728880, OverlayTexture.NO_OVERLAY, 0);
      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
   }
}
