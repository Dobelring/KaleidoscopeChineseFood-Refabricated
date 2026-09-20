package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import java.util.List;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 读 {@code BlockModelRenderState#modelParts}（26.1.2 只有 private 字段，无 getter）。
 * 附魔拼盘渲染器要用它把同一批模型部件换成附魔光效渲染类型重画一遍。
 */
@Mixin(BlockModelRenderState.class)
public interface BlockModelRenderStateAccessor {
    @Accessor("modelParts")
    @Nullable
    List<BlockStateModelPart> kaleidoscope_chinesefood$getModelParts();
}
