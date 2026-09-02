package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.CoupletBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.CoupletBlockEntityRenderState;
import com.bmt.kaleidoscope_chinesefood.config.ClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CoupletBlockEntityRender implements BlockEntityRenderer<CoupletBlockEntity, CoupletBlockEntityRenderState> {
   private static final Identifier COUPLET_FONT = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   private final Font font;

   public CoupletBlockEntityRender(Context context) {
      this.font = context.font();
   }

   public CoupletBlockEntityRenderState createRenderState() {
      return new CoupletBlockEntityRenderState();
   }

   public void extractRenderState(
      CoupletBlockEntity blockEntity, CoupletBlockEntityRenderState state, float partialTick, Vec3 vec3, @Nullable CrumblingOverlay crumblingOverlay
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, vec3, crumblingOverlay);
      state.text = null;
      BlockState state2 = blockEntity.getBlockState();
      if (state2.getValue(CoupletBlock.PART) == CoupletBlock.CoupletPart.LOWER) {
         String firstLineText = blockEntity.getTruncatedLine(0);
         if (firstLineText != null && !firstLineText.isBlank()) {
            boolean isTriple = false;
            if (blockEntity.getLevel() != null) {
               BlockPos upperUpperPos = blockEntity.getBlockPos().above(2);
               BlockState upperUpperState = blockEntity.getLevel().getBlockState(upperUpperPos);
               isTriple = upperUpperState.is(state2.getBlock()) && upperUpperState.getValue(CoupletBlock.PART) == CoupletBlock.CoupletPart.UPPER;
            }

            state.text = firstLineText;
            state.isTriple = isTriple;
            state.facing = (Direction)state2.getValue(CoupletBlock.FACING);
         }
      }
   }

   public void submit(CoupletBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
      String text = state.text;
      if (text != null) {
         poseStack.pushPose();
         poseStack.translate(0.5, 0.5, 0.5);
         poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
         poseStack.translate(0.0, 0.0, -0.48);
         float scale = ClientConfig.COUPLET_TEXT_SCALE;
         poseStack.scale(scale, -scale, scale);
         float wordHeight = ClientConfig.COUPLET_VERTICAL_SPACING;
         float totalContentHeight = text.length() * wordHeight;
         float baseY = state.isTriple ? ClientConfig.COUPLET_TRIPLE_BASE_Y : ClientConfig.COUPLET_DOUBLE_BASE_Y;
         float currentY = baseY - totalContentHeight / 2.0F;
         Style coupletStyle = Style.EMPTY.withFont(new FontDescription.Resource(COUPLET_FONT));

         for (int i = 0; i < text.length(); i++) {
            String singleCharStr = String.valueOf(text.charAt(i));
            Component singleCharComponent = Component.literal(singleCharStr).withStyle(coupletStyle);
            FormattedCharSequence singleChar = singleCharComponent.getVisualOrderText();
            float x = -this.font.width(singleChar) / 2.0F + ClientConfig.COUPLET_HORIZONTAL_OFFSET;
            float y = currentY + i * wordHeight;
            // 传不透明黑色，保证文字始终可见
            submitNodeCollector.submitText(poseStack, x, y, singleChar, false, DisplayMode.NORMAL, state.lightCoords, -16777216, 0, 0);
         }

         poseStack.popPose();
      }
   }

   public boolean shouldRenderOffScreen() {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }
}
