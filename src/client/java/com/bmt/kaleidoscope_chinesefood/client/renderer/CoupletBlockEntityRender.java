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
 * 渲染参数沿用移植版既有的 ClientConfig 滑条（默认值已对齐官方 1.1.11 调参）。
 */
public class CoupletBlockEntityRender implements BlockEntityRenderer<CoupletBlockEntity, CoupletBlockEntityRenderState> {
   private static final Identifier COUPLET_FONT = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   // 0xF0F0CC 是官方的发光色；26.x 的 Font 不再把 alpha=0 强制成不透明，必须显式带上 FF
   private static final int GLOW_COLOR = 0xFFF0F0CC;
   private static final int NORMAL_COLOR = 0xFF000000;
   private static final int GLOW_LIGHT = 15728880;
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
      poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
      poseStack.translate(0.0, 0.0, -0.48);
      float scale = ClientConfig.COUPLET_TEXT_SCALE;
      poseStack.scale(scale, -scale, scale);
      float wordHeight = ClientConfig.COUPLET_VERTICAL_SPACING;
      float totalContentHeight = text.length() * wordHeight;
      float baseY = state.isTriple ? ClientConfig.COUPLET_TRIPLE_BASE_Y : ClientConfig.COUPLET_DOUBLE_BASE_Y;
      float currentY = baseY - totalContentHeight / 2.0F;
      Style coupletStyle = Style.EMPTY.withFont(new FontDescription.Resource(COUPLET_FONT));
      int color = state.glowing ? GLOW_COLOR : NORMAL_COLOR;
      int light = state.glowing ? GLOW_LIGHT : state.lightCoords;

      for (int i = 0; i < text.length(); i++) {
         FormattedCharSequence singleChar = Component.literal(String.valueOf(text.charAt(i))).withStyle(coupletStyle).getVisualOrderText();
         float x = -this.font.width(singleChar) / 2.0F + ClientConfig.COUPLET_HORIZONTAL_OFFSET;
         float y = currentY + i * wordHeight;
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
