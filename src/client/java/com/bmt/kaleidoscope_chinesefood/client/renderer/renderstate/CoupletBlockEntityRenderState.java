package com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

/**
 * 1.21.11 渲染改为 state/submit 模型，对联文本在 extract 阶段采集。
 */
public class CoupletBlockEntityRenderState extends BlockEntityRenderState {
    public String text;
    public boolean isTriple;
    public Direction facing;
}
