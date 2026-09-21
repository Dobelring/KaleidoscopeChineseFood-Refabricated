package com.bmt.kaleidoscope_chinesefood.mixins.tavern;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 写 cookery 茶壶的液体 id（水/岩浆）。 */
@Mixin(TeapotBlockEntity.class)
public interface TeapotBlockEntityAccessor {
    @Accessor("teaFluidId")
    void kaleidoscope_chinesefood$setTeaFluidId(Identifier teaFluidId);
}
