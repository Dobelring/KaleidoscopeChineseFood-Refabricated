package com.bmt.kaleidoscope_chinesefood.client.renderer.block;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.client.renderer.GlyphMetrics;
import com.bmt.kaleidoscope_chinesefood.block.CoupletBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.CoupletBlockEntity;
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

public class CoupletBlockEntityRender implements BlockEntityRenderer<CoupletBlockEntity> {
    private static final ResourceLocation COUPLET_FONT = KaleidoscopeChineseFood.id("couplet_font");
    private static final float TEXT_SCALE = 0.021F;
    private static final float VERTICAL_SPACING = 9.0F;
    private static final float DOUBLE_BASE_Y = -25.5F;
    private static final float TRIPLE_BASE_Y = -48.5F;
    private static final float HORIZONTAL_OFFSET = 0.2F;
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
                float scale = 0.024F;
                poseStack.scale(scale, -scale, scale);
                float wordHeight = 9.3F;
                float totalContentHeight = text.length() * wordHeight;
                float baseY = isTriple ? -43.0F : -22.0F;
                float currentY = baseY - totalContentHeight / 2.0F;
                Style coupletStyle = Style.EMPTY.withFont(COUPLET_FONT);
                int color = glowing ? 15789004 : 0;
                int light = glowing ? 15728880 : packedLight;

                for (int i = 0; i < text.length(); i++) {
                    char c = text.charAt(i);
                    FormattedCharSequence seq = Component.literal(String.valueOf(c)).withStyle(coupletStyle).getVisualOrderText();
                    // 1.2.0：按字形墨迹包围盒水平居中、按行高垂直居中
                    GlyphMetrics.InkBox ink = GlyphMetrics.measure(this.font, COUPLET_FONT, c);
                    float x = -(ink.left() + ink.right()) / 2.0F + 0.2F;
                    float cellTop = currentY + i * wordHeight;
                    float y = cellTop + (wordHeight - ink.height()) / 2.0F - (ink.up() - 3.0F);
                    if (glowing) {
                        this.font.drawInBatch8xOutline(seq, x, y, color, 0, poseStack.last().pose(), bufferSource, light);
                    } else {
                        this.font.drawInBatch(seq, x, y, color, false, poseStack.last().pose(), bufferSource, DisplayMode.POLYGON_OFFSET, 0, light);
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
