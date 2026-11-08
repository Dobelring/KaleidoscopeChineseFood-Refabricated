package com.bmt.kaleidoscope_chinesefood.client.renderer.entity;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.entity.YellowCroakerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class YellowCroakerRender extends MobRenderer<YellowCroakerEntity, CodModel<YellowCroakerEntity>> {
   private static final ResourceLocation TEXTURE = KaleidoscopeChineseFood.id("textures/entity/yellow_croaker.png");

   public YellowCroakerRender(Context context) {
      super(context, new CodModel(context.bakeLayer(ModelLayers.COD)), 0.3F);
   }

   public ResourceLocation getTextureLocation(YellowCroakerEntity entity) {
      return TEXTURE;
   }

   protected void setupRotations(YellowCroakerEntity entity, PoseStack poseStack, float bob, float yRot, float partialTicks, float scale) {
      super.setupRotations(entity, poseStack, bob, yRot, partialTicks, scale);
      float f = 4.3F * Mth.sin(0.6F * bob);
      poseStack.mulPose(Axis.YP.rotationDegrees(f));
      if (!entity.isInWater()) {
         poseStack.translate(0.1F, 0.1F, -0.1F);
         poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }
   }
}
