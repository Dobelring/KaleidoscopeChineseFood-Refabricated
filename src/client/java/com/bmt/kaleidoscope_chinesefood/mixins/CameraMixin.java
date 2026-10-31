package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 官方 1.1.16 的 oculus 兼容修复：相机在岩浆中时若玩家有岩浆畅游效果，
 * 让 {@code getFluidInCamera} 返回 NONE——岩浆雾/雾色从源头（唯一采样点）消失。
 * 原版雾渲染与 shader 模组（Oculus/Iris 等）都以此为准，因此对两者同时生效。
 * 原 Forge 方案（ComputeFogColor/RenderFog 事件）在 shader 模组接管雾管线时失效，
 * 且 Fabric 侧根本没有对应事件（1.20.1 旧移植版是未接线的死代码）。
 */
@Mixin({Camera.class})
public abstract class CameraMixin {
    public CameraMixin() {
    }

    @Inject(
        method = {"getFluidInCamera"},
        at = {@At("RETURN")},
        cancellable = true
    )
    private void kaleidoscope$removeLavaFog(CallbackInfoReturnable<FogType> cir) {
        if (cir.getReturnValue() == FogType.LAVA
            && ((Camera) (Object) this).entity() instanceof Player player
            && player.hasEffect(ModEffects.LAVA_SWIM)) {
            cir.setReturnValue(FogType.NONE);
        }
    }
}
