package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import java.util.Map;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 暴露 FontManager 的字体集映射（26.3 等价物：1.20.1 是 Font#fonts），
 * 供 GlyphMetrics 测量自定义字体（couplet_font）字形墨迹框。
 */
@Mixin(FontManager.class)
public interface FontManagerAccessor {
    @Accessor("fontSets")
    Map<Identifier, FontSet> kaleidoscope_chinesefood$fontSets();
}
