package com.bmt.kaleidoscope_chinesefood.mixins;

import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 26.1.2 猫音效改为 variant 驱动（SoundEvents.CAT_EAT 已删），
 * 用 @Invoker 暴露原版 {@code playEatingSound}（按猫的音效变体播放吃食音）。
 * 独立接口 mixin：事件类里 cast 编译期需要接口形态。
 */
@Mixin({Cat.class})
public interface CatInvoker {
    @Invoker("playEatingSound")
    void kaleidoscope$playEatingSound();
}
