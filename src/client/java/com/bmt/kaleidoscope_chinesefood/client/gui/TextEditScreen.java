package com.bmt.kaleidoscope_chinesefood.client.gui;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.client.renderer.GlyphMetrics;
import com.bmt.kaleidoscope_chinesefood.network.TextEditUpdateC2SPayload;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

/**
 * 对联 / 横批文字编辑界面（1.1.11 新增）。
 * 官方用 NeoForge payload 提交，这里改用 ClientPlayNetworking。
 */
public class TextEditScreen extends Screen {
   private static final ResourceLocation COUPLET_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/couplet_edit.png");
   private static final ResourceLocation BANNER_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/banner_edit.png");
   private static final ResourceLocation BANNER_SINGLE_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/banner_edit_single.png");
   private static final int SEGMENT = 64;
   private static final int TEXT_COLOR = 0;
   private static final int COUNT_COLOR = 0xFFFFFF;
   private static final Style GUI_FONT_STYLE = Style.EMPTY.withFont(KaleidoscopeChineseFood.id("couplet_font"));
   private final BlockPos blockPos;
   private final int maxChars;
   private final boolean vertical;
   private final int segmentCount;
   private final String initialText;
   private EditBox inputBox;

   public TextEditScreen(BlockPos blockPos, String initialText, int maxChars, int segmentCount, boolean vertical) {
      super(Component.translatable("gui.kaleidoscope_chinesefood.text_edit.title").withStyle(GUI_FONT_STYLE));
      this.blockPos = blockPos;
      this.initialText = initialText == null ? "" : initialText;
      this.maxChars = maxChars;
      this.vertical = vertical;
      this.segmentCount = Math.max(1, Math.min(3, segmentCount));
   }

   private int panelWidth() {
      return this.vertical ? SEGMENT : SEGMENT * this.segmentCount;
   }

   private int panelHeight() {
      return this.vertical ? SEGMENT * this.segmentCount : SEGMENT;
   }

   private int panelY() {
      int offset = this.vertical ? -10 : -24;
      return this.height / 2 - this.panelHeight() / 2 + offset;
   }

   private int panelX() {
      return this.width / 2 - this.panelWidth() / 2;
   }

