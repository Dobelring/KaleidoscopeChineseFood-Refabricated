package com.bmt.kaleidoscope_chinesefood.client.renderer.entity;

import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.KongmingLanternRenderState;
import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class KongmingLanternRender extends EntityRenderer<KongmingLanternEntity, KongmingLanternRenderState> {
   // BlockDisplayContext.create() 无世界上下文依赖，可静态缓存
   private static final BlockDisplayContext DISPLAY_CONTEXT = BlockDisplayContext.create();
   private final net.minecraft.client.renderer.block.BlockModelResolver blockModelResolver;

   public KongmingLanternRender(Context context) {
      super(context);
      this.blockModelResolver = context.getBlockModelResolver();
      this.shadowRadius = 0.3F;
   }

   public KongmingLanternRenderState createRenderState() {
      return new KongmingLanternRenderState();
   }

   public void extractRenderState(KongmingLanternEntity entity, KongmingLanternRenderState state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.yRot = entity.getYRot() + (entity.getYRot() - entity.yRotO) * partialTick;
      // 26.1 BER 新范式：extract 期解析方块模型进 BlockModelRenderState
      this.blockModelResolver.update(state.model, ModBlocks.KONGMING_LANTERN.defaultBlockState(), DISPLAY_CONTEXT);
   }

   public void submit(KongmingLanternRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
      poseStack.pushPose();
      poseStack.translate(-0.5, 0.0, -0.5);
      poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot));
      // 常亮：光源值取满；由 BlockModelRenderState 按模型层自选 sheet 提交
      if (!state.model.isEmpty()) {
         state.model.submit(poseStack, submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, 0);
      }
      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, cameraRenderState);
   }
}
