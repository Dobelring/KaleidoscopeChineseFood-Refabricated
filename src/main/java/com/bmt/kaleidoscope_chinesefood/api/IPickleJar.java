package com.bmt.kaleidoscope_chinesefood.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 1.2.0：泡菜坛操作接口（对齐官方 api.IPickleJar），
 * 解耦方块类，供 Create 机械上的虚拟泡菜坛复用。
 */
public interface IPickleJar {
    int getProgress();

    int getMaxProgress();

    boolean tryStartFermenting(Level level);

    void resetProgress();

    boolean insertItem(ItemStack stack, Player player);

    boolean extractItem(Player player, ItemStack held);

    boolean isEmpty();
}
