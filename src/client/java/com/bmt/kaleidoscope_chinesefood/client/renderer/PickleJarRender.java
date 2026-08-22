package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PickleJarRender implements BlockEntityRenderer<PickleJarBlockEntity> {
   private static final float[] LAYER_BASE_Y = new float[]{0.2F, 0.3F, 0.4F, 0.5F};
   private static final float JAR_RADIUS = 0.15F;
   private static final int MIN_RENDER_COUNT = 4;
   private static final int MAX_RENDER_COUNT = 10;
   private static final float BASE_SCALE = 0.28F;
   private static final float SCALE_VARIATION = 0.05F;
   private static final float HEIGHT_OFFSET = 0.03F;
   private final ItemRenderer itemRenderer;

   public PickleJarRender(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(PickleJarBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      Level level = be.getLevel();
      if (level != null) {
         for (int slot = 0; slot < 4; slot++) {
            ItemStack stack = be.inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
               Random random = new Random(be.getBlockPos().hashCode() + slot * 200L);
               int renderCount = Math.min(Math.max(10 + stack.getCount() / 8, 4), 10);
               float baseY = LAYER_BASE_Y[slot];

               for (int i = 0; i < renderCount; i++) {
                  poseStack.pushPose();
                  float radius = random.nextFloat() * 0.15F;
                  float angle = random.nextFloat() * (float) Math.PI * 2.0F;
                  float x = 0.5F + radius * (float)Math.cos(angle);
                  float z = 0.5F + radius * (float)Math.sin(angle);
                  float y = baseY + random.nextFloat() * 0.03F;
                  poseStack.translate(x, y, z);
                  poseStack.mulPose(Axis.YP.rotationDegrees(random.nextFloat() * 360.0F));
                  poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                  float scale = 0.28F + random.nextFloat() * 0.05F;
                  poseStack.scale(scale, scale, scale);
                  this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, level, 0);
                  poseStack.popPose();
               }
            }
         }
      }
   }

   public boolean shouldRenderOffScreen(PickleJarBlockEntity be) {
      return true;
   }
}
