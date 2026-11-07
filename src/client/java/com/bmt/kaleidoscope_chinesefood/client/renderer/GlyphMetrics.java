package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.mixins.accessor.FontAccessor;
import com.mojang.blaze3d.font.GlyphInfo;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;

/**
 * 1.2.0:自绘文字(对联/横批/编辑器)的字形墨迹包围盒缓存(对齐官方 GlyphMetrics)。
 * 通过 FontAccessor 拿 FontSet,取 GlyphInfo 的实际渲染边界,按码点缓存。
 */
public final class GlyphMetrics {
    private static final Map<Integer, InkBox> CACHE = new ConcurrentHashMap<>();

    private GlyphMetrics() {
    }

    public static InkBox measure(Font font, ResourceLocation fontId, char c) {
        return CACHE.computeIfAbsent((int)c, codepoint -> measure(font, fontId, codepoint));
    }

    private static InkBox measure(Font font, ResourceLocation fontId, int codepoint) {
        FontSet fontSet = ((FontAccessor)font).kaleidoscope_chinesefood$fonts().apply(fontId);
        GlyphInfo glyphInfo = fontSet.getGlyphInfo(codepoint, false);
        InkBox[] captured = new InkBox[1];
        glyphInfo.bake(sheet -> {
            captured[0] = new InkBox(sheet.getLeft(), sheet.getRight(), sheet.getTop(), sheet.getBottom());
            return null;
        });
        if (captured[0] == null) {
            float advance = glyphInfo.getAdvance();
            return new InkBox(advance / 2.0F, advance / 2.0F, 0.0F, 0.0F);
        }
        return captured[0];
    }

    public record InkBox(float left, float right, float up, float down) {
        public float width() {
            return this.right - this.left;
        }

        public float height() {
            return this.down - this.up;
        }
    }
}
