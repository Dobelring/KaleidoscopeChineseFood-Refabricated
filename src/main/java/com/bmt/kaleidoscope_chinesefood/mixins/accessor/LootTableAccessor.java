package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LootTable.class})
public interface LootTableAccessor {
    @Accessor("pools")
    LootPool[] getPools();

    @Mutable
    @Accessor("pools")
    void setPools(LootPool[] pools);
}
