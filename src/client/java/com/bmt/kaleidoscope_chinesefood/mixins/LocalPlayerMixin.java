package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 官方 1.1.16 岩浆畅游补全（客户端输入侧，26.x）：
 * 26.x 的双击 W 起跑（canStartSprinting）无流体门槛、在岩浆里能起跑，
 * 但游起来后 {@code shouldStopSwimSprinting} 的 {@code !isInWater()} 下一拍就把疾跑清掉
 * → 游泳旗标跟着被 updateSwimming 复位，实测表现为无法持续游泳。
 * 补两处采样点：aiStep 内的 isInWater（潜行下沉判定，顺带与水中一致）、
 * shouldStopSwimSprinting 内的 isInWater（维持判定）。
 * 26.x aiStep 内没有 isUnderWater 采样点，不加对应注入（require=1 下零命中会崩）。
 * @At owner 写 LocalPlayer：javac 对 this.xxx() 的 methodref owner 是接收者静态类型，
 * 原版调用点 owner 即 LocalPlayer（26.x 无混淆，字符串原样匹配）。
 */
@Mixin({LocalPlayer.class})
public abstract class LocalPlayerMixin {
    public LocalPlayerMixin() {
    }

    @ModifyExpressionValue(
        method = {"aiStep"},
        at = {@At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isInWater()Z"
        )}
    )
    private boolean kaleidoscope$aiStepInWater(boolean original) {
        return original || this.kaleidoscope$lavaSwimming();
    }

    @ModifyExpressionValue(
        method = {"shouldStopSwimSprinting"},
        at = {@At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isInWater()Z"
        )}
    )
    private boolean kaleidoscope$swimSprintMaintain(boolean original) {
        return original || this.kaleidoscope$lavaSwimming();
    }

    private boolean kaleidoscope$lavaSwimming() {
        LocalPlayer self = (LocalPlayer) (Object) this;
        return self.isInLava() && self.hasEffect(ModEffects.LAVA_SWIM);
    }
}
