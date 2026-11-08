package com.bmt.kaleidoscope_chinesefood.client.renderer.block;

import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 官方 1.1.16 重构：随机散布 → 确定性圆环布局（种子=方块坐标），显示数量按存量映射，
 * 仅开盖（OPEN）时渲染，视距降为 32。
 */
public class PickleJarBlockEntityRender implements BlockEntityRenderer<PickleJarBlockEntity> {
    private static final float[] LAYER_BASE_Y = new float[]{0.2F, 0.3F, 0.4F, 0.5F};
    private static final int[] RENDER_COUNTS = new int[]{0, 1, 2, 3, 6};
    private static final float LAYER_RADIUS = 0.105F;
    private static final float BASE_SCALE = 0.28F;
    private static final float SCALE_VARIATION = 0.02F;
    private static final float HEIGHT_JITTER = 0.012F;
    private static final int VIEW_DISTANCE = 32;
    private final ItemRenderer itemRenderer;

    public PickleJarBlockEntityRender(Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public int getViewDistance() {
        return VIEW_DISTANCE;
    }

    @Override
    public void render(PickleJarBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be.getLevel() != null) {
            BlockState state = be.getBlockState();
            if ((Boolean)state.getValue(PickleJarBlock.OPEN)) {
                long seed = be.getBlockPos().asLong();

                for (int slot = 0; slot < 4; slot++) {
                    ItemStack stack = be.inventory.getStackInSlot(slot);
                    if (!stack.isEmpty()) {
                        int copies = RENDER_COUNTS[Math.min(stack.getCount(), RENDER_COUNTS.length - 1)];
                        float baseY = LAYER_BASE_Y[slot];

                        for (int i = 0; i < copies; i++) {
                            poseStack.pushPose();
                            float x = 0.5F;
                            float z = 0.5F;
                            if (copies > 1) {
                                double angle = (Math.PI * 2) * i / copies;
                                x += (float)(Math.cos(angle) * LAYER_RADIUS);
                                z += (float)(Math.sin(angle) * LAYER_RADIUS);
                            }

                            float y = baseY + hashUnit(seed, slot * 64 + i) * HEIGHT_JITTER;
                            poseStack.translate(x, y, z);
                            poseStack.mulPose(Axis.YP.rotationDegrees(hashUnit(seed, slot * 64 + i + 16) * 360.0F));
                            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                            float scale = BASE_SCALE + (hashUnit(seed, slot * 64 + i + 32) - 0.5F) * 2.0F * SCALE_VARIATION;
                            poseStack.scale(scale, scale, scale);
                            this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, buffer, be.getLevel(), slot * 16 + i);
                            poseStack.popPose();
                        }
                    }
                }
            }
        }
    }

    private static float hashUnit(long seed, int salt) {
        long h = seed * -7046029254386353131L + salt * -4658895280553007687L;
        h ^= h >>> 30;
        h *= -4658895280553007687L;
        h ^= h >>> 27;
        h *= -7723592293110705685L;
        h ^= h >>> 31;
        return (float)(h & 16777215L) / 1.6777216E7F;
    }
}
