package com.bmt.kaleidoscope_chinesefood.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderStateShard.LayeringStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class CustomRenderType extends RenderType {
    private static final LayeringStateShard CUSTOM_POLYGON_OFFSET_LAYERING = new LayeringStateShard("polygon_offset_layering", () -> {
        RenderSystem.polygonOffset(-0.25F, -10.0F);
        RenderSystem.enablePolygonOffset();
    }, () -> {
        RenderSystem.polygonOffset(0.0F, 0.0F);
        RenderSystem.disablePolygonOffset();
    });
    // RenderType#create 在原版 1.20.1 是包内可见，已在
    // kaleidoscope_chinesefood.accesswidener 里用 accessible method 放开
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
        String pName,
        VertexFormat pFormat,
        Mode pMode,
        int pBufferSize,
        boolean pAffectsCrumbling,
        boolean pSortOnUpload,
        Runnable pSetupState,
        Runnable pClearState
    ) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
        throw new UnsupportedOperationException();
    }
}
