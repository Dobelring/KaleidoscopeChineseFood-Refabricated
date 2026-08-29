package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.HorizontalBannerBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalBannerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.HorizontalBannerBlockEntityRenderState;
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

public class HorizontalBannerBlockEntityRender implements BlockEntityRenderer<HorizontalBannerBlockEntity, HorizontalBannerBlockEntityRenderState> {
   private static final Identifier COUPLET_FONT = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   private final Font font;

   public HorizontalBannerBlockEntityRender(Context context) {
      this.font = context.font();
   }

   public HorizontalBannerBlockEntityRenderState createRenderState() {
      return new HorizontalBannerBlockEntityRenderState();
   }

   public void extractRenderState(
      HorizontalBannerBlockEntity blockEntity, HorizontalBannerBlockEntityRenderState state, float partialTick, Vec3 vec3, @Nullable CrumblingOverlay crumblingOverlay
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, vec3, crumblingOverlay);
      state.text = null;
      BlockState blockState = blockEntity.getBlockState();
      if (blockState.getValue(HorizontalBannerBlock.PART) == HorizontalBannerBlock.BannerPart.SINGLE
         || blockState.getValue(HorizontalBannerBlock.PART) == HorizontalBannerBlock.BannerPart.LEFT
      ) {
         String firstLineText = blockEntity.getTruncatedLine(0);
         if (firstLineText != null && !firstLineText.isBlank()) {
            int totalWidth = 1;
            Direction facing = (Direction)blockState.getValue(HorizontalBannerBlock.FACING);

            for (BlockPos currentPos = blockEntity.getBlockPos().relative(facing.getCounterClockWise());
               blockEntity.getLevel() != null
                  && blockEntity.getLevel().getBlockState(currentPos).is(blockEntity.getBlockState().getBlock())
                  && blockEntity.getLevel().getBlockState(currentPos).getValue(HorizontalBannerBlock.FACING) == facing;
               currentPos = currentPos.relative(facing.getCounterClockWise())
            ) {
               totalWidth++;
            }

            state.text = firstLineText;
            state.totalWidth = totalWidth;
            state.facing = facing;
         }
      }
   }

   public void submit(HorizontalBannerBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
      String text = state.text;
      if (text != null) {
         poseStack.pushPose();
         poseStack.translate(0.5, 0.5, 0.5);
         poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
         poseStack.translate(0.0, 0.0, -0.48);
         float scale = ClientConfig.BANNER_TEXT_SCALE;
         poseStack.scale(scale, -scale, scale);
         float charWidth = ClientConfig.BANNER_CHAR_WIDTH;
         float totalContentWidth = text.length() * charWidth;
         float unitCenterOffset = (state.totalWidth - 1) * 8.0F;
         float textCenterOffset = -totalContentWidth / 2.0F;
         float extraOffset;
         if (state.totalWidth == 1) {
            extraOffset = ClientConfig.BANNER_SINGLE_OFFSET;
         } else if (state.totalWidth == 2) {
            extraOffset = ClientConfig.BANNER_DOUBLE_OFFSET;
         } else {
            extraOffset = ClientConfig.BANNER_TRIPLE_OFFSET;
         }

         float baseX = unitCenterOffset + textCenterOffset + extraOffset;
         float currentY = ClientConfig.BANNER_VERTICAL_OFFSET;
         Style coupletStyle = Style.EMPTY.withFont(new FontDescription.Resource(COUPLET_FONT));
         int charCount = text.length();
         if (charCount > 0) {
            for (int i = 0; i < charCount; i++) {
               String singleCharStr = String.valueOf(text.charAt(i));
               Component singleCharComponent = Component.literal(singleCharStr).withStyle(coupletStyle);
               FormattedCharSequence singleChar = singleCharComponent.getVisualOrderText();
               float x = baseX + i * charWidth;
               submitNodeCollector.submitText(poseStack, x, currentY, singleChar, false, DisplayMode.NORMAL, state.lightCoords, 0, 0, 0);
            }
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
