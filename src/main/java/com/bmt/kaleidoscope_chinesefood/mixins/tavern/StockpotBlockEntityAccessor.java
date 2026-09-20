package com.bmt.kaleidoscope_chinesefood.mixins.tavern;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StockpotBlockEntity.class)
public interface StockpotBlockEntityAccessor {
    @Accessor("soupBaseId")
    void kaleidoscope_chinesefood$setSoupBaseId(ResourceLocation soupBaseId);
}
