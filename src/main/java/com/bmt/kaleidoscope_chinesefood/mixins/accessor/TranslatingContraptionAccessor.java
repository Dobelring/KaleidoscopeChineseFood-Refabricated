package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import com.simibubi.create.content.contraptions.TranslatingContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/**
 * 暴露 Create TranslatingContraption 的碰撞体缓存，方块数据变更后置空强制重算
 * （原官方经 kaleidoscope_contraption 的 accessor，Fabric 侧自建）。
 */
@Mixin(TranslatingContraption.class)
public interface TranslatingContraptionAccessor {
    @Accessor("cachedColliders")
    void kaleidoscope_chinesefood$setCachedColliders(Set<BlockPos> colliders);

    @Accessor("cachedColliderDirection")
    void kaleidoscope_chinesefood$setCachedColliderDirection(Direction direction);
}
