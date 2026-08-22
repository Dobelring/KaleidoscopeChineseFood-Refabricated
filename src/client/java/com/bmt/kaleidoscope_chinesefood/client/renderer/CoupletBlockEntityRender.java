package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.CoupletBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.config.ClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;

public class CoupletBlockEntityRender implements BlockEntityRenderer<CoupletBlockEntity> {
   private static final ResourceLocation COUPLET_FONT = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   private final Font font;

   public CoupletBlockEntityRender(Context context) {
      this.font = context.getFont();
   }

   public void render(
      CoupletBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      BlockState state = blockEntity.getBlockState();
      if (state.getValue(CoupletBlock.PART) == CoupletBlock.CoupletPart.LOWER) {
         String firstLineText = blockEntity.getTruncatedLine(0);
         if (firstLineText != null && !firstLineText.isBlank()) {
            boolean isTriple = false;
            if (blockEntity.getLevel() != null) {
               BlockPos upperUpperPos = blockEntity.getBlockPos().above(2);
               BlockState upperUpperState = blockEntity.getLevel().getBlockState(upperUpperPos);
               isTriple = upperUpperState.is(state.getBlock()) && upperUpperState.getValue(CoupletBlock.PART) == CoupletBlock.CoupletPart.UPPER;
            }

            Direction facing = (Direction)state.getValue(CoupletBlock.FACING);
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            poseStack.translate(0.0, 0.0, -0.48);
            float scale = ClientConfig.COUPLET_TEXT_SCALE;
            poseStack.scale(scale, -scale, scale);
            float wordHeight = ClientConfig.COUPLET_VERTICAL_SPACING;
            float totalContentHeight = firstLineText.length() * wordHeight;
            float baseY = isTriple ? ClientConfig.COUPLET_TRIPLE_BASE_Y : ClientConfig.COUPLET_DOUBLE_BASE_Y;
            float currentY = baseY - totalContentHeight / 2.0F;
            Style coupletStyle = Style.EMPTY.withFont(COUPLET_FONT);
            int charCount = firstLineText.length();

            for (int i = 0; i < charCount; i++) {
               String singleCharStr = String.valueOf(firstLineText.charAt(i));
               Component singleCharComponent = Component.literal(singleCharStr).withStyle(coupletStyle);
               FormattedCharSequence singleChar = singleCharComponent.getVisualOrderText();
               float x = -this.font.width(singleChar) / 2.0F + ClientConfig.COUPLET_HORIZONTAL_OFFSET;
               float y = currentY + i * wordHeight;
               this.font.drawInBatch(singleChar, x, y, 0, false, poseStack.last().pose(), bufferSource, DisplayMode.NORMAL, 0, packedLight);
            }

            poseStack.popPose();
         }
      }
   }

   public boolean shouldRenderOffScreen(CoupletBlockEntity blockEntity) {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }
}
