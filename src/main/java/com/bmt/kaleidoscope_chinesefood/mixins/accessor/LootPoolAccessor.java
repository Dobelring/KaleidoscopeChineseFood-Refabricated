package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import java.util.List;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 读写 {@code LootPool#entries}（26.1.2 仍是 {@code private final List}，需 {@code @Mutable}）。 */
@Mixin(LootPool.class)
public interface LootPoolAccessor {
    @Accessor("entries")
    List<LootPoolEntryContainer> kaleidoscope_chinesefood$getEntries();

    @Mutable
    @Accessor("entries")
    void kaleidoscope_chinesefood$setEntries(List<LootPoolEntryContainer> entries);
}
