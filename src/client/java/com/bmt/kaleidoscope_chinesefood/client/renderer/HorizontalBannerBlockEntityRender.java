package com.bmt.kaleidoscope_chinesefood.client.renderer;

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
 */
public class HorizontalBannerBlockEntityRender implements BlockEntityRenderer<HorizontalBannerBlockEntity, HorizontalBannerBlockEntityRenderState> {
   private static final Identifier COUPLET_FONT = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "couplet_font");
   // 0xF0F0CC 是官方的发光色；26.x 的 Font 不再把 alpha=0 强制成不透明，必须显式带上 FF
   private static final int GLOW_COLOR = 0xFFF0F0CC;
   private static final int NORMAL_COLOR = 0xFF000000;
   private static final int GLOW_LIGHT = 15728880;
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
         state.facing = blockState.getValue(com.bmt.kaleidoscope_chinesefood.block.HorizontalBannerBlock.FACING);
      }
   }

   public void submit(HorizontalBannerBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
      String text = state.text;
      if (text == null) {
         return;
      }

      poseStack.pushPose();
      poseStack.translate(0.5, 0.5, 0.5);
      poseStack.rotateDegrees(Axis.YP, -state.facing.toYRot());
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
      int color = state.glowing ? GLOW_COLOR : NORMAL_COLOR;
      int light = state.glowing ? GLOW_LIGHT : state.lightCoords;

      for (int i = 0; i < text.length(); i++) {
         FormattedCharSequence singleChar = Component.literal(String.valueOf(text.charAt(i))).withStyle(coupletStyle).getVisualOrderText();
         float x = baseX + i * charWidth;
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
