package com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 附魔金苹果拼盘：extract 阶段记下方块状态，submit 阶段直接向烘焙好的模型取部件，
 * 用原版附魔光效再画一遍。
 */
public class EnchantedPlateRenderState extends BlockEntityRenderState {
    public BlockState blockState;
}
