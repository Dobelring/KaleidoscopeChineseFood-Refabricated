package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModFoodBiteRegistry;
import com.bmt.kaleidoscope_chinesefood.init.ModPlateRegistry;
import com.bmt.kaleidoscope_chinesefood.init.ModTea;
import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在 cookery 初始化之前填充三张注册数据表。
 * <p>
 * cookery 的 {@code CommonRegistry.init()}（其 {@code onInitialize} 的第一件事）会遍历
 * {@code PlateRegistry.PLATE_DATA_MAP} / {@code TeacupRegistry.TEACUP_DATA_MAP} /
 * {@code FoodBiteRegistry.FOOD_DATA_MAP}，把其中的条目注册成方块与物品。
 * <p>
 * 而 Fabric 的入口点顺序是「依赖模组先初始化」，cookery 必然早于本模组，
 * 因此本模组的盘装 / 茶杯 / 硬菜方块物品必须抢在 cookery 的这一步之前放进表里。
 * 不能用 preLaunch：那时原版 {@code Bootstrap} 还没跑，任何注册表访问都会
 * 抛 {@code Not bootstrapped}。
 */
@Mixin(KaleidoscopeCookery.class)
public class CookeryInitMixin {
    @Inject(method = "onInitialize", at = @At("HEAD"))
    private void kaleidoscope_chinesefood$registerCookeryDataBeforeInit(CallbackInfo ci) {
        ModTea.init();
        ModPlateRegistry.init();
        ModFoodBiteRegistry.init();
    }
}
