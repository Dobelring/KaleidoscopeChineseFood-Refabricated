package com.bmt.kaleidoscope_chinesefood.mixins.tavern;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({TeapotBlockEntity.class})
public interface TeapotBlockEntityAccessor {
    @Accessor("teaFluidId")
    void setTeaFluidId(ResourceLocation var1);
}
