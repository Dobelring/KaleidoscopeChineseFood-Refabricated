package com.bmt.kaleidoscope_chinesefood.client.renderer.renderstate;

import java.util.List;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class PickleJarBlockEntityRenderState extends BlockEntityRenderState {
    public List<Piece> pieces = List.of();

    public record Piece(ItemStackRenderState item, float x, float y, float z, float yRot, float scale) {
    }
}
