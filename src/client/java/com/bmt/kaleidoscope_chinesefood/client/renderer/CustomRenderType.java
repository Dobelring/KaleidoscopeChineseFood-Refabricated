package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderStateShard.LayeringStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.entity.ItemRenderer;

/**
 * 附魔拼盘方块用的附魔光效渲染类型。
 * <p>
 * 与 {@code RenderType#glint} 的唯一区别是多了一层 polygon offset —— 方块实体渲染器是把
 * 方块本体模型再画一遍，光效与方块表面完全共面，不加偏移会 z-fighting。
 */
public class CustomRenderType extends RenderType {
    private static final LayeringStateShard CUSTOM_POLYGON_OFFSET_LAYERING = new LayeringStateShard(
            "polygon_offset_layering",
            () -> {
                RenderSystem.polygonOffset(-0.25F, -10.0F);
                RenderSystem.enablePolygonOffset();
            },
            () -> {
                RenderSystem.polygonOffset(0.0F, 0.0F);
                RenderSystem.disablePolygonOffset();
            }
    );

    public static final RenderType GLINT = create(
            "glint",
            DefaultVertexFormat.POSITION_TEX,
            Mode.QUADS,
            1536,
            false,
            false,
            CompositeState.builder()
                    .setShaderState(RENDERTYPE_GLINT_SHADER)
                    .setTextureState(new TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false))
                    .setWriteMaskState(COLOR_WRITE)
                    .setCullState(NO_CULL)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setTransparencyState(GLINT_TRANSPARENCY)
                    .setTexturingState(GLINT_TEXTURING)
                    .setLayeringState(CUSTOM_POLYGON_OFFSET_LAYERING)
                    .createCompositeState(false)
    );

    private CustomRenderType(
            String name,
            VertexFormat format,
            Mode mode,
            int bufferSize,
            boolean affectsCrumbling,
            boolean sortOnUpload,
            Runnable setupState,
            Runnable clearState
    ) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        throw new UnsupportedOperationException();
    }
}
