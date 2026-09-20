package com.bmt.kaleidoscope_chinesefood.mixins.tavern;

import com.bmt.kaleidoscope_chinesefood.compat.kaleidoscope_tavern.CookeryTapBehavior;
import com.github.ysbbbbbb.kaleidoscopetavern.api.blockentity.ITapBehavior;
import com.github.ysbbbbbb.kaleidoscopetavern.game.tap.TapBehaviorManager;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 让酒馆的水龙头能给厨房的汤锅/茶壶注入水和岩浆。
 * <p>
 * 官方是在 {@code TapBlock#tryOpen}/{@code #tick} 里对 isMatch / onStartExtract / onEndExtract
 * 做 {@code @WrapOperation}；但 {@code @Redirect}/{@code @WrapOperation} 这类改写的注入器对同一条指令互斥，
 * 而酒水模组已经用 {@code @Redirect} 占了那几处，照搬会直接崩启动。
 * 这里改为在 {@link TapBehaviorManager#get} 的返回值上包一层组合行为（{@code @Inject} 可叠加），
 * 效果等价且与其他模组共存。
 */
@Mixin(TapBehaviorManager.class)
public abstract class TapBehaviorManagerMixin {
    @Inject(method = "get", at = @At("RETURN"), cancellable = true, remap = false)
    private static void kaleidoscope_chinesefood$wrapWithCookeryBehavior(
            BlockState sourceState, CallbackInfoReturnable<ITapBehavior> cir
    ) {
        ITapBehavior original = cir.getReturnValue();
        if (original != null) {
            cir.setReturnValue(CookeryTapBehavior.of(original));
        }
    }
}
