package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.block.HorizontalBannerBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.HorizontalBannerBlockEntity;
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

public class HorizontalBannerRender implements BlockEntityRenderer<HorizontalBannerBlockEntity> {
    private static final ResourceLocation COUPLET_FONT = KaleidoscopeChineseFood.id("couplet_font");
    private static final float TEXT_SCALE = 0.021F;
    private static final float SINGLE_OFFSET = -0.5F;
    private static final float DOUBLE_OFFSET = 15.0F;
    private static final float TRIPLE_OFFSET = 31.0F;
    private static final float VERTICAL_OFFSET = 1.5F;
    private final Font font;

    public HorizontalBannerRender(Context context) {
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
                float scale = 0.021F;
                poseStack.scale(scale, -scale, scale);
                float charWidth = 9.5F;
                float totalContentWidth = text.length() * 9.5F;
                float unitCenterOffset = (totalWidth - 1) * 8.0F;
                float textCenterOffset = -totalContentWidth / 2.0F;
                float extraOffset;
                if (totalWidth == 1) {
                    extraOffset = -0.5F;
                } else if (totalWidth == 2) {
                    extraOffset = 15.0F;
                } else {
                    extraOffset = 31.0F;
                }

                float baseX = unitCenterOffset + textCenterOffset + extraOffset;
                float currentY = 1.5F;
                Style coupletStyle = Style.EMPTY.withFont(COUPLET_FONT);
                int color = glowing ? 15789004 : 0;
                int light = glowing ? 15728880 : packedLight;

                for (int i = 0; i < text.length(); i++) {
                    FormattedCharSequence seq = Component.literal(String.valueOf(text.charAt(i))).withStyle(coupletStyle).getVisualOrderText();
                    float x = baseX + i * 9.5F;
                    if (glowing) {
                        this.font.drawInBatch8xOutline(seq, x, currentY, color, 0, poseStack.last().pose(), bufferSource, light);
                    } else {
                        this.font.drawInBatch(seq, x, currentY, color, false, poseStack.last().pose(), bufferSource, DisplayMode.POLYGON_OFFSET, 0, light);
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
