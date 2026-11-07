package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.bmt.kaleidoscope_chinesefood.mixins.accessor.BakedSheetGlyphAccessor;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.FontManagerAccessor;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.MinecraftFontManagerAccessor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedSheetGlyph;
import net.minecraft.resources.Identifier;

/**
 * 1.2.0：自绘文字（对联/横批）的字形墨迹包围盒缓存（对齐官方 GlyphMetrics），按码点缓存。
 * <p>
 * 26.3 适配：1.20.1 的 {@code Font#fonts + GlyphInfo#bake(SheetGlyphInfo)} 钩子已不存在，
 * 等价路径为 Minecraft→FontManager→fontSets→FontSet.source(false).getGlyph(codepoint)，
 * 烘焙出的 {@link BakedSheetGlyph} 的 left/right/up/down 即字形墨迹框（与渲染同一坐标系）。
 */
public final class GlyphMetrics {
    private static final Map<Integer, InkBox> CACHE = new ConcurrentHashMap<>();

    private GlyphMetrics() {
    }

    public static InkBox measure(Identifier fontId, char c) {
        return CACHE.computeIfAbsent((int) c, codepoint -> measure(fontId, codepoint));
    }

    private static InkBox measure(Identifier fontId, int codepoint) {
        Minecraft minecraft = Minecraft.getInstance();
        FontManager fontManager = ((MinecraftFontManagerAccessor) minecraft).kaleidoscope_chinesefood$fontManager();
        if (fontManager == null) {
            return new InkBox(4.5F, 4.5F, 0.0F, 0.0F);
        }
        FontSet fontSet = ((FontManagerAccessor) fontManager).kaleidoscope_chinesefood$fontSets().get(fontId);
        if (fontSet == null) {
            return new InkBox(4.5F, 4.5F, 0.0F, 0.0F);
        }
        var glyph = fontSet.source(false).getGlyph(codepoint);
        if (glyph instanceof BakedSheetGlyph sheet) {
            BakedSheetGlyphAccessor accessor = (BakedSheetGlyphAccessor) sheet;
            return new InkBox(accessor.kaleidoscope_chinesefood$left(), accessor.kaleidoscope_chinesefood$right(),
                accessor.kaleidoscope_chinesefood$up(), accessor.kaleidoscope_chinesefood$down());
        }
        float advance = glyph.info().getAdvance();
        return new InkBox(advance / 2.0F, advance / 2.0F, 0.0F, 0.0F);
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
