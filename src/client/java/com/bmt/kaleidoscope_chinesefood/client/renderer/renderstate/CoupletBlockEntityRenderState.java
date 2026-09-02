package com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

/**
 * state/submit 渲染模型：对联文本在 extract 阶段采集到渲染状态。
 */
public class CoupletBlockEntityRenderState extends BlockEntityRenderState {
    public String text;
    public boolean isTriple;
    public Direction facing;
}
