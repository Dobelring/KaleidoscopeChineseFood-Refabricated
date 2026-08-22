package com.bmt.kaleidoscope_chinesefood.client.renderer.entity;

import com.bmt.kaleidoscope_chinesefood.block.KongmingLanternBlock;
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
import org.jetbrains.annotations.NotNull;

public class KongmingLanternRender extends EntityRenderer<KongmingLanternEntity> {
   private final BlockRenderDispatcher blockRenderer;

   public KongmingLanternRender(Context context) {
      super(context);
      this.shadowRadius = 0.3F;
      this.blockRenderer = context.getBlockRenderDispatcher();
   }

   public void render(
      @NotNull KongmingLanternEntity entity,
      float entityYaw,
      float partialTicks,
      @NotNull PoseStack poseStack,
      @NotNull MultiBufferSource buffer,
      int packedLight
   ) {
      poseStack.pushPose();
      poseStack.translate(-0.5, 0.0, -0.5);
      float rotation = entity.getYRot() + (entity.getYRot() - entity.yRotO) * partialTicks;
      poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
      BlockState state = ((KongmingLanternBlock)ModBlocks.KONGMING_LANTERN).defaultBlockState();
      this.blockRenderer.renderSingleBlock(state, poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY);
      poseStack.popPose();
      super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull KongmingLanternEntity entity) {
      return null;
   }
}
