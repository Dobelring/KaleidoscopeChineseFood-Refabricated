package com.bmt.kaleidoscope_chinesefood.client.gui;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.network.TextEditUpdateC2SPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

/**
 * 对联 / 横批文字编辑界面（1.1.11 新增）。
 * <p>
 * 官方用 NeoForge payload 提交，这里改用 ClientPlayNetworking；
 * 26.1.2 的 GUI 是"提取模型"，绘制入口是 {@code extractRenderState}/{@code extractBackground}，
 * 坐标变换走 2D 的 {@link Matrix3x2fStack}。
 */
public class TextEditScreen extends Screen {
    private static final Identifier COUPLET_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/couplet_edit.png");
    private static final Identifier BANNER_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/banner_edit.png");
    private static final Identifier BANNER_SINGLE_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/banner_edit_single.png");
    private static final int SEGMENT = 64;
    // 26.x 的 Font 不再把 alpha=0 的颜色强制成不透明（1.21.1 会），必须显式给不透明黑
    private static final int TEXT_COLOR = 0xFF000000;
    // 26.x 的 Style.withFont 收 FontDescription 而不是 Identifier
    private static final Style GUI_FONT_STYLE = Style.EMPTY.withFont(
            new FontDescription.Resource(KaleidoscopeChineseFood.id("couplet_font"))
    );

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

    @Override
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
            // 输入框自身不画任何东西，文字由本界面按竖排/横排规则绘制
            @Override
            public void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            }
        };
        this.inputBox.setMaxLength(this.maxChars);
        this.inputBox.setBordered(false);
        this.inputBox.setTextColor(TEXT_COLOR);
        this.inputBox.addFormatter((text, cursorPos) -> Component.literal(text).withStyle(GUI_FONT_STYLE).getVisualOrderText());
        this.inputBox.setValue(this.initialText);
        this.inputBox.setCursorPosition(this.initialText.length());
        this.inputBox.setHighlightPos(this.initialText.length());
        this.addRenderableWidget(this.inputBox);
        this.setInitialFocus(this.inputBox);

        int buttonWidth = 80;
        int buttonY = panelY + panelH + 6;
        this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose())
                        .pos(this.width / 2 - buttonWidth - 4, buttonY)
                        .size(buttonWidth, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(CommonComponents.GUI_DONE, b -> this.onDone())
                        .pos(this.width / 2 + 4, buttonY)
                        .size(buttonWidth, 20)
                        .build()
        );
    }

    private void onDone() {
        String text = this.inputBox.getValue().replaceAll("[\r\n]", "");
        if (text.length() > this.maxChars) {
            text = text.substring(0, this.maxChars);
        }

        ClientPlayNetworking.send(new TextEditUpdateC2SPayload(this.blockPos, text));
        this.onClose();
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        int keyCode = event.key();
        if (keyCode != 257 && keyCode != 335) {
            return super.keyPressed(event);
        }

        this.onDone();
        return true;
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        this.renderPaper(graphics, this.panelX(), this.panelY());
    }

    private void renderPaper(GuiGraphicsExtractor graphics, int x, int y) {
        if (this.vertical) {
            int texW = 64;
            int texH = 192;
            int drawH = SEGMENT * this.segmentCount;
            if (this.segmentCount == 3) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, COUPLET_TEXTURE, x, y, 0.0F, 0.0F, SEGMENT, drawH, texW, texH);
            } else if (this.segmentCount == 2) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, COUPLET_TEXTURE, x, y, 0.0F, 0.0F, SEGMENT, SEGMENT, texW, texH);
                graphics.blit(RenderPipelines.GUI_TEXTURED, COUPLET_TEXTURE, x, y + SEGMENT, 0.0F, 128.0F, SEGMENT, SEGMENT, texW, texH);
            } else {
                graphics.blit(RenderPipelines.GUI_TEXTURED, COUPLET_TEXTURE, x, y, 0.0F, 0.0F, SEGMENT, 32, texW, texH);
                graphics.blit(RenderPipelines.GUI_TEXTURED, COUPLET_TEXTURE, x, y + 32, 0.0F, 160.0F, SEGMENT, 32, texW, texH);
            }
        } else {
            int texW = 192;
            int texH = 64;
            int drawW = SEGMENT * this.segmentCount;
            if (this.segmentCount == 3) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BANNER_TEXTURE, x, y, 0.0F, 0.0F, drawW, SEGMENT, texW, texH);
            } else if (this.segmentCount == 2) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BANNER_TEXTURE, x, y, 0.0F, 0.0F, SEGMENT, SEGMENT, texW, texH);
                graphics.blit(RenderPipelines.GUI_TEXTURED, BANNER_TEXTURE, x + SEGMENT, y, 128.0F, 0.0F, SEGMENT, SEGMENT, texW, texH);
            } else {
                graphics.blit(RenderPipelines.GUI_TEXTURED, BANNER_SINGLE_TEXTURE, x, y, 0.0F, 0.0F, SEGMENT, SEGMENT, SEGMENT, SEGMENT);
            }
        }
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
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

        graphics.text(this.font, count, countX, countY, TEXT_COLOR, false);
    }

    private void renderVerticalText(GuiGraphicsExtractor graphics, int panelX, int panelY, int panelW, int panelH) {
        String text = this.inputBox.getValue();
        int spacing = 13;
        FormattedCharSequence[] seqs = new FormattedCharSequence[text.length()];

        for (int i = 0; i < text.length(); i++) {
            seqs[i] = Component.literal(String.valueOf(text.charAt(i))).withStyle(GUI_FONT_STYLE).getVisualOrderText();
        }

        int total = seqs.length * spacing;
        int startY = panelY + (panelH - total) / 2 + 4;
        int cx = panelX + panelW / 2 + 1;
        Matrix3x2fStack pose = graphics.pose();

        for (int i = 0; i < seqs.length; i++) {
            int w = this.font.width(seqs[i]);
            pose.pushMatrix();
            pose.translate(cx, startY + i * spacing);
            graphics.text(this.font, seqs[i], -w / 2, 0, TEXT_COLOR, false);
            pose.popMatrix();
        }
    }

    private void renderHorizontalText(GuiGraphicsExtractor graphics, int panelX, int panelY, int panelW, int panelH) {
        String text = this.inputBox.getValue();
        FormattedCharSequence[] seqs = new FormattedCharSequence[text.length()];
        int totalW = 0;

        for (int i = 0; i < text.length(); i++) {
            seqs[i] = Component.literal(String.valueOf(text.charAt(i))).withStyle(GUI_FONT_STYLE).getVisualOrderText();
            totalW += this.font.width(seqs[i]);
        }

        int startX = panelX + panelW / 2 - totalW / 2 + 1;
        int y = panelY + panelH / 2 - 9 / 2 + 1;
        Matrix3x2fStack pose = graphics.pose();
        int runX = startX;

        for (FormattedCharSequence seq : seqs) {
            int w = this.font.width(seq);
            pose.pushMatrix();
            pose.translate(runX, y);
            graphics.text(this.font, seq, 0, 0, TEXT_COLOR, false);
            pose.popMatrix();
            runX += w;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
