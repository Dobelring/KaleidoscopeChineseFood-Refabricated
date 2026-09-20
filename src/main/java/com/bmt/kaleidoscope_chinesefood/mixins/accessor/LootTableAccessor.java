package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import java.util.List;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 读 {@code LootTable#pools}，供 {@code ModLootTableEvents} 往 pool 0 追加条目。 */
@Mixin(LootTable.class)
public interface LootTableAccessor {
    @Accessor("pools")
    List<LootPool> kaleidoscope_chinesefood$getPools();
}
