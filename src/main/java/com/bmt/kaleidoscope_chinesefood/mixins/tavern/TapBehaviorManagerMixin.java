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
 * 官方是在 {@code TapBlock#tryOpen}/{@code #tick} 里对 isMatch / onStartExtract / onEndExtract 做
 * {@code @WrapOperation}。这里改成在 {@link TapBehaviorManager#get} 的返回值上包一层组合行为：
 * {@code @Inject} 可叠加，不占指令，与其他模组（世界名酒等）的龙头行为共存。
 * 整份配置由 {@link TavernMixinConfigPlugin} 守卫，未装酒馆时不生效。
 */
@Mixin(TapBehaviorManager.class)
public abstract class TapBehaviorManagerMixin {
    @Inject(method = "get", at = @At("RETURN"), cancellable = true)
    private static void kaleidoscope_chinesefood$wrapWithCookeryBehavior(
            BlockState sourceState, CallbackInfoReturnable<ITapBehavior> cir
    ) {
        ITapBehavior original = cir.getReturnValue();
        if (original != null) {
            cir.setReturnValue(CookeryTapBehavior.of(original));
        }
    }
}
