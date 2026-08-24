package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.CoupletBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.config.ClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;

public class CoupletBlockEntityRender implements BlockEntityRenderer<CoupletBlockEntity, CoupletBlockEntityRender.State> {
   private static final Identifier COUPLET_FONT = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   private static final Style COUPLET_STYLE = Style.EMPTY.withFont(new FontDescription.Resource(COUPLET_FONT));
   private final Font font;

   public CoupletBlockEntityRender(Context context) {
      this.font = context.font();
   }

   public State createRenderState() {
      return new State();
   }

   private static final org.slf4j.Logger WRITE_LOG = org.slf4j.LoggerFactory.getLogger("kcf_write_debug");
   private static String lastLoggedClientText;

   public void extractRenderState(CoupletBlockEntity blockEntity, State state, float partialTick, net.minecraft.world.phys.Vec3 cameraPos, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
      BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
      state.text = null;
      state.facing = Direction.NORTH;
      state.isTriple = false;
      BlockState blockState = blockEntity.getBlockState();
      if (blockState.getValue(CoupletBlock.PART) == CoupletBlock.CoupletPart.LOWER) {
         String firstLineText = blockEntity.getTruncatedLine(0);
         if (!java.util.Objects.equals(firstLineText, lastLoggedClientText)) {
            WRITE_LOG.info("[字][客户端] 渲染器读到BE文字='{}'", firstLineText);
            lastLoggedClientText = firstLineText;
         }
         if (firstLineText != null && !firstLineText.isBlank()) {
            if (blockEntity.getLevel() != null) {
               BlockPos upperUpperPos = blockEntity.getBlockPos().above(2);
               BlockState upperUpperState = blockEntity.getLevel().getBlockState(upperUpperPos);
               state.isTriple = upperUpperState.is(blockState.getBlock()) && upperUpperState.getValue(CoupletBlock.PART) == CoupletBlock.CoupletPart.UPPER;
            }

            state.facing = (Direction)blockState.getValue(CoupletBlock.FACING);
            state.text = firstLineText;
         }
      }
   }

   public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
      String text = state.text;
      if (text != null) {
         Direction facing = state.facing;
         poseStack.pushPose();
         poseStack.translate(0.5, 0.5, 0.5);
         poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
         poseStack.translate(0.0, 0.0, -0.48);
         float scale = ClientConfig.COUPLET_TEXT_SCALE;
         poseStack.scale(scale, -scale, scale);
         float wordHeight = ClientConfig.COUPLET_VERTICAL_SPACING;
         float totalContentHeight = text.length() * wordHeight;
         float baseY = state.isTriple ? ClientConfig.COUPLET_TRIPLE_BASE_Y : ClientConfig.COUPLET_DOUBLE_BASE_Y;
         float currentY = baseY - totalContentHeight / 2.0F;
         int charCount = text.length();

         for (int i = 0; i < charCount; i++) {
            Component singleCharComponent = Component.literal(String.valueOf(text.charAt(i))).withStyle(COUPLET_STYLE);
            FormattedCharSequence singleChar = singleCharComponent.getVisualOrderText();
            float x = -this.font.width(singleChar) / 2.0F + ClientConfig.COUPLET_HORIZONTAL_OFFSET;
            float y = currentY + i * wordHeight;
            collector.submitText(poseStack, x, y, singleChar, false, DisplayMode.NORMAL, state.lightCoords, 0, 0, 0);
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

   public static class State extends BlockEntityRenderState {
      public String text;
      public Direction facing = Direction.NORTH;
      public boolean isTriple;

      public State() {
      }
   }
}
