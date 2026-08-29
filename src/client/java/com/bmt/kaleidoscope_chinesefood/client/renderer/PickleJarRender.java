package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.PickleJarBlockEntityRenderState;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.PickleJarBlockEntityRenderState.Piece;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class PickleJarRender implements BlockEntityRenderer<PickleJarBlockEntity, PickleJarBlockEntityRenderState> {
   private static final float[] LAYER_BASE_Y = new float[]{0.2F, 0.3F, 0.4F, 0.5F};
   private static final int MIN_RENDER_COUNT = 4;
   private static final int MAX_RENDER_COUNT = 10;
   private static final float BASE_SCALE = 0.28F;
   private static final float SCALE_VARIATION = 0.05F;
   private static final float HEIGHT_OFFSET = 0.03F;
   private final ItemModelResolver itemModelResolver;

   public PickleJarRender(Context context) {
      this.itemModelResolver = context.itemModelResolver();
   }

   public PickleJarBlockEntityRenderState createRenderState() {
      return new PickleJarBlockEntityRenderState();
   }

   public void extractRenderState(
      PickleJarBlockEntity blockEntity, PickleJarBlockEntityRenderState state, float partialTick, Vec3 vec3, @Nullable CrumblingOverlay crumblingOverlay
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, vec3, crumblingOverlay);
      Level level = blockEntity.getLevel();
      List<Piece> pieces = new ArrayList<>();

      for (int slot = 0; slot < 4; slot++) {
         ItemStack stack = blockEntity.inventory.getStackInSlot(slot);
         if (!stack.isEmpty()) {
            Random random = new Random(blockEntity.getBlockPos().hashCode() + slot * 200L);
            int renderCount = Math.min(Math.max(10 + stack.getCount() / 8, 4), 10);
            float baseY = LAYER_BASE_Y[slot];

            for (int i = 0; i < renderCount; i++) {
               ItemStackRenderState itemState = new ItemStackRenderState();
               this.itemModelResolver.updateForTopItem(itemState, stack, ItemDisplayContext.FIXED, level, null, (int)blockEntity.getBlockPos().asLong() + slot * 10 + i);
               float radius = random.nextFloat() * 0.15F;
               float angle = random.nextFloat() * (float) Math.PI * 2.0F;
               float x = 0.5F + radius * (float)Math.cos(angle);
               float z = 0.5F + radius * (float)Math.sin(angle);
               float y = baseY + random.nextFloat() * 0.03F;
               float yRot = random.nextFloat() * 360.0F;
               float scale = 0.28F + random.nextFloat() * 0.05F;
               pieces.add(new Piece(itemState, x, y, z, yRot, scale));
            }
         }
      }

      state.pieces = pieces;
   }

   public void submit(PickleJarBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
      for (Piece piece : state.pieces) {
         if (!piece.item().isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(piece.x(), piece.y(), piece.z());
            poseStack.mulPose(Axis.YP.rotationDegrees(piece.yRot()));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(piece.scale(), piece.scale(), piece.scale());
            piece.item().submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
         }
      }
   }

   public boolean shouldRenderOffScreen() {
      return true;
   }
}
