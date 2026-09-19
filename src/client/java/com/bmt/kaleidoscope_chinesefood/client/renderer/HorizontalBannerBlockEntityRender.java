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
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

/**
 * 1.1.11 重构：只由分组锚点（owner）渲染整段文字，发光态走 8x 描边。
 * 渲染参数沿用移植版既有的 ClientConfig 滑条（默认值已对齐官方 1.1.11 调参）。
 */
public class HorizontalBannerBlockEntityRender implements BlockEntityRenderer<HorizontalBannerBlockEntity> {
   private static final ResourceLocation COUPLET_FONT = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   private static final int GLOW_COLOR = 15789004;
   private static final int GLOW_LIGHT = 15728880;
   private final Font font;

   public HorizontalBannerBlockEntityRender(Context context) {
      this.font = context.getFont();
   }

   public void render(
      HorizontalBannerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      BlockState state = blockEntity.getBlockState();
      if (blockEntity.getOwnerPos().equals(blockEntity.getBlockPos())) {
         String text = blockEntity.getTruncatedLine(0);
         if (text != null && !text.isBlank()) {
            int totalWidth = blockEntity.getSegmentCount();
            boolean glowing = blockEntity.isGlowing();
            Direction facing = (Direction)state.getValue(HorizontalBannerBlock.FACING);
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            poseStack.translate(0.0, 0.0, -0.48);
            float scale = ClientConfig.BANNER_TEXT_SCALE;
            poseStack.scale(scale, -scale, scale);
            float charWidth = ClientConfig.BANNER_CHAR_WIDTH;
            float totalContentWidth = text.length() * charWidth;
            float unitCenterOffset = (totalWidth - 1) * 8.0F;
            float textCenterOffset = -totalContentWidth / 2.0F;
            float extraOffset = switch (totalWidth) {
               case 1 -> ClientConfig.BANNER_SINGLE_OFFSET;
               case 2 -> ClientConfig.BANNER_DOUBLE_OFFSET;
               default -> ClientConfig.BANNER_TRIPLE_OFFSET;
            };
            float baseX = unitCenterOffset + textCenterOffset + extraOffset;
            float currentY = ClientConfig.BANNER_VERTICAL_OFFSET;
            Style style = Style.EMPTY.withFont(COUPLET_FONT);
            int color = glowing ? GLOW_COLOR : 0;
            int light = glowing ? GLOW_LIGHT : packedLight;

            for (int i = 0; i < text.length(); i++) {
               FormattedCharSequence seq = Component.literal(String.valueOf(text.charAt(i))).withStyle(style).getVisualOrderText();
               float x = baseX + i * charWidth;
               Matrix4f pose = poseStack.last().pose();
               if (glowing) {
                  this.font.drawInBatch8xOutline(seq, x, currentY, color, 0, pose, bufferSource, light);
               } else {
                  this.font.drawInBatch(seq, x, currentY, color, false, pose, bufferSource, DisplayMode.POLYGON_OFFSET, 0, light);
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
