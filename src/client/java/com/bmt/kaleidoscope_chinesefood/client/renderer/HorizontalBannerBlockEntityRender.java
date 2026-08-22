package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.HorizontalBannerBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalBannerBlockEntity;
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

public class HorizontalBannerBlockEntityRender implements BlockEntityRenderer<HorizontalBannerBlockEntity> {
   private static final ResourceLocation COUPLET_FONT = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   private final Font font;

   public HorizontalBannerBlockEntityRender(Context context) {
      this.font = context.getFont();
   }

   public void render(
      HorizontalBannerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      BlockState state = blockEntity.getBlockState();
      if (state.getValue(HorizontalBannerBlock.PART) == HorizontalBannerBlock.BannerPart.SINGLE
         || state.getValue(HorizontalBannerBlock.PART) == HorizontalBannerBlock.BannerPart.LEFT) {
         String firstLineText = blockEntity.getTruncatedLine(0);
         if (firstLineText != null && !firstLineText.isBlank()) {
            int totalWidth = 1;
            Direction facing = (Direction)state.getValue(HorizontalBannerBlock.FACING);

            for (BlockPos currentPos = blockEntity.getBlockPos().relative(facing.getCounterClockWise());
               blockEntity.getLevel() != null
                  && blockEntity.getLevel().getBlockState(currentPos).is(blockEntity.getBlockState().getBlock())
                  && blockEntity.getLevel().getBlockState(currentPos).getValue(HorizontalBannerBlock.FACING) == facing;
               currentPos = currentPos.relative(facing.getCounterClockWise())
            ) {
               totalWidth++;
            }

            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            poseStack.translate(0.0, 0.0, -0.48);
            float scale = ClientConfig.BANNER_TEXT_SCALE;
            poseStack.scale(scale, -scale, scale);
            float charWidth = ClientConfig.BANNER_CHAR_WIDTH;
            float totalContentWidth = firstLineText.length() * charWidth;
            float unitCenterOffset = (totalWidth - 1) * 8.0F;
            float textCenterOffset = -totalContentWidth / 2.0F;
            float extraOffset;
            if (totalWidth == 1) {
               extraOffset = ClientConfig.BANNER_SINGLE_OFFSET;
            } else if (totalWidth == 2) {
               extraOffset = ClientConfig.BANNER_DOUBLE_OFFSET;
            } else {
               extraOffset = ClientConfig.BANNER_TRIPLE_OFFSET;
            }

            float baseX = unitCenterOffset + textCenterOffset + extraOffset;
            float currentY = ClientConfig.BANNER_VERTICAL_OFFSET;
            Style coupletStyle = Style.EMPTY.withFont(COUPLET_FONT);
            int charCount = firstLineText.length();
            if (charCount > 0) {
               for (int i = 0; i < charCount; i++) {
                  String singleCharStr = String.valueOf(firstLineText.charAt(i));
                  Component singleCharComponent = Component.literal(singleCharStr).withStyle(coupletStyle);
                  FormattedCharSequence singleChar = singleCharComponent.getVisualOrderText();
                  float x = baseX + i * charWidth;
                  this.font.drawInBatch(singleChar, x, currentY, 0, false, poseStack.last().pose(), bufferSource, DisplayMode.NORMAL, 0, packedLight);
               }
            }

            poseStack.popPose();
         }
      }
   }

   public boolean shouldRenderOffScreen(HorizontalBannerBlockEntity blockEntity) {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }
}
