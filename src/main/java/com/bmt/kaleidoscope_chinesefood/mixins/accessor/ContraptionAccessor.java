package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * 暴露 Create Contraption 的 updateTags 增量同步表
 * （原官方通过 kaleidoscope_contraption 的 accessor 访问，Fabric 侧 create-fly
 * 的 Contraption 本就带此字段，自建 accessor 即可）。
 */
@Mixin(Contraption.class)
public interface ContraptionAccessor {
    @Accessor("updateTags")
    Map<BlockPos, CompoundTag> kaleidoscope_chinesefood$getUpdateTags();
}
