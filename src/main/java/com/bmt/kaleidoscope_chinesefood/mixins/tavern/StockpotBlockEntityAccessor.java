package com.bmt.kaleidoscope_chinesefood.mixins.tavern;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 写 cookery 汤锅的汤底 id（水/岩浆）。 */
@Mixin(StockpotBlockEntity.class)
public interface StockpotBlockEntityAccessor {
    @Accessor("soupBaseId")
    void kaleidoscope_chinesefood$setSoupBaseId(Identifier soupBaseId);
}
