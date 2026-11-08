package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import net.minecraft.client.gui.font.glyphs.BakedSheetGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 暴露 BakedSheetGlyph 的字形墨迹包围盒（26.3 等价物：1.20.1 是 GlyphInfo#bake
 * 回调里的 SheetGlyphInfo.getLeft/getRight/getUp/getDown）。
 * 四个字段与渲染时的 GlyphInstance.left/top/right/bottom 同一坐标系。
 */
@Mixin(BakedSheetGlyph.class)
public interface BakedSheetGlyphAccessor {
    @Accessor("left")
    float kaleidoscope_chinesefood$left();

    @Accessor("right")
    float kaleidoscope_chinesefood$right();

    @Accessor("up")
    float kaleidoscope_chinesefood$up();

    @Accessor("down")
    float kaleidoscope_chinesefood$down();
}
