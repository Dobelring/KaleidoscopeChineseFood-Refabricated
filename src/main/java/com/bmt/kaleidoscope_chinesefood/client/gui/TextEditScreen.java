package com.bmt.kaleidoscope_chinesefood.client.gui;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.client.renderer.GlyphMetrics;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.IWallTextBlockEntity;
import com.bmt.kaleidoscope_chinesefood.network.TextEditUpdateC2SPacket;
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

public class TextEditScreen extends Screen {
    private static final ResourceLocation COUPLET_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/couplet_edit.png");
    private static final ResourceLocation BANNER_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/banner_edit.png");
    private static final ResourceLocation BANNER_SINGLE_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/banner_edit_single.png");
    private static final int SEGMENT = 64;
    private static final int TEXT_COLOR = 0;
    private static final Style GUI_FONT_STYLE = Style.EMPTY.withFont(KaleidoscopeChineseFood.id("couplet_font"));
    private final BlockPos blockPos;
    private final int maxChars;
    private final boolean vertical;
    private final int segmentCount;
    private final String initialText;
    private EditBox inputBox;

    public TextEditScreen(BlockPos blockPos, IWallTextBlockEntity textBe) {
        super(Component.translatable("gui.kaleidoscope_chinesefood.text_edit.title").withStyle(GUI_FONT_STYLE));
        this.blockPos = blockPos;
        this.maxChars = textBe.getMaxChars();
        this.vertical = textBe instanceof CoupletBlockEntity;
        this.segmentCount = Math.max(1, Math.min(3, textBe.getSegmentCount()));
        this.initialText = textBe.getText();
    }

    private int panelWidth() {
        return this.vertical ? 64 : 64 * this.segmentCount;
    }

    private int panelHeight() {
        return this.vertical ? 64 * this.segmentCount : 64;
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
        this.inputBox.setTextColor(0);
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

        // 原 Forge：ModNetwork.sendToServer(new TextEditUpdateC2SPacket(...))
        // Fabric：改用 ClientPlayNetworking，发送前用 canSend 守卫
        if (ClientPlayNetworking.canSend(TextEditUpdateC2SPacket.TYPE)) {
            ClientPlayNetworking.send(new TextEditUpdateC2SPacket(this.blockPos, text));
        }

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

    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
        this.renderPaper(graphics, this.panelX(), this.panelY());
    }

    private void renderPaper(GuiGraphics graphics, int x, int y) {
        if (this.vertical) {
            int texW = 64;
            int texH = 192;
            int drawH = 64 * this.segmentCount;
            if (this.segmentCount == 3) {
                graphics.blit(COUPLET_TEXTURE, x, y, 0, 0.0F, 0.0F, 64, drawH, texW, texH);
            } else if (this.segmentCount == 2) {
                graphics.blit(COUPLET_TEXTURE, x, y, 0, 0.0F, 0.0F, 64, 64, texW, texH);
                graphics.blit(COUPLET_TEXTURE, x, y + 64, 0, 0.0F, 128.0F, 64, 64, texW, texH);
            } else {
                graphics.blit(COUPLET_TEXTURE, x, y, 0, 0.0F, 0.0F, 64, 32, texW, texH);
                graphics.blit(COUPLET_TEXTURE, x, y + 32, 0, 0.0F, 160.0F, 64, 32, texW, texH);
            }
        } else {
            int texW = 192;
            int texH = 64;
            int drawW = 64 * this.segmentCount;
            if (this.segmentCount == 3) {
                graphics.blit(BANNER_TEXTURE, x, y, 0, 0.0F, 0.0F, drawW, 64, texW, texH);
            } else if (this.segmentCount == 2) {
                graphics.blit(BANNER_TEXTURE, x, y, 0, 0.0F, 0.0F, 64, 64, texW, texH);
                graphics.blit(BANNER_TEXTURE, x + 64, y, 0, 128.0F, 0.0F, 64, 64, texW, texH);
            } else {
                graphics.blit(BANNER_SINGLE_TEXTURE, x, y, 0, 0.0F, 0.0F, 64, 64, 64, 64);
            }
        }
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
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

        graphics.drawString(this.font, count, countX, countY, 16777215, false);
    }

    private void renderVerticalText(GuiGraphics graphics, int panelX, int panelY, int panelW, int panelH) {
        String text = this.inputBox.getValue();
        if (text.isEmpty()) {
            return;
        }
        // 1.2.0：按字形墨迹包围盒逐字居中，缩放 1.2 -> 1.4
        float s = 1.4F;
        float cellHeight = 10.0F * s;
        float total = (float)text.length() * cellHeight;
        float startY = (float)panelY + ((float)panelH - total) / 2.0F;
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
            graphics.drawString(this.font, seq, (int)drawX, (int)drawY, 0, false);
            pose.popPose();
        }
    }

    private void renderHorizontalText(GuiGraphics graphics, int panelX, int panelY, int panelW, int panelH) {
        String text = this.inputBox.getValue();
        if (text.isEmpty()) {
            return;
        }
        float s = 1.4F;
        float cellWidth = 9.5F * s;
        float totalW = (float)text.length() * cellWidth;
        float startX = (float)panelX + ((float)panelW - totalW) / 2.0F;
        float cy = (float)panelY + (float)panelH / 2.0F;
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
            graphics.drawString(this.font, seq, (int)drawX, (int)drawY, 0, false);
            pose.popPose();
        }
    }

    public boolean isPauseScreen() {
        return false;
    }
}
