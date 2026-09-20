package com.bmt.kaleidoscope_chinesefood.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 让 cookery 的效果注册变成幂等，好让 chinesefood 能在自己的初始化阶段提前调用它。
 * <p>
 * 原因：cookery 的效果 Holder 只在它自己的 {@code onInitialize} 里赋值，而 chinesefood 按 mod id
 * 排在 cookery 之前。{@code ModFoods} 必须在 chinesefood 初始化时就建好（菜品的 FoodProperties
 * 要提前交给 cookery 的 FoodBite/Plate/Teacup 数据表），此时读 cookery 的效果字段会拿到 null，
 * 效果实例就永久带着 null Holder —— 吃了没有任何效果。官方在 NeoForge 上用
 * {@code FoodProperties.Builder#effect(Supplier, float)} 惰性求值绕开了这一点，原版没有这个重载，
 * 所以这里改为提前把 cookery 的效果注册掉。
 */
@Mixin(com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.class)
public abstract class CookeryModEffectsMixin {
    @Unique
    private static boolean kaleidoscope_chinesefood$registered;

    @Inject(method = "registerEffects", at = @At("HEAD"), cancellable = true, remap = false)
    private static void kaleidoscope_chinesefood$registerOnce(CallbackInfo ci) {
        if (kaleidoscope_chinesefood$registered) {
            ci.cancel();
        } else {
            kaleidoscope_chinesefood$registered = true;
        }
    }
}
