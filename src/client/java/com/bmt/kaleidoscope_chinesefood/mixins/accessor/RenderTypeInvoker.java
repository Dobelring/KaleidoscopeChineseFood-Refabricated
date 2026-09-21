package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 打开 {@code RenderType#create(String, RenderSetup)}（26.1.2 里是包内可见）。
 * <p>
 * 自建渲染类型只改 RenderSetup（贴图 / 纹理变换 / 层偏移），管线仍用原版的，
 * 因此不需要新的 RenderPipeline，也就不涉及管线注册。
 */
@Mixin(RenderType.class)
public interface RenderTypeInvoker {
    @Invoker("create")
    static RenderType kaleidoscope_chinesefood$create(String name, RenderSetup setup) {
        throw new AssertionError();
    }
}
