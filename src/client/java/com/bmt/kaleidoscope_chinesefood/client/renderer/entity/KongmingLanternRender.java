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
      Level level = entity.level();
      if (level instanceof ClientLevel clientLevel) {
         BlockPos pos = entity.blockPosition();
         state.movingBlock.biome = clientLevel.getBiome(pos);
         state.movingBlock.cardinalLighting = clientLevel.cardinalLighting();
         state.movingBlock.lightEngine = clientLevel.getLightEngine();
      }
   }

   public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
      if (state.lanternBlock.getRenderShape() == RenderShape.MODEL) {
         state.movingBlock.randomSeedPos = BlockPos.containing(state.x, state.y, state.z);
         state.movingBlock.blockPos = BlockPos.ZERO;
         state.movingBlock.blockState = state.lanternBlock;
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
