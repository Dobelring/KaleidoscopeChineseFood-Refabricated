package com.bmt.kaleidoscope_chinesefood.client.gui;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.inventory.FreezerMenu;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * 官方 1.1.16 重构：冷冻进度渲染从「12×2 进度条」改为
 * 槽位四周生长的霜冻描边（进度渐变色+尖端淡出）+ 雪花粒子动画
 * （8 帧竖排贴图、重力/旋转/淡入淡出，每槽最多 8 片、全局最多 72 片）。
 * 26.1.2 GUI 为提取（extract）管线：无即时 Tesselator，霜边用 fill、
 * 雪花用带 pose 旋转的 blit（RenderPipelines.GUI_TEXTURED 自带混合），颜色/透明度走 blit 的 color 参数。
 */
public class FreezerScreen extends AbstractContainerScreen<FreezerMenu> {
   // 两层统一使用原版箱子贴图：上层裁剪拼贴出冷藏层布局（4 排 36 格），下层直接整图（6 排 54 格）
   private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
   private static final Identifier SNOWFLAKES_TEXTURE = KaleidoscopeChineseFood.id("textures/gui/snowflakes.png");
   private static final int CHILLED_PALE = 14086911;
   private static final int CHILLED_DEEP = 5884144;
   private static final int FROZEN_PALE = 12767231;
   private static final int FROZEN_DEEP = 2839264;
   private static final int SNOWFLAKE_PALE = 13490906;
   private static final int SNOWFLAKE_WHITE = 15596282;
   private static final int SNOWFLAKE_FRAMES = 8;
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

   public FreezerScreen(FreezerMenu menu, Inventory playerInventory, Component title) {
      // 26.1.2 imageWidth/imageHeight 为 final，经 5 参构造传入（176 宽 + 按层数的高）
      super(menu, playerInventory, title, 176, menu.isTop() ? 184 : 222);
      this.isTop = menu.isTop();
      if (this.isTop) {
         this.titleLabelX = 8;
         this.titleLabelY = 5;
         this.inventoryLabelY = this.imageHeight - 93;
         this.guiOffsetY = -1;
      } else {
         this.titleLabelX = 8;
         this.titleLabelY = 6;
         this.inventoryLabelY = this.imageHeight - 94;
         this.guiOffsetY = 0;
      }
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2 + this.guiOffsetY;
      if (this.isTop) {
         // 冷藏层：用原版箱子贴图裁剪拼出 184 高布局
         guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + 1, 0.0F, 0.0F, this.imageWidth, 18, 256, 256);
         guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + 19, 0.0F, 18.0F, this.imageWidth, 72, 256, 256);
         guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + 90, 0.0F, 126.0F, this.imageWidth, 14, 256, 256);
         guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y + 104, 0.0F, 140.0F, this.imageWidth, 81, 256, 256);
      } else {
         // 冷冻层：原版箱子贴图整图
         guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      }
   }

   // 26.1 GUI 提取模型：霜边+雪花插入在槽位物品之后、tooltip 之前（与 1.21.11 render 顺序一致）
   @Override
   protected void extractTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
      this.updateSnowflakes();
      this.renderFreezeBorders(guiGraphics);
      this.renderSnowflakes(guiGraphics);
      super.extractTooltip(guiGraphics, mouseX, mouseY);
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

   private void renderFreezeBorders(GuiGraphicsExtractor guiGraphics) {
      int containerSize = ((FreezerMenu)this.menu).getContainer().getContainerSize();
      int slotCount = Math.min(containerSize, ((FreezerMenu)this.menu).slots.size());

      for (int i = 0; i < slotCount; i++) {
         Slot slot = ((FreezerMenu)this.menu).slots.get(i);
         if (slot.hasItem()) {
            int totalTime = ((FreezerMenu)this.menu).getTotalTime(i);
            if (totalTime > 0) {
               float progress = Mth.clamp((float)((FreezerMenu)this.menu).getProgress(i) / totalTime, 0.0F, 1.0F);
               this.renderSlotBorder(guiGraphics, this.leftPos + slot.x, this.topPos + slot.y, progress);
            }
         }
      }
   }

   /** 26.1 无逐顶点颜色，每条边拆「主体实色 + 尖端淡出」两段 fill（视觉与官方 tipFade 等价）。 */
   private void renderSlotBorder(GuiGraphicsExtractor guiGraphics, int x, int y, float progress) {
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
      // 下边
      guiGraphics.fill(x, y + 15, (int)Math.floor(x + 1.0F + extH), y + 16, base);
      guiGraphics.fill((int)Math.floor(x + 1.0F + extH), y + 15, x + 16, y + 16, tipH);
      // 左边（自下而上生长，fillGradient 自上而下 → 主体下段实色+上段尖端淡出）
      guiGraphics.fillGradient(x, y + 15 - (int)Math.ceil(extV), x + 1, y + 15, tipV, base);
      // 右边
      guiGraphics.fill(x + 15, y + 1, x + 16, (int)Math.floor(y + 1.0F + extV), base);
      guiGraphics.fill(x + 15, (int)Math.floor(y + 1.0F + extV), x + 16, y + 16, tipV);
      // 上边
      guiGraphics.fill((int)Math.floor(x + 15.0F - extH), y, x + 15, y + 1, tipH);
      guiGraphics.fill(x + 15, y, x + 16, y + 1, base);
   }

   private static float tipFade(float remaining) {
      return 1.0F - Mth.clamp(remaining / 4.0F, 0.0F, 1.0F);
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

   private void renderSnowflakes(GuiGraphicsExtractor guiGraphics) {
      if (!this.snowflakes.isEmpty()) {
         for (FreezerScreen.Snowflake snowflake : this.snowflakes) {
            float fade = 1.0F;
            if (snowflake.age < 5) {
               fade = snowflake.age / 5.0F;
            } else if (snowflake.age > SNOWFLAKE_LIFETIME - 10) {
               fade = (SNOWFLAKE_LIFETIME - snowflake.age) / 10.0F;
            }

            int alpha = Mth.clamp((int)(fade * 255.0F), 0, 255);
            if (alpha >= 4) {
               int color = alpha << 24 | snowflake.rgb;
               int frame = Math.min(snowflake.age / 10, SNOWFLAKE_FRAMES - 1);
               guiGraphics.pose().pushMatrix();
               guiGraphics.pose().translate(snowflake.x, snowflake.y);
               guiGraphics.pose().rotate(snowflake.angle * (float) (Math.PI / 180.0));
               guiGraphics.blit(
                  RenderPipelines.GUI_TEXTURED,
                  SNOWFLAKES_TEXTURE,
                  -4,
                  -4,
                  0.0F,
                  frame * 8.0F,
                  8,
                  8,
                  8,
                  64,
                  color
               );
               guiGraphics.pose().popMatrix();
            }
         }
      }
   }

   @Override
   protected void extractLabels(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
      // 使用不透明深灰 -12566464（0xFF404040），与原版 AbstractContainerScreen 一致
      guiGraphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
      guiGraphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
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
