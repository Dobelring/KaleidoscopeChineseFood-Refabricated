package com.bmt.kaleidoscope_chinesefood.client.gui;

import com.bmt.kaleidoscope_chinesefood.inventory.FreezerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class FreezerScreen extends AbstractContainerScreen<FreezerMenu> {
   // 两层统一使用原版箱子贴图：上层裁剪拼贴出冷藏层布局（4 排 36 格），下层直接整图（6 排 54 格）
   private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
   private final boolean isTop;
   private int guiOffsetY;

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

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      super.render(guiGraphics, mouseX, mouseY, delta);
      this.renderProcessingProgress(guiGraphics);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   private void renderProcessingProgress(GuiGraphics guiGraphics) {
      int containerSize = ((FreezerMenu)this.menu).getContainer().getContainerSize();

      for (int i = 0; i < containerSize; i++) {
         Slot slot = ((FreezerMenu)this.menu).slots.get(i);
         if (slot.hasItem()) {
            int progress = ((FreezerMenu)this.menu).getProgress(i);
            int totalTime = ((FreezerMenu)this.menu).getTotalTime(i);
            if (totalTime > 0 && progress < totalTime) {
               int x = this.leftPos + slot.x + 2;
               int y = this.topPos + slot.y + 15;
               guiGraphics.fill(x, y, x + 12, y + 2, -11184811);
               float percent = (float)progress / totalTime;
               int progressWidth = (int)(percent * 12.0F);
               int color = this.isTop ? -11141121 : -16742145;
               guiGraphics.fill(x, y, x + progressWidth, y + 2, color);
            }
         }
      }
   }

   protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      // 使用不透明深灰 -12566464（0xFF404040），与原版 AbstractContainerScreen 一致
      guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
      guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
   }
}
