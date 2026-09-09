package com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class KongmingLanternRenderState extends EntityRenderState {
    public float yRot;
    // 26.1 BER：extract 期解析好的方块模型状态（孔明灯本体）
    public final BlockModelRenderState model = new BlockModelRenderState();
}
