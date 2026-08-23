package com.bmt.kaleidoscope_chinesefood.client.gui;

import com.bmt.kaleidoscope_chinesefood.inventory.FreezerMenu;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
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
      super(menu, playerInventory, title, 176, menu.isTop() ? 184 : 222);
      this.isTop = menu.isTop();
      this.titleLabelX = 8;
      if (this.isTop) {
         this.titleLabelY = 5;
         this.inventoryLabelY = this.imageHeight - 93;
         this.guiOffsetY = -1;
      } else {
         this.titleLabelY = 6;
         this.inventoryLabelY = this.imageHeight - 94;
         this.guiOffsetY = 0;
      }
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
      RenderPipeline pipeline = RenderPipelines.GUI_TEXTURED;
      Identifier currentTexture = this.isTop ? TEXTURE_TOP : TEXTURE_BOTTOM;
      int x = (this.width - this.imageWidth) / 2;
      int y = (this.height - this.imageHeight) / 2 + this.guiOffsetY;
      guiGraphics.blit(pipeline, currentTexture, x, y, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
   }

   @Override
   public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
      this.extractProcessingProgress(guiGraphics);
   }

   private void extractProcessingProgress(GuiGraphicsExtractor guiGraphics) {
      int containerSize = ((FreezerMenu)this.menu).getContainer().getContainerSize();

      for (int i = 0; i < containerSize; i++) {
         Slot slot = ((FreezerMenu)this.menu).slots.get(i);
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
}
