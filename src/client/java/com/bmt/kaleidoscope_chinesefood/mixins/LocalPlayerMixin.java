package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 官方 1.1.16 岩浆畅游补全（客户端输入侧，1.20.1/1.21.1）：
 * updateSwimming/travel 只把物理当水，但 {@code aiStep} 里的疾跑/游泳判定仍按原样判岩浆——
 * <ul>
 * <li>双击 W 起跑闸门 {@code (onGround || isUnderWater())}：岩浆里两者皆否 → 起不了跑；</li>
 * <li>疾跑键闸门 {@code (!isInWater() || isUnderWater())}；</li>
 * <li>维持检查 {@code isInWater() && !isUnderWater()} 与 isSwimming 分支的 {@code !isInWater()}。</li>
 * </ul>
 * 将 aiStep 内这两类采样点在岩浆畅游下视为水，使整套判定序列与原版水中完全一致。
 * @At owner 必须写 LocalPlayer：javac 对 this.xxx() 生成的 methodref owner 是接收者静态类型
 * （LocalPlayer）而非声明类（Entity），生产环境（intermediary）调用点 owner=class_746，
 * 写 Entity 会导致注入点零命中（实测 19:05 崩溃）。
 * 注意 {@code hasEnoughImpulseToStartSprinting} 在 input.tick 之前用上一拍旧输入求值
 * （双击检测本体），其内部采样点不在此列——旧拍未按 W 时本来就为 false，语义正确。
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
        method = {"aiStep"},
        at = {@At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUnderWater()Z"
        )}
    )
    private boolean kaleidoscope$aiStepUnderWater(boolean original) {
        return original || this.kaleidoscope$lavaSwimming();
    }

    private boolean kaleidoscope$lavaSwimming() {
        LocalPlayer self = (LocalPlayer) (Object) this;
        return self.isInLava() && self.hasEffect(ModEffects.LAVA_SWIM);
    }
}
