package com.bmt.kaleidoscope_chinesefood.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Forge 版目标是 Forge 专有的 CreativeModeTabRegistry#getSortedCreativeModeTabs。
 * Fabric 无该注册表：创造栏标签页列表由 CreativeModeTabs#tabs()（即 CREATIVE_MODE_TAB 注册表的注册顺序）提供，
 * 因此改为修饰该方法的返回值，保持「本模组标签页紧跟在 cookery 食物标签页之后」这一原有行为。
 */
@Mixin(
    value = {CreativeModeTabs.class},
    priority = 900
)
public abstract class CreativeModeTabsMixin {
    public CreativeModeTabsMixin() {
    }

    @ModifyReturnValue(
        method = {"tabs"},
        at = {@At("RETURN")}
    )
    private static List<CreativeModeTab> kaleidoscopeChinesefood$moveTabAfterCookeryFood(List<CreativeModeTab> tabs) {
        CreativeModeTab cookeryFood = BuiltInRegistries.CREATIVE_MODE_TAB
            .getOptional(new ResourceLocation("kaleidoscope_cookery", "cookery_food"))
            .orElse(null);
        CreativeModeTab ourTab = BuiltInRegistries.CREATIVE_MODE_TAB
            .getOptional(new ResourceLocation("kaleidoscope_chinesefood", "kaleidoscope_chinesefood_tab"))
            .orElse(null);
        if (cookeryFood != null && ourTab != null) {
            int cookeryIndex = tabs.indexOf(cookeryFood);
            int ourIndex = tabs.indexOf(ourTab);
            if (cookeryIndex == -1 || ourIndex == -1) {
                return tabs;
            } else if (ourIndex == cookeryIndex + 1) {
                return tabs;
            } else {
                List<CreativeModeTab> result = new ArrayList<>(tabs);
                result.remove(ourIndex);
                if (ourIndex < cookeryIndex) {
                    cookeryIndex--;
                }

                result.add(cookeryIndex + 1, ourTab);
                return result;
            }
        } else {
            return tabs;
        }
    }
}
