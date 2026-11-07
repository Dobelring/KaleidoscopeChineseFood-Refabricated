package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.block.CoupletBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate.CoupletBlockEntityRenderState;
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
 * 1.1.11 重构：只由分组锚点（owner）渲染整段文字，发光态用亮色 + 满亮度。
 * <p>
 * 官方 1.1.11 的发光分支调 {@code drawInBatch8xOutline(seq, x, y, GLOW_COLOR, 0, ...)}，
 * 描边色传 0（不可见），实际观感来自 GLOW_COLOR + GLOW_LIGHT，所以这里同样只改色与亮度、
 * 描边色保持 0（26.x 的 submitText 只在 outlineColor≠0 时才走 8x 描边）。
 * <p>
 * 1.2.0：按字形墨迹包围盒逐字居中（字号/行距同时调整），排版常量对齐官方
 * （文字大小 0.024、行距 9.3、双联/三联基线 -22/-43），渲染调参 ClientConfig 一并移除。
 */
public class CoupletBlockEntityRender implements BlockEntityRenderer<CoupletBlockEntity, CoupletBlockEntityRenderState> {
   private static final Identifier COUPLET_FONT = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   // 0xF0F0CC 是官方的发光色；26.x 的 Font 不再把 alpha=0 强制成不透明，必须显式带上 FF
   private static final int GLOW_COLOR = 0xFFF0F0CC;
   private static final int NORMAL_COLOR = 0xFF000000;
   private static final int GLOW_LIGHT = 15728880;
   private static final float TEXT_SCALE = 0.024F;
   private static final float VERTICAL_SPACING = 9.3F;
   private static final float DOUBLE_BASE_Y = -22.0F;
   private static final float TRIPLE_BASE_Y = -43.0F;
   private static final float HORIZONTAL_OFFSET = 0.2F;

   public CoupletBlockEntityRender(Context context) {
   }

   public CoupletBlockEntityRenderState createRenderState() {
      return new CoupletBlockEntityRenderState();
   }

   public void extractRenderState(
      CoupletBlockEntity blockEntity, CoupletBlockEntityRenderState state, float partialTick, Vec3 vec3, @Nullable CrumblingOverlay crumblingOverlay
   ) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, vec3, crumblingOverlay);
      state.text = null;
      // 整组文字只由锚点那一格提交，其余格什么都不画
      if (!blockEntity.getOwnerPos().equals(blockEntity.getBlockPos())) {
         return;
      }

      String firstLineText = blockEntity.getTruncatedLine(0);
      if (firstLineText != null && !firstLineText.isBlank()) {
         BlockState blockState = blockEntity.getBlockState();
         state.text = firstLineText;
         state.isTriple = blockEntity.getSegmentCount() >= 3;
         state.glowing = blockEntity.isGlowing();
         state.facing = blockState.getValue(CoupletBlock.FACING);
      }
   }

   public void submit(CoupletBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
      String text = state.text;
      if (text == null) {
         return;
      }

      poseStack.pushPose();
      poseStack.translate(0.5, 0.5, 0.5);
      poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
      poseStack.translate(0.0, 0.0, -0.48);
      poseStack.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
      float wordHeight = VERTICAL_SPACING;
      float totalContentHeight = text.length() * wordHeight;
      float baseY = state.isTriple ? TRIPLE_BASE_Y : DOUBLE_BASE_Y;
      float currentY = baseY - totalContentHeight / 2.0F;
      Style coupletStyle = Style.EMPTY.withFont(new FontDescription.Resource(COUPLET_FONT));
      int color = state.glowing ? GLOW_COLOR : NORMAL_COLOR;
      int light = state.glowing ? GLOW_LIGHT : state.lightCoords;

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         FormattedCharSequence singleChar = Component.literal(String.valueOf(c)).withStyle(coupletStyle).getVisualOrderText();
         // 1.2.0：按字形墨迹包围盒水平居中、按行高垂直居中
         GlyphMetrics.InkBox ink = GlyphMetrics.measure(COUPLET_FONT, c);
         float x = -(ink.left() + ink.right()) / 2.0F + HORIZONTAL_OFFSET;
         float cellTop = currentY + i * wordHeight;
         float y = cellTop + (wordHeight - ink.height()) / 2.0F - (ink.up() - 3.0F);
         submitNodeCollector.submitText(poseStack, x, y, singleChar, false, DisplayMode.POLYGON_OFFSET, light, color, 0, 0);
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
