package com.bmt.kaleidoscope_chinesefood.client.gui;

import com.bmt.kaleidoscope_chinesefood.inventory.FreezerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class FreezerScreen extends AbstractContainerScreen<FreezerMenu> {
   private static final Identifier TEXTURE_TOP = Identifier.fromNamespaceAndPath("kaleidoscope_chinesefood", "textures/gui/freezer_top.png");
   private static final Identifier TEXTURE_BOTTOM = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
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
      // 1.21.11 移除了 RenderSystem 手动 shader 设置，GuiGraphics.blit 自带渲染管线
      Identifier currentTexture = this.isTop ? TEXTURE_TOP : TEXTURE_BOTTOM;
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2 + this.guiOffsetY;
      guiGraphics.blit(currentTexture, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
   }

   public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
      super.render(guiGraphics, mouseX, mouseY, delta);
      this.renderProcessingProgress(guiGraphics);
      this.renderTooltip(guiGraphics, mouseX, mouseY);
   }

   private void renderProcessingProgress(GuiGraphics guiGraphics) {
      int containerSize = ((FreezerMenu)this.menu).getContainer().getContainerSize();

      for (int i = 0; i < containerSize; i++) {
         Slot slot = (Slot)((FreezerMenu)this.menu).slots.get(i);
         if (slot.hasItem()) {
            int progress = ((FreezerMenu)this.menu).getProgress(i);
            int totalTime = ((FreezerMenu)this.menu).getTotalTime(i);
            if (totalTime > 0 && progress < totalTime) {
               int x = this.leftPos + slot.x + 2;
               int y = this.topPos + slot.y + 15;
               guiGraphics.fill(x, y, x + 13, y + 2, -11184811);
               float percent = (float)progress / totalTime;
               int progressWidth = (int)(percent * 13.0F);
               int color = this.isTop ? -11141121 : -16742145;
               guiGraphics.fill(x, y, x + progressWidth, y + 2, color);
            }
         }
      }
   }

   protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
      guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
      guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
   }
}
