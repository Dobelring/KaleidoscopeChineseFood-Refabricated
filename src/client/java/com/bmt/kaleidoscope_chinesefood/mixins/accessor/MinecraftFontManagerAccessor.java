package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 26.3 的 Font 不再持有字体集映射（1.20.1 的 Font#fonts 已移除），改为
 * Minecraft 持有 FontManager、FontManager 持有 fontSets。
 * 本 accessor 供 GlyphMetrics 拿到 FontManager。
 */
@Mixin(Minecraft.class)
public interface MinecraftFontManagerAccessor {
    @Accessor("fontManager")
    FontManager kaleidoscope_chinesefood$fontManager();
}
