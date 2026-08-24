package com.bmt.kaleidoscope_chinesefood.client.renderer.entity;

import com.bmt.kaleidoscope_chinesefood.block.KongmingLanternBlock;
import com.bmt.kaleidoscope_chinesefood.entity.KongmingLanternEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class KongmingLanternRender extends EntityRenderer<KongmingLanternEntity, KongmingLanternRender.State> {
   public KongmingLanternRender(Context context) {
      super(context);
      this.shadowRadius = 0.3F;
   }

   public State createRenderState() {
      return new State();
   }

   public void extractRenderState(KongmingLanternEntity entity, State state, float partialTick) {
      super.extractRenderState(entity, state, partialTick);
      state.yRotInterpolated = entity.getYRot() + (entity.getYRot() - entity.yRotO) * partialTick;
      state.lanternBlock = ((KongmingLanternBlock)ModBlocks.KONGMING_LANTERN).defaultBlockState();
      // 与 vanilla FallingBlockRenderer 对齐：MovingBlockRenderState 是以 blockPos 为锚点的
      // 单方块假世界，块模型的光照/AO 都按 blockPos 在真实光照引擎里采样。此前写死
      // BlockPos.ZERO 导致光照采到世界原点（y≈0），孔明灯一点燃（转为实体渲染）就整体变暗。
      BlockPos pos = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
      state.movingBlock.randomSeedPos = pos;
      state.movingBlock.blockPos = pos;
      state.movingBlock.blockState = state.lanternBlock;
      Level level = entity.level();
      if (level instanceof ClientLevel clientLevel) {
         state.movingBlock.biome = clientLevel.getBiome(pos);
         state.movingBlock.cardinalLighting = clientLevel.cardinalLighting();
         state.movingBlock.lightEngine = clientLevel.getLightEngine();
      }
   }

   public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
      if (state.lanternBlock.getRenderShape() == RenderShape.MODEL) {
         poseStack.pushPose();
         poseStack.translate(-0.5, 0.0, -0.5);
         poseStack.mulPose(Axis.YP.rotationDegrees(state.yRotInterpolated));
         collector.submitMovingBlock(poseStack, state.movingBlock, state.outlineColor);
         poseStack.popPose();
      }

      super.submit(state, poseStack, collector, camera);
   }

   public static class State extends EntityRenderState {
      public final MovingBlockRenderState movingBlock = new MovingBlockRenderState();
      public BlockState lanternBlock;
      public float yRotInterpolated;

      public State() {
      }
   }
}
