package com.bmt.kaleidoscope_chinesefood.client.gui;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.client.menu.FreezerMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.joml.Matrix4f;

/**
 * 官方 1.1.16 重构：冷冻进度渲染从「12×2 进度条」改为
 * 槽位四周生长的霜冻描边（进度渐变色+尖端淡出）+ 雪花粒子动画
 * （8 帧竖排贴图、重力/旋转/淡入淡出，每槽最多 8 片、全局最多 72 片）。
 */
public class FreezerScreen extends AbstractContainerScreen<FreezerMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    /** 1.2.0：雪花贴图改用原版粒子 generic_7/6/5 三帧（原 8 帧自制贴图已删）。 */
    private static final ResourceLocation[] SNOWFLAKE_TEXTURES = new ResourceLocation[3];
    private static final int CHILLED_PALE = 14086911;
    private static final int CHILLED_DEEP = 5884144;
    private static final int FROZEN_PALE = 12767231;
    private static final int FROZEN_DEEP = 2839264;
    private static final int SNOWFLAKE_PALE = 13490906;
    private static final int SNOWFLAKE_WHITE = 15596282;
    private static final int SNOWFLAKE_FRAMES = 3;
    private static final int SNOWFLAKE_LIFETIME = 80;
    private static final float SNOWFLAKE_GRAVITY = 0.1F;
    private static final float SNOWFLAKE_MAX_FALL = 3.0F;
    private static final float SNOWFLAKE_HALF_SIZE = 4.0F;
    private static final int MAX_SNOWFLAKES = 72;
    private static final int MAX_SNOWFLAKES_PER_SLOT = 8;
    private static final int SPAWN_CHANCE = 6;
    private final List<FreezerScreen.Snowflake> snowflakes = new ArrayList<>();
    private final RandomSource random = RandomSource.create();
    private long lastSnowflakeTick;
    private final boolean isTop;
    private int guiOffsetY;

    static {
        for (int i = 0; i < SNOWFLAKE_TEXTURES.length; i++) {
            SNOWFLAKE_TEXTURES[i] = new ResourceLocation("textures/particle/generic_" + (7 - i) + ".png");
        }
    }

    public FreezerScreen(FreezerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.isTop = menu.isTop();
        this.imageWidth = 176;
        if (this.isTop) {
            this.imageHeight = 184;
            this.titleLabelX = 8;
            this.titleLabelY = 5;
            this.inventoryLabelY = this.imageHeight - 93;
            this.guiOffsetY = -1;
        } else {
            this.imageHeight = 222;
            this.titleLabelX = 8;
            this.titleLabelY = 6;
            this.inventoryLabelY = this.imageHeight - 94;
            this.guiOffsetY = 0;
        }
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2 + this.guiOffsetY;
        if (this.isTop) {
            guiGraphics.blit(TEXTURE, x, y + 1, 0, 0, this.imageWidth, 18);
            guiGraphics.blit(TEXTURE, x, y + 19, 0, 18, this.imageWidth, 72);
            guiGraphics.blit(TEXTURE, x, y + 90, 0, 126, this.imageWidth, 14);
            guiGraphics.blit(TEXTURE, x, y + 104, 0, 140, this.imageWidth, 81);
        } else {
            guiGraphics.blit(TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.updateSnowflakes();
        this.renderFreezeBorders(guiGraphics);
        this.renderSnowflakes(guiGraphics);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void updateSnowflakes() {
        long now = Util.getMillis();
        if (this.lastSnowflakeTick == 0L) {
            this.lastSnowflakeTick = now;
        }

        int steps = (int)((now - this.lastSnowflakeTick) / 50L);
        if (steps > 0) {
            this.lastSnowflakeTick = now;

            for (int i = 0; i < Math.min(steps, 3); i++) {
                this.spawnSnowflakes();
                this.tickSnowflakes();
            }
        }
    }

    private void renderFreezeBorders(GuiGraphics guiGraphics) {
        int containerSize = ((FreezerMenu)this.menu).getContainer().getContainerSize();
        int slotCount = Math.min(containerSize, ((FreezerMenu)this.menu).slots.size());
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0.0F, 0.0F, 250.0F);
        Matrix4f matrix = pose.last().pose();
        BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        VertexConsumer consumer = buffer.getBuffer(RenderType.gui());
        RenderSystem.disableDepthTest();

        for (int i = 0; i < slotCount; i++) {
            Slot slot = ((FreezerMenu)this.menu).slots.get(i);
            if (slot.hasItem()) {
                int totalTime = ((FreezerMenu)this.menu).getTotalTime(i);
                if (totalTime > 0) {
                    float progress = Mth.clamp((float)((FreezerMenu)this.menu).getProgress(i) / totalTime, 0.0F, 1.0F);
                    this.renderSlotBorder(consumer, matrix, this.leftPos + slot.x, this.topPos + slot.y, progress);
                }
            }
        }

        buffer.endBatch();
        RenderSystem.enableDepthTest();
        pose.popPose();
    }

    private void renderSlotBorder(VertexConsumer consumer, Matrix4f matrix, int x, int y, float progress) {
        if (progress >= 0.98F) {
            progress = 1.0F;
        }

        int rgb = lerpRgb(this.isTop ? CHILLED_PALE : FROZEN_PALE, this.isTop ? CHILLED_DEEP : FROZEN_DEEP, progress);
        float alpha = 0.5F + 0.5F * progress;
        float extH = Math.max(2.0F, progress * 15.0F);
        float extV = Math.max(2.0F, progress * 14.0F);
        int base = withAlpha(rgb, alpha);
        int tipH = withAlpha(rgb, alpha * tipFade(15.0F - extH));
        int tipV = withAlpha(rgb, alpha * tipFade(14.0F - extV));
        quad(consumer, matrix, x, y + 15.0F, x + 1.0F + extH, y + 16.0F, base, base, tipH, tipH);
        quad(consumer, matrix, x, y + 15.0F - extV, x + 1.0F, y + 15.0F, tipV, base, base, tipV);
        quad(consumer, matrix, x + 15.0F - extH, y, x + 16.0F, y + 1.0F, tipH, tipH, base, base);
        quad(consumer, matrix, x + 15.0F, y + 1.0F, x + 16.0F, y + 1.0F + extV, base, tipV, tipV, base);
    }

    private static float tipFade(float remaining) {
        return 1.0F - Mth.clamp(remaining / 4.0F, 0.0F, 1.0F);
    }

    private static void quad(
        VertexConsumer consumer, Matrix4f matrix, float x1, float y1, float x2, float y2, int colorTL, int colorBL, int colorBR, int colorTR
    ) {
        consumer.vertex(matrix, x1, y1, 0.0F).color(colorTL).endVertex();
        consumer.vertex(matrix, x1, y2, 0.0F).color(colorBL).endVertex();
        consumer.vertex(matrix, x2, y2, 0.0F).color(colorBR).endVertex();
        consumer.vertex(matrix, x2, y1, 0.0F).color(colorTR).endVertex();
    }

    private static int lerpRgb(int from, int to, float delta) {
        int red = (int)Mth.lerp(delta, from >> 16 & 0xFF, to >> 16 & 0xFF);
        int green = (int)Mth.lerp(delta, from >> 8 & 0xFF, to >> 8 & 0xFF);
        int blue = (int)Mth.lerp(delta, from & 0xFF, to & 0xFF);
        return red << 16 | green << 8 | blue;
    }

    private static int withAlpha(int rgb, float alpha) {
        int a = Mth.clamp((int)(alpha * 255.0F), 0, 255);
        return a << 24 | rgb & 16777215;
    }

    private void spawnSnowflakes() {
        int containerSize = ((FreezerMenu)this.menu).getContainer().getContainerSize();
        int slotCount = Math.min(containerSize, ((FreezerMenu)this.menu).slots.size());

        for (int i = 0; i < slotCount; i++) {
            if (this.snowflakes.size() >= MAX_SNOWFLAKES) {
                return;
            }

            Slot slot = ((FreezerMenu)this.menu).slots.get(i);
            if (slot.hasItem() && ((FreezerMenu)this.menu).getTotalTime(i) > 0 && this.random.nextInt(100) < SPAWN_CHANCE) {
                float minCx = this.leftPos + slot.x + 2.0F;
                float maxCx = this.leftPos + slot.x + 14.0F;
                float minCy = this.topPos + slot.y + 2.0F;
                float maxCy = this.topPos + slot.y + 14.0F;
                int count = 0;

                for (FreezerScreen.Snowflake snowflake : this.snowflakes) {
                    if (snowflake.minCx == minCx && snowflake.minCy == minCy) {
                        if (++count >= MAX_SNOWFLAKES_PER_SLOT) {
                            break;
                        }
                    }
                }

                if (count < MAX_SNOWFLAKES_PER_SLOT) {
                    FreezerScreen.Snowflake snowflake = new FreezerScreen.Snowflake();
                    switch (this.random.nextInt(3)) {
                        case 0:
                            snowflake.x = Mth.lerp(this.random.nextFloat(), minCx, maxCx);
                            snowflake.y = minCy;
                            break;
                        case 1:
                            snowflake.x = minCx;
                            snowflake.y = Mth.lerp(this.random.nextFloat(), minCy, maxCy - 2.0F);
                            break;
                        default:
                            snowflake.x = maxCx;
                            snowflake.y = Mth.lerp(this.random.nextFloat(), minCy, maxCy - 2.0F);
                    }

                    snowflake.minCx = minCx;
                    snowflake.maxCx = maxCx;
                    snowflake.minCy = minCy;
                    snowflake.maxCy = maxCy;
                    snowflake.angle = this.random.nextFloat() * 360.0F;
                    snowflake.rotSpeed = (1.0F + this.random.nextFloat() * 2.0F) * (this.random.nextBoolean() ? 1.0F : -1.0F);
                    snowflake.rgb = lerpRgb(SNOWFLAKE_PALE, SNOWFLAKE_WHITE, this.random.nextFloat());
                    this.snowflakes.add(snowflake);
                }
            }
        }
    }

    private void tickSnowflakes() {
        Iterator<FreezerScreen.Snowflake> iterator = this.snowflakes.iterator();

        while (iterator.hasNext()) {
            FreezerScreen.Snowflake snowflake = iterator.next();
            if (++snowflake.age >= SNOWFLAKE_LIFETIME) {
                iterator.remove();
            } else {
                snowflake.vy = Math.min(snowflake.vy + SNOWFLAKE_GRAVITY, SNOWFLAKE_MAX_FALL);
                snowflake.vx *= 0.9F;
                float newY = snowflake.y + snowflake.vy;
                if (newY >= snowflake.maxCy) {
                    iterator.remove();
                } else {
                    snowflake.x = Mth.clamp(snowflake.x + snowflake.vx, snowflake.minCx, snowflake.maxCx);
                    snowflake.y = newY;
                    snowflake.angle = snowflake.angle + snowflake.rotSpeed;
                }
            }
        }
    }

    private void renderSnowflakes(GuiGraphics guiGraphics) {
        if (!this.snowflakes.isEmpty()) {
            // 1.2.0：按帧分 3 组、每组一张独立原版粒子贴图分别绘制
            Map<Integer, List<FreezerScreen.Snowflake>> byFrame = new HashMap<>();
            for (int frame = 0; frame < SNOWFLAKE_FRAMES; frame++) {
                byFrame.put(frame, new ArrayList<>());
            }
            for (FreezerScreen.Snowflake snowflake : this.snowflakes) {
                byFrame.get(Math.min(snowflake.age / 10, 2)).add(snowflake);
            }
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0.0F, 0.0F, 300.0F);
            Matrix4f matrix = guiGraphics.pose().last().pose();
            Tesselator tesselator = Tesselator.getInstance();
            for (int frame = 0; frame < SNOWFLAKE_FRAMES; frame++) {
                List<FreezerScreen.Snowflake> group = byFrame.get(frame);
                if (group.isEmpty()) {
                    continue;
                }
                RenderSystem.setShaderTexture(0, SNOWFLAKE_TEXTURES[frame]);
                BufferBuilder builder = tesselator.getBuilder();
                builder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                for (FreezerScreen.Snowflake snowflake : group) {
                    float fade = 1.0F;
                    if (snowflake.age < 5) {
                        fade = snowflake.age / 5.0F;
                    } else if (snowflake.age > SNOWFLAKE_LIFETIME - 10) {
                        fade = (SNOWFLAKE_LIFETIME - snowflake.age) / 10.0F;
                    }
                    int alpha = Mth.clamp((int)(fade * 255.0F), 0, 255);
                    if (alpha < 4) {
                        continue;
                    }
                    int color = alpha << 24 | snowflake.rgb;
                    float radians = snowflake.angle * (float) (Math.PI / 180.0);
                    float cos = Mth.cos(radians);
                    float sin = Mth.sin(radians);
                    texturedVertex(builder, matrix, snowflake.x, snowflake.y, -SNOWFLAKE_HALF_SIZE, -SNOWFLAKE_HALF_SIZE, cos, sin, 0.0F, 0.0F, color);
                    texturedVertex(builder, matrix, snowflake.x, snowflake.y, -SNOWFLAKE_HALF_SIZE, SNOWFLAKE_HALF_SIZE, cos, sin, 0.0F, 1.0F, color);
                    texturedVertex(builder, matrix, snowflake.x, snowflake.y, SNOWFLAKE_HALF_SIZE, SNOWFLAKE_HALF_SIZE, cos, sin, 1.0F, 1.0F, color);
                    texturedVertex(builder, matrix, snowflake.x, snowflake.y, SNOWFLAKE_HALF_SIZE, -SNOWFLAKE_HALF_SIZE, cos, sin, 1.0F, 0.0F, color);
                }
                // 1.20.1 官方 mappings 里结束绘制叫 end()（1.20.2+ 改名 buildOrThrow()）
                BufferUploader.drawWithShader(builder.end());
            }
            guiGraphics.pose().popPose();
            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
        }
    }

    private static void texturedVertex(
        BufferBuilder builder, Matrix4f matrix, float cx, float cy, float dx, float dy, float cos, float sin, float u, float v, int color
    ) {
        float x = cx + dx * cos - dy * sin;
        float y = cy + dx * sin + dy * cos;
        builder.vertex(matrix, x, y, 0.0F).uv(u, v).color(color).endVertex();
    }

    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    private static final class Snowflake {
        private float x;
        private float y;
        private float vx;
        private float vy;
        private float angle;
        private float rotSpeed;
        private float minCx;
        private float maxCx;
        private float minCy;
        private float maxCy;
        private int rgb;
        private int age;
    }
}
