package com.bmt.kaleidoscope_chinesefood.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class ModConfigScreen extends Screen {
    private final Screen parent;
    private Checkbox enableCustomPacksCheckbox;

    public ModConfigScreen(Screen parent) {
        super(Component.translatable("config.kaleidoscope_chinesefood.title"));
        this.parent = parent;
    }

    protected void init() {
        int centerX = this.width / 2;
        int y = 70;
        this.enableCustomPacksCheckbox = new Checkbox(
            centerX - 150, y, 20, 20, Component.translatable("config.kaleidoscope_chinesefood.enableCustomPacks"), ModConfig.ENABLE_CUSTOM_PACKS.get()
        );
        this.addRenderableWidget(this.enableCustomPacksCheckbox);
        y = this.height - 30;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose()).pos(centerX - 100, y).size(200, 20).build());
    }

    public void onClose() {
        ModConfig.ENABLE_CUSTOM_PACKS.set(this.enableCustomPacksCheckbox.selected());
        ModConfig.SPEC.save();
        this.minecraft.setScreen(this.parent);
    }

    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
