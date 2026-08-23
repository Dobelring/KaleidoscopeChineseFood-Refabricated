package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PickleJarRender implements BlockEntityRenderer<PickleJarBlockEntity, PickleJarRender.State> {
   private static final float[] LAYER_BASE_Y = new float[]{0.2F, 0.3F, 0.4F, 0.5F};
   private static final float JAR_RADIUS = 0.15F;
   private static final int MIN_RENDER_COUNT = 4;
   private static final int MAX_RENDER_COUNT = 10;
   private static final int MAX_ITEMS = 40; // 4 slots * MAX_RENDER_COUNT
   private static final float BASE_SCALE = 0.28F;
   private static final float SCALE_VARIATION = 0.05F;
   private static final float HEIGHT_OFFSET = 0.03F;
   private final ItemModelResolver itemModelResolver;

   public PickleJarRender(Context context) {
      this.itemModelResolver = context.itemModelResolver();
   }

   public State createRenderState() {
      return new State();
   }

   public void extractRenderState(PickleJarBlockEntity be, State state, float partialTick, net.minecraft.world.phys.Vec3 cameraPos, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
      BlockEntityRenderState.extractBase(be, state, crumblingOverlay);
      state.clear();

      for (int slot = 0; slot < 4 && slot * MAX_RENDER_COUNT < MAX_ITEMS; slot++) {
         ItemStack stack = be.inventory.getStackInSlot(slot);
         if (!stack.isEmpty()) {
            Random random = new Random(be.getBlockPos().hashCode() + slot * 200L);
            int renderCount = Math.min(Math.max(10 + stack.getCount() / 8, MIN_RENDER_COUNT), MAX_RENDER_COUNT);
            float baseY = LAYER_BASE_Y[slot];

            for (int i = 0; i < renderCount; i++) {
               int index = state.count++;
               if (index >= MAX_ITEMS) {
                  return;
               }

               float radius = random.nextFloat() * JAR_RADIUS;
               float angle = random.nextFloat() * (float) Math.PI * 2.0F;
               float x = 0.5F + radius * (float) Math.cos(angle);
               float z = 0.5F + radius * (float) Math.sin(angle);
               float y = baseY + random.nextFloat() * HEIGHT_OFFSET;
               state.x[index] = x;
               state.y[index] = y;
               state.z[index] = z;
               state.yaw[index] = random.nextFloat() * 360.0F;
               state.scale[index] = BASE_SCALE + random.nextFloat() * SCALE_VARIATION;
               this.itemModelResolver.updateForTopItem(state.models[index], stack, ItemDisplayContext.FIXED, be.getLevel(), (ItemOwner)null, index);
            }
         }
      }
   }

   public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
      for (int i = 0; i < state.count; i++) {
         ItemStackRenderState model = state.models[i];
         if (!model.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(state.x[i], state.y[i], state.z[i]);
            poseStack.mulPose(Axis.YP.rotationDegrees(state.yaw[i]));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            float scale = state.scale[i];
            poseStack.scale(scale, scale, scale);
            model.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
         }
      }
   }

   public boolean shouldRenderOffScreen() {
      return true;
   }

   public static class State extends BlockEntityRenderState {
      public final ItemStackRenderState[] models = new ItemStackRenderState[MAX_ITEMS];
      public final float[] x = new float[MAX_ITEMS];
      public final float[] y = new float[MAX_ITEMS];
      public final float[] z = new float[MAX_ITEMS];
      public final float[] yaw = new float[MAX_ITEMS];
      public final float[] scale = new float[MAX_ITEMS];
      public int count;

      public State() {
         for (int i = 0; i < MAX_ITEMS; i++) {
            this.models[i] = new ItemStackRenderState();
         }
      }

      public void clear() {
         this.count = 0;
      }
   }
}
