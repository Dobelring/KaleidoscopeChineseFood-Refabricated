package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.PickleJarBlockEntityRenderState;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.PickleJarBlockEntityRenderState.Piece;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * 官方 1.1.16 重构：随机散布 → 确定性圆环布局（种子=方块坐标），显示数量按存量映射，
 * 仅开盖（OPEN）时渲染，视距降为 32。26.1.2 渲染状态架构下布局计算在 extract 阶段完成，
 * submit 只按 extract 结果绘制。
 */
public class PickleJarRender implements BlockEntityRenderer<PickleJarBlockEntity, PickleJarBlockEntityRenderState> {
   private static final float[] LAYER_BASE_Y = new float[]{0.2F, 0.3F, 0.4F, 0.5F};
   private static final int[] RENDER_COUNTS = new int[]{0, 1, 2, 3, 6};
   private static final float LAYER_RADIUS = 0.105F;
   private static final float BASE_SCALE = 0.28F;
   private static final float SCALE_VARIATION = 0.02F;
   private static final float HEIGHT_JITTER = 0.012F;
   private static final int VIEW_DISTANCE = 32;
   private final ItemModelResolver itemModelResolver;

   public PickleJarRender(Context context) {
      this.itemModelResolver = context.itemModelResolver();
   }

   @Override
   public int getViewDistance() {
      return VIEW_DISTANCE;
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
      BlockState blockState = blockEntity.getBlockState();
      if (level == null || !(Boolean)blockState.getValue(PickleJarBlock.OPEN)) {
         state.pieces = pieces;
         return;
      }

      long seed = blockEntity.getBlockPos().asLong();

      for (int slot = 0; slot < 4; slot++) {
         ItemStack stack = blockEntity.inventory.getStackInSlot(slot);
         if (!stack.isEmpty()) {
            int copies = RENDER_COUNTS[Math.min(stack.getCount(), RENDER_COUNTS.length - 1)];
            float baseY = LAYER_BASE_Y[slot];

            for (int i = 0; i < copies; i++) {
               ItemStackRenderState itemState = new ItemStackRenderState();
               this.itemModelResolver
                  .updateForTopItem(itemState, stack, ItemDisplayContext.FIXED, level, null, (int)seed + slot * 64 + i);
               float x = 0.5F;
               float z = 0.5F;
               if (copies > 1) {
                  double angle = (Math.PI * 2) * i / copies;
                  x += (float)(Math.cos(angle) * LAYER_RADIUS);
                  z += (float)(Math.sin(angle) * LAYER_RADIUS);
               }

               float y = baseY + hashUnit(seed, slot * 64 + i) * HEIGHT_JITTER;
               float yRot = hashUnit(seed, slot * 64 + i + 16) * 360.0F;
               float scale = BASE_SCALE + (hashUnit(seed, slot * 64 + i + 32) - 0.5F) * 2.0F * SCALE_VARIATION;
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

   private static float hashUnit(long seed, int salt) {
      long h = seed * -7046029254386353131L + salt * -4658895280553007687L;
      h ^= h >>> 30;
      h *= -4658895280553007687L;
      h ^= h >>> 27;
      h *= -7723592293110705685L;
      h ^= h >>> 31;
      return (float)(h & 16777215L) / 1.6777216E7F;
   }
}
