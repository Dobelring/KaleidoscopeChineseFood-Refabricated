package com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class HorizontalBannerBlockEntityRenderState extends BlockEntityRenderState {
    public String text;
    public int totalWidth = 1;
    public Direction facing;
}