   protected void init() {
      int panelW = this.panelWidth();
      int panelH = this.panelHeight();
      int panelY = this.panelY();
      this.inputBox = new EditBox(
         this.font,
         this.panelX(),
         panelY,
         panelW,
         panelH,
         Component.translatable("gui.kaleidoscope_chinesefood.text_edit.placeholder").withStyle(GUI_FONT_STYLE)
      ) {
         public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
         }
      };
      this.inputBox.setMaxLength(this.maxChars);
      this.inputBox.setBordered(false);
      this.inputBox.setTextColor(TEXT_COLOR);
      this.inputBox.setFormatter((text, cursorPos) -> Component.literal(text).withStyle(GUI_FONT_STYLE).getVisualOrderText());
      this.inputBox.setValue(this.initialText);
      this.inputBox.setCursorPosition(this.initialText.length());
      this.inputBox.setHighlightPos(this.initialText.length());
      this.addRenderableWidget(this.inputBox);
      this.setInitialFocus(this.inputBox);
      int buttonWidth = 80;
      int buttonY = panelY + panelH + 6;
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose()).bounds(this.width / 2 - buttonWidth - 4, buttonY, buttonWidth, 20).build()
      );
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onDone()).bounds(this.width / 2 + 4, buttonY, buttonWidth, 20).build());
   }

   private void onDone() {
      String text = this.inputBox.getValue().replaceAll("[\r\n]", "");
      if (text.length() > this.maxChars) {
         text = text.substring(0, this.maxChars);
      }

      ClientPlayNetworking.send(new TextEditUpdateC2SPayload(this.blockPos, text));
      this.onClose();
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode != 257 && keyCode != 335) {
         return super.keyPressed(keyCode, scanCode, modifiers);
      } else {
         this.onDone();
         return true;
      }
   }

   public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.renderBackground(graphics, mouseX, mouseY, partialTick);
      this.renderPaper(graphics, this.panelX(), this.panelY());
   }

   private void renderPaper(GuiGraphics graphics, int x, int y) {
      if (this.vertical) {
         int texW = 64;
         int texH = 192;
         int drawH = SEGMENT * this.segmentCount;
         if (this.segmentCount == 3) {
            graphics.blit(COUPLET_TEXTURE, x, y, 0, 0.0F, 0.0F, SEGMENT, drawH, texW, texH);
         } else if (this.segmentCount == 2) {
            graphics.blit(COUPLET_TEXTURE, x, y, 0, 0.0F, 0.0F, SEGMENT, SEGMENT, texW, texH);
            graphics.blit(COUPLET_TEXTURE, x, y + SEGMENT, 0, 0.0F, 128.0F, SEGMENT, SEGMENT, texW, texH);
         } else {
            graphics.blit(COUPLET_TEXTURE, x, y, 0, 0.0F, 0.0F, SEGMENT, 32, texW, texH);
            graphics.blit(COUPLET_TEXTURE, x, y + 32, 0, 0.0F, 160.0F, SEGMENT, 32, texW, texH);
         }
      } else {
         int texW = 192;
         int texH = 64;
         int drawW = SEGMENT * this.segmentCount;
         if (this.segmentCount == 3) {
            graphics.blit(BANNER_TEXTURE, x, y, 0, 0.0F, 0.0F, drawW, SEGMENT, texW, texH);
         } else if (this.segmentCount == 2) {
            graphics.blit(BANNER_TEXTURE, x, y, 0, 0.0F, 0.0F, SEGMENT, SEGMENT, texW, texH);
            graphics.blit(BANNER_TEXTURE, x + SEGMENT, y, 0, 128.0F, 0.0F, SEGMENT, SEGMENT, texW, texH);
         } else {
            graphics.blit(BANNER_SINGLE_TEXTURE, x, y, 0, 0.0F, 0.0F, SEGMENT, SEGMENT, SEGMENT, SEGMENT);
         }
      }
   }

   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
      super.render(graphics, mouseX, mouseY, partialTick);
      int panelW = this.panelWidth();
      int panelH = this.panelHeight();
      int panelX = this.panelX();
      int panelY = this.panelY();
      if (this.vertical) {
         this.renderVerticalText(graphics, panelX, panelY, panelW, panelH);
      } else {
         this.renderHorizontalText(graphics, panelX, panelY, panelW, panelH);
      }

      Component count = Component.literal(this.inputBox.getValue().length() + "/" + this.maxChars).withStyle(GUI_FONT_STYLE);
      int countX;
      int countY;
      if (this.vertical) {
         countX = panelX + panelW - this.font.width(count) + 10;
         countY = panelY + panelH - 11;
      } else {
         countX = panelX + panelW - this.font.width(count) - 4;
         countY = panelY + panelH - 9;
      }

      graphics.drawString(this.font, count, countX, countY, COUNT_COLOR, false);
   }

   /** 1.2.0:编辑器预览按字形墨迹包围盒逐字居中(字号 1.15、行距 12×s)。 */
   private void renderVerticalText(GuiGraphics graphics, int panelX, int panelY, int panelW, int panelH) {
      String text = this.inputBox.getValue();
      if (text.isEmpty()) {
         return;
      }

      float s = 1.15F;
      float cellHeight = 12.0F * s;
      float total = (float)text.length() * cellHeight;
      float startY = (float)panelY + ((float)panelH - total) / 2.0F - 3.5F;
      float cx = (float)panelX + (float)panelW / 2.0F;
      PoseStack pose = graphics.pose();

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         FormattedCharSequence seq = Component.literal(String.valueOf(c)).withStyle(GUI_FONT_STYLE).getVisualOrderText();
         GlyphMetrics.InkBox ink = GlyphMetrics.measure(this.font, KaleidoscopeChineseFood.id("couplet_font"), c);
         float cellCenterY = startY + (float)i * cellHeight + cellHeight / 2.0F;
         float drawX = -(ink.left() + ink.right()) / 2.0F;
         float drawY = -(ink.up() - 3.0F) - ink.height() / 2.0F;
         pose.pushPose();
         pose.translate(cx, cellCenterY, 0.0F);
         pose.scale(s, s, 1.0F);
         // 1.21.1 Fabric GuiGraphics.drawString 仅 int 版,浮点位移经 pose.translate 保留
         pose.translate(drawX, drawY, 0.0F);
         graphics.drawString(this.font, seq, 0, 0, TEXT_COLOR, false);
         pose.popPose();
      }
   }

   /** 1.2.0:编辑器预览按字形墨迹包围盒在格内水平居中(格宽 11.5×s)。 */
   private void renderHorizontalText(GuiGraphics graphics, int panelX, int panelY, int panelW, int panelH) {
      String text = this.inputBox.getValue();
      if (text.isEmpty()) {
         return;
      }

      float s = 1.15F;
      float cellWidth = 11.5F * s;
      float totalW = (float)text.length() * cellWidth;
      float startX = (float)panelX + ((float)panelW - totalW) / 2.0F;
      float cy = (float)panelY + (float)panelH / 2.0F - 3.5F;
      PoseStack pose = graphics.pose();

      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         FormattedCharSequence seq = Component.literal(String.valueOf(c)).withStyle(GUI_FONT_STYLE).getVisualOrderText();
         GlyphMetrics.InkBox ink = GlyphMetrics.measure(this.font, KaleidoscopeChineseFood.id("couplet_font"), c);
         float cellCenterX = startX + (float)i * cellWidth + cellWidth / 2.0F;
         float drawX = -(ink.left() + ink.right()) / 2.0F;
         float drawY = -(ink.up() - 3.0F) - ink.height() / 2.0F;
         pose.pushPose();
         pose.translate(cellCenterX, cy, 0.0F);
         pose.scale(s, s, 1.0F);
         pose.translate(drawX, drawY, 0.0F);
         graphics.drawString(this.font, seq, 0, 0, TEXT_COLOR, false);
         pose.popPose();
      }
   }

   public boolean isPauseScreen() {
      return false;
   }
}
