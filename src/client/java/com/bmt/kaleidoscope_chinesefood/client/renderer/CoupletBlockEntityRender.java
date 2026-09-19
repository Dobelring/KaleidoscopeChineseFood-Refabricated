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
public class CoupletBlockEntityRender implements BlockEntityRenderer<CoupletBlockEntity> {
   private static final ResourceLocation COUPLET_FONT = ResourceLocation.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   private static final int GLOW_COLOR = 15789004;
   private static final int GLOW_LIGHT = 15728880;
   private final Font font;

   public CoupletBlockEntityRender(Context context) {
      this.font = context.getFont();
   }

   public void render(
      CoupletBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay
   ) {
      BlockState state = blockEntity.getBlockState();
      if (blockEntity.getOwnerPos().equals(blockEntity.getBlockPos())) {
         String text = blockEntity.getTruncatedLine(0);
         if (text != null && !text.isBlank()) {
            boolean isTriple = blockEntity.getSegmentCount() >= 3;
            boolean glowing = blockEntity.isGlowing();
            Direction facing = (Direction)state.getValue(CoupletBlock.FACING);
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
            poseStack.translate(0.0, 0.0, -0.48);
            float scale = ClientConfig.COUPLET_TEXT_SCALE;
            poseStack.scale(scale, -scale, scale);
            float wordHeight = ClientConfig.COUPLET_VERTICAL_SPACING;
            float totalContentHeight = text.length() * wordHeight;
            float baseY = isTriple ? ClientConfig.COUPLET_TRIPLE_BASE_Y : ClientConfig.COUPLET_DOUBLE_BASE_Y;
            float currentY = baseY - totalContentHeight / 2.0F;
            Style style = Style.EMPTY.withFont(COUPLET_FONT);
            int color = glowing ? GLOW_COLOR : 0;
            int light = glowing ? GLOW_LIGHT : packedLight;

            for (int i = 0; i < text.length(); i++) {
               FormattedCharSequence seq = Component.literal(String.valueOf(text.charAt(i))).withStyle(style).getVisualOrderText();
               float x = -this.font.width(seq) / 2.0F + ClientConfig.COUPLET_HORIZONTAL_OFFSET;
               float y = currentY + i * wordHeight;
               Matrix4f pose = poseStack.last().pose();
               if (glowing) {
                  this.font.drawInBatch8xOutline(seq, x, y, color, 0, pose, bufferSource, light);
               } else {
                  this.font.drawInBatch(seq, x, y, color, false, pose, bufferSource, DisplayMode.POLYGON_OFFSET, 0, light);
               }
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
