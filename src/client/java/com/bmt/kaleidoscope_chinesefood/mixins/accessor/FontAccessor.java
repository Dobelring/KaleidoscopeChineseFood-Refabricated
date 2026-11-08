package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import java.util.function.Function;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 1.2.0:暴露 Font 的字体集映射,供 GlyphMetrics 测量自定义字体(couplet_font)字形墨迹框。
 */
@Mixin(Font.class)
public interface FontAccessor {
    @Accessor("fonts")
    Function<ResourceLocation, FontSet> kaleidoscope_chinesefood$fonts();
}
