package com.bmt.kaleidoscope_chinesefood.config;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModConfigScreen extends Screen {
   private final Screen parent;
   private int currentTab = 0;
   private Button commonTabButton;
   private Button coupletTabButton;
   private Button bannerTabButton;
   private Checkbox enableCustomPacksCheckbox;
   private ModConfigScreen.ConfigSlider coupletTextScaleSlider;
   private Button resetCoupletTextScaleButton;
   private ModConfigScreen.ConfigSlider coupletHorizontalOffsetSlider;
   private Button resetCoupletHorizontalOffsetButton;
   private ModConfigScreen.ConfigSlider coupletDoubleBaseYSlider;
   private Button resetCoupletDoubleBaseYButton;
   private ModConfigScreen.ConfigSlider coupletTripleBaseYSlider;
   private Button resetCoupletTripleBaseYButton;
   private ModConfigScreen.ConfigSlider coupletVerticalSpacingSlider;
   private Button resetCoupletVerticalSpacingButton;
   private ModConfigScreen.ConfigSlider bannerTextScaleSlider;
   private Button resetBannerTextScaleButton;
   private ModConfigScreen.ConfigSlider bannerVerticalOffsetSlider;
   private Button resetBannerVerticalOffsetButton;
   private ModConfigScreen.ConfigSlider bannerSingleOffsetSlider;
   private Button resetBannerSingleOffsetButton;
   private ModConfigScreen.ConfigSlider bannerDoubleOffsetSlider;
   private Button resetBannerDoubleOffsetButton;
   private ModConfigScreen.ConfigSlider bannerTripleOffsetSlider;
   private Button resetBannerTripleOffsetButton;
   private ModConfigScreen.ConfigSlider bannerCharWidthSlider;
   private Button resetBannerCharWidthButton;
   private static final double DEFAULT_COUPLET_TEXT_SCALE = 0.025;
   private static final double DEFAULT_COUPLET_HORIZONTAL_OFFSET = 0.2;
   private static final double DEFAULT_COUPLET_DOUBLE_BASE_Y = -21.5;
   private static final double DEFAULT_COUPLET_TRIPLE_BASE_Y = -41.0;
   private static final double DEFAULT_COUPLET_VERTICAL_SPACING = 9.6;
   private static final double DEFAULT_BANNER_TEXT_SCALE = 0.022;
   private static final double DEFAULT_BANNER_VERTICAL_OFFSET = 0.99;
   private static final double DEFAULT_BANNER_SINGLE_OFFSET = -0.9;
   private static final double DEFAULT_BANNER_DOUBLE_OFFSET = 14.0;
   private static final double DEFAULT_BANNER_TRIPLE_OFFSET = 28.9;
   private static final double DEFAULT_BANNER_CHAR_WIDTH = 9.5;

   public ModConfigScreen(Screen parent) {
      super(Component.translatable("config.kaleidoscope_chinesefood.title"));
      this.parent = parent;
   }

   protected void init() {
      int centerX = this.width / 2;
      int y = 35;
      this.commonTabButton = Button.builder(Component.translatable("config.kaleidoscope_chinesefood.tab.common"), button -> this.switchTab(0))
         .pos(centerX - 150, y)
         .size(100, 20)
         .build();
      this.coupletTabButton = Button.builder(Component.translatable("config.kaleidoscope_chinesefood.tab.couplet"), button -> this.switchTab(1))
         .pos(centerX - 50, y)
         .size(100, 20)
         .build();
      this.bannerTabButton = Button.builder(Component.translatable("config.kaleidoscope_chinesefood.tab.banner"), button -> this.switchTab(2))
         .pos(centerX + 50, y)
         .size(100, 20)
         .build();
      this.addRenderableWidget(this.commonTabButton);
      this.addRenderableWidget(this.coupletTabButton);
      this.addRenderableWidget(this.bannerTabButton);
      this.initCommonComponents();
      this.initCoupletComponents();
      this.initBannerComponents();
      this.updateComponentVisibility();
      y = this.height - 30;
      this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> this.onClose()).pos(centerX - 100, y).size(200, 20).build());
   }

   private void switchTab(int tabIndex) {
      this.currentTab = tabIndex;
      this.updateComponentVisibility();
      this.updateTabButtonStyles();
   }

   private void updateTabButtonStyles() {
      this.commonTabButton.active = true;
      this.coupletTabButton.active = true;
      this.bannerTabButton.active = true;
      switch (this.currentTab) {
         case 0:
            this.commonTabButton.active = false;
            break;
         case 1:
            this.coupletTabButton.active = false;
            break;
         case 2:
            this.bannerTabButton.active = false;
      }
   }

   private void initCommonComponents() {
      int centerX = this.width / 2;
      int y = 70;
      this.enableCustomPacksCheckbox = Checkbox.builder(Component.translatable("config.kaleidoscope_chinesefood.enableCustomPacks"), this.font)
         .pos(centerX - 150, y)
         .selected((Boolean)ModConfig.ENABLE_CUSTOM_PACKS.get())
         .build();
      this.addRenderableWidget(this.enableCustomPacksCheckbox);
   }

   private void initCoupletComponents() {
      int centerX = this.width / 2;
      int y = 70;
      this.coupletTextScaleSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.coupletTextScale"),
         (Double)ClientConfig._COUPLET_TEXT_SCALE.get(),
         0.01,
         0.05,
         value -> String.format("%.3f", value),
         value -> ClientConfig._COUPLET_TEXT_SCALE.set(value)
      );
      this.resetCoupletTextScaleButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.coupletTextScaleSlider.setValue(0.025)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.coupletTextScaleSlider);
      this.addRenderableWidget(this.resetCoupletTextScaleButton);
      y += 22;
      this.coupletHorizontalOffsetSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.coupletHorizontalOffset"),
         (Double)ClientConfig._COUPLET_HORIZONTAL_OFFSET.get(),
         -1.0,
         1.0,
         value -> String.format("%.2f", value),
         value -> ClientConfig._COUPLET_HORIZONTAL_OFFSET.set(value)
      );
      this.resetCoupletHorizontalOffsetButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.coupletHorizontalOffsetSlider.setValue(0.2)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.coupletHorizontalOffsetSlider);
      this.addRenderableWidget(this.resetCoupletHorizontalOffsetButton);
      y += 22;
      this.coupletDoubleBaseYSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.coupletDoubleBaseY"),
         (Double)ClientConfig._COUPLET_DOUBLE_BASE_Y.get(),
         -100.0,
         100.0,
         value -> String.format("%.1f", value),
         value -> ClientConfig._COUPLET_DOUBLE_BASE_Y.set(value)
      );
      this.resetCoupletDoubleBaseYButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.coupletDoubleBaseYSlider.setValue(-21.5)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.coupletDoubleBaseYSlider);
      this.addRenderableWidget(this.resetCoupletDoubleBaseYButton);
      y += 22;
      this.coupletTripleBaseYSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.coupletTripleBaseY"),
         (Double)ClientConfig._COUPLET_TRIPLE_BASE_Y.get(),
         -100.0,
         100.0,
         value -> String.format("%.1f", value),
         value -> ClientConfig._COUPLET_TRIPLE_BASE_Y.set(value)
      );
      this.resetCoupletTripleBaseYButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.coupletTripleBaseYSlider.setValue(-41.0)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.coupletTripleBaseYSlider);
      this.addRenderableWidget(this.resetCoupletTripleBaseYButton);
      y += 22;
      this.coupletVerticalSpacingSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.coupletVerticalSpacing"),
         (Double)ClientConfig._COUPLET_VERTICAL_SPACING.get(),
         5.0,
         15.0,
         value -> String.format("%.1f", value),
         value -> ClientConfig._COUPLET_VERTICAL_SPACING.set(value)
      );
      this.resetCoupletVerticalSpacingButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.coupletVerticalSpacingSlider.setValue(9.6)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.coupletVerticalSpacingSlider);
      this.addRenderableWidget(this.resetCoupletVerticalSpacingButton);
   }

   private void initBannerComponents() {
      int centerX = this.width / 2;
      int y = 70;
      this.bannerTextScaleSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.bannerTextScale"),
         (Double)ClientConfig._BANNER_TEXT_SCALE.get(),
         0.01,
         0.05,
         value -> String.format("%.3f", value),
         value -> ClientConfig._BANNER_TEXT_SCALE.set(value)
      );
      this.resetBannerTextScaleButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.bannerTextScaleSlider.setValue(0.022)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.bannerTextScaleSlider);
      this.addRenderableWidget(this.resetBannerTextScaleButton);
      y += 22;
      this.bannerVerticalOffsetSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.bannerVerticalOffset"),
         (Double)ClientConfig._BANNER_VERTICAL_OFFSET.get(),
         -5.0,
         5.0,
         value -> String.format("%.2f", value),
         value -> ClientConfig._BANNER_VERTICAL_OFFSET.set(value)
      );
      this.resetBannerVerticalOffsetButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.bannerVerticalOffsetSlider.setValue(0.99)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.bannerVerticalOffsetSlider);
      this.addRenderableWidget(this.resetBannerVerticalOffsetButton);
      y += 22;
      this.bannerSingleOffsetSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.bannerSingleOffset"),
         (Double)ClientConfig._BANNER_SINGLE_OFFSET.get(),
         -50.0,
         50.0,
         value -> String.format("%.1f", value),
         value -> ClientConfig._BANNER_SINGLE_OFFSET.set(value)
      );
      this.resetBannerSingleOffsetButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.bannerSingleOffsetSlider.setValue(-0.9)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.bannerSingleOffsetSlider);
      this.addRenderableWidget(this.resetBannerSingleOffsetButton);
      y += 22;
      this.bannerDoubleOffsetSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.bannerDoubleOffset"),
         (Double)ClientConfig._BANNER_DOUBLE_OFFSET.get(),
         -50.0,
         50.0,
         value -> String.format("%.2f", value),
         value -> ClientConfig._BANNER_DOUBLE_OFFSET.set(value)
      );
      this.resetBannerDoubleOffsetButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.bannerDoubleOffsetSlider.setValue(14.0)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.bannerDoubleOffsetSlider);
      this.addRenderableWidget(this.resetBannerDoubleOffsetButton);
      y += 22;
      this.bannerTripleOffsetSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.bannerTripleOffset"),
         (Double)ClientConfig._BANNER_TRIPLE_OFFSET.get(),
         -50.0,
         50.0,
         value -> String.format("%.1f", value),
         value -> ClientConfig._BANNER_TRIPLE_OFFSET.set(value)
      );
      this.resetBannerTripleOffsetButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.bannerTripleOffsetSlider.setValue(28.9)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.bannerTripleOffsetSlider);
      this.addRenderableWidget(this.resetBannerTripleOffsetButton);
      y += 22;
      this.bannerCharWidthSlider = new ModConfigScreen.ConfigSlider(
         centerX - 150,
         y,
         245,
         20,
         Component.translatable("config.kaleidoscope_chinesefood.bannerCharWidth"),
         (Double)ClientConfig._BANNER_CHAR_WIDTH.get(),
         5.0,
         15.0,
         value -> String.format("%.1f", value),
         value -> ClientConfig._BANNER_CHAR_WIDTH.set(value)
      );
      this.resetBannerCharWidthButton = Button.builder(
            Component.translatable("config.kaleidoscope_chinesefood.reset"), button -> this.bannerCharWidthSlider.setValue(9.5)
         )
         .pos(centerX + 100, y)
         .size(50, 20)
         .build();
      this.addRenderableWidget(this.bannerCharWidthSlider);
      this.addRenderableWidget(this.resetBannerCharWidthButton);
   }

   private void updateComponentVisibility() {
      this.enableCustomPacksCheckbox.visible = false;
      this.coupletTextScaleSlider.visible = false;
      this.resetCoupletTextScaleButton.visible = false;
      this.coupletHorizontalOffsetSlider.visible = false;
      this.resetCoupletHorizontalOffsetButton.visible = false;
      this.coupletDoubleBaseYSlider.visible = false;
      this.resetCoupletDoubleBaseYButton.visible = false;
      this.coupletTripleBaseYSlider.visible = false;
      this.resetCoupletTripleBaseYButton.visible = false;
      this.coupletVerticalSpacingSlider.visible = false;
      this.resetCoupletVerticalSpacingButton.visible = false;
      this.bannerTextScaleSlider.visible = false;
      this.resetBannerTextScaleButton.visible = false;
      this.bannerVerticalOffsetSlider.visible = false;
      this.resetBannerVerticalOffsetButton.visible = false;
      this.bannerSingleOffsetSlider.visible = false;
      this.resetBannerSingleOffsetButton.visible = false;
      this.bannerDoubleOffsetSlider.visible = false;
      this.resetBannerDoubleOffsetButton.visible = false;
      this.bannerTripleOffsetSlider.visible = false;
      this.resetBannerTripleOffsetButton.visible = false;
      this.bannerCharWidthSlider.visible = false;
      this.resetBannerCharWidthButton.visible = false;
      switch (this.currentTab) {
         case 0:
            this.enableCustomPacksCheckbox.visible = true;
            break;
         case 1:
            this.coupletTextScaleSlider.visible = true;
            this.resetCoupletTextScaleButton.visible = true;
            this.coupletHorizontalOffsetSlider.visible = true;
            this.resetCoupletHorizontalOffsetButton.visible = true;
            this.coupletDoubleBaseYSlider.visible = true;
            this.resetCoupletDoubleBaseYButton.visible = true;
            this.coupletTripleBaseYSlider.visible = true;
            this.resetCoupletTripleBaseYButton.visible = true;
            this.coupletVerticalSpacingSlider.visible = true;
            this.resetCoupletVerticalSpacingButton.visible = true;
            break;
         case 2:
            this.bannerTextScaleSlider.visible = true;
            this.resetBannerTextScaleButton.visible = true;
            this.bannerVerticalOffsetSlider.visible = true;
            this.resetBannerVerticalOffsetButton.visible = true;
            this.bannerSingleOffsetSlider.visible = true;
            this.resetBannerSingleOffsetButton.visible = true;
            this.bannerDoubleOffsetSlider.visible = true;
            this.resetBannerDoubleOffsetButton.visible = true;
            this.bannerTripleOffsetSlider.visible = true;
            this.resetBannerTripleOffsetButton.visible = true;
            this.bannerCharWidthSlider.visible = true;
            this.resetBannerCharWidthButton.visible = true;
      }

      this.updateTabButtonStyles();
   }

   public void onClose() {
      ModConfig.ENABLE_CUSTOM_PACKS.set(this.enableCustomPacksCheckbox.selected());
      ModConfig.SPEC.save();
      this.coupletTextScaleSlider.applyValue();
      this.coupletHorizontalOffsetSlider.applyValue();
      this.coupletDoubleBaseYSlider.applyValue();
      this.coupletTripleBaseYSlider.applyValue();
      this.coupletVerticalSpacingSlider.applyValue();
      this.bannerTextScaleSlider.applyValue();
      this.bannerVerticalOffsetSlider.applyValue();
      this.bannerSingleOffsetSlider.applyValue();
      this.bannerDoubleOffsetSlider.applyValue();
      this.bannerTripleOffsetSlider.applyValue();
      this.bannerCharWidthSlider.applyValue();
      ClientConfig.SPEC.save();
      ClientConfig.validateAndCacheConfigValues();
      this.minecraft.setScreen(this.parent);
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
      this.extractBackground(graphics, mouseX, mouseY, partialTick);
      graphics.centeredText(this.font, this.title, this.width / 2, 15, 16777215);
      super.extractRenderState(graphics, mouseX, mouseY, partialTick);
   }

   private static class ConfigSlider extends AbstractSliderButton {
      private final Component prefix;
      private final double minValue;
      private final double maxValue;
      private final Function<Double, String> valueFormatter;
      private final Consumer<Double> valueSetter;
      private double currentValue;

      public ConfigSlider(
         int x,
         int y,
         int width,
         int height,
         Component prefix,
         double currentValue,
         double minValue,
         double maxValue,
         Function<Double, String> valueFormatter,
         Consumer<Double> valueSetter
      ) {
         super(x, y, width, height, Component.empty(), 0.0);
         this.prefix = prefix;
         this.minValue = minValue;
         this.maxValue = maxValue;
         this.valueFormatter = valueFormatter;
         this.valueSetter = valueSetter;
         this.currentValue = currentValue;
         this.value = (currentValue - minValue) / (maxValue - minValue);
         this.updateMessage();
      }

      protected void updateMessage() {
         this.setMessage(Component.literal("").append(this.prefix).append(": ").append(this.valueFormatter.apply(this.currentValue)));
      }

      protected void applyValue() {
         this.currentValue = this.minValue + this.value * (this.maxValue - this.minValue);
         this.valueSetter.accept(this.currentValue);
      }

      public void setValue(double value) {
         this.currentValue = value;
         this.value = (value - this.minValue) / (this.maxValue - this.minValue);
         this.updateMessage();
      }
   }
}
