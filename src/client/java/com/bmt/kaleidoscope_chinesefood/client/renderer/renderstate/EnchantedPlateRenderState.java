package com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

/**
 * 附魔金苹果拼盘：extract 阶段解析出拼盘方块模型，submit 阶段用附魔光效渲染类型再画一遍。
 */
public class EnchantedPlateRenderState extends BlockEntityRenderState {
    public final BlockModelRenderState model = new BlockModelRenderState();
}
