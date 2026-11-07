package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalScrollBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.HorizontalScrollBlockEntityRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * 1.1.11 重构：只由分组锚点（owner）渲染整段文字，发光态用亮色 + 满亮度（同 {@link CoupletBlockEntityRender}）。
 * <p>
 * 1.2.0：按字形墨迹包围盒在格内水平居中，排版常量对齐官方
 * （文字大小 0.021、格宽 9.5、三档水平偏移 -0.5/15/31、垂直 1.5），渲染调参 ClientConfig 一并移除。
 */
public class HorizontalScrollBlockEntityRender implements BlockEntityRenderer<HorizontalScrollBlockEntity, HorizontalScrollBlockEntityRenderState> {
   private static final Identifier COUPLET_FONT = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   // 0xF0F0CC 是官方的发光色；26.x 的 Font 不再把 alpha=0 强制成不透明，必须显式带上 FF
   private static final int GLOW_COLOR = 0xFFF0F0CC;
   private static final int NORMAL_COLOR = 0xFF000000;
   private static final int GLOW_LIGHT = 15728880;
   private static final float TEXT_SCALE = 0.021F;
   private static final float SINGLE_OFFSET = -0.5F;
   private static final float DOUBLE_OFFSET = 15.0F;
   private static final float TRIPLE_OFFSET = 31.0F;
   private static final float VERTICAL_OFFSET = 1.5F;

   public HorizontalScrollBlockEntityRender(Context context) {
   }

   public HorizontalScrollBlockEntityRenderState createRenderState() {
      return new HorizontalScrollBlockEntityRenderState();
   }

   public void extractRenderState(
      HorizontalScrollBlockEntity blockEntity, HorizontalScrollBlockEntityRenderState state, float partialTick, Vec3 vec3, @Nullable CrumblingOverlay crumblingOverlay
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, vec3, crumblingOverlay);
      state.text = null;
      // 整段横批只由锚点（最左那一格）提交
      if (!blockEntity.getOwnerPos().equals(blockEntity.getBlockPos())) {
         return;
      }

      String firstLineText = blockEntity.getTruncatedLine(0);
      if (firstLineText != null && !firstLineText.isBlank()) {
         BlockState blockState = blockEntity.getBlockState();
         state.text = firstLineText;
         state.totalWidth = blockEntity.getSegmentCount();
         state.glowing = blockEntity.isGlowing();
         state.facing = blockState.getValue(com.bmt.kaleidoscope_chinesefood.block.HorizontalScrollBlock.FACING);
      }
   }

   public void submit(HorizontalScrollBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
      String text = state.text;
      if (text == null) {
         return;
      }

      poseStack.pushPose();
      poseStack.translate(0.5, 0.5, 0.5);
      poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
      poseStack.translate(0.0, 0.0, -0.48);
      poseStack.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
      float charWidth = 9.5F;
      float totalContentWidth = text.length() * charWidth;
      float unitCenterOffset = (state.totalWidth - 1) * 8.0F;
      float textCenterOffset = -totalContentWidth / 2.0F;
      float extraOffset;
      if (state.totalWidth == 1) {
         extraOffset = SINGLE_OFFSET;
      } else if (state.totalWidth == 2) {
         extraOffset = DOUBLE_OFFSET;
      } else {
         extraOffset = TRIPLE_OFFSET;
      }

      float baseX = unitCenterOffset + textCenterOffset + extraOffset;
      float currentY = VERTICAL_OFFSET;
      Style coupletStyle = Style.EMPTY.withFont(new FontDescription.Resource(COUPLET_FONT));
      int color = state.glowing ? GLOW_COLOR : NORMAL_COLOR;
      int light = state.glowing ? GLOW_LIGHT : state.lightCoords;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         FormattedCharSequence singleChar = Component.literal(String.valueOf(c)).withStyle(coupletStyle).getVisualOrderText();
         // 1.2.0：按字形墨迹包围盒在格内水平居中
         GlyphMetrics.InkBox ink = GlyphMetrics.measure(COUPLET_FONT, c);
         float cellStart = baseX + i * charWidth;
         float x = cellStart + (charWidth - ink.width()) / 2.0F - ink.left();
         submitNodeCollector.submitText(poseStack, x, currentY, singleChar, false, DisplayMode.POLYGON_OFFSET, light, color, 0, 0);
      }

      poseStack.popPose();
   }

   public boolean shouldRenderOffScreen() {
      return true;
   }

   public int getViewDistance() {
      return 96;
   }
}
