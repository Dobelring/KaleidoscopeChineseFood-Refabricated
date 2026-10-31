package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.event.YellowCroakerEvents;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.MobAccessor;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 官方 1.1.16：给猫挂黄花鱼 TemptGoal（引诱未驯服的猫，goal 的 canUse 已做 isTame 门控）。
 * 原 Forge 在 EntityJoinLevelEvent 里往 goalSelector 加 goal（chunk 重载会重复挂）。
 * 改为注入 {@code registerGoals}（Mob 构造器末尾调用，每实例恰好一次，
 * 所有猫都会挂上该 goal，只有未驯服时激活）。
 * goalSelector 是 protected 且声明在父类 Mob 上，@Shadow 跨父类解析在无 refmap 链下失败，走 MobAccessor。
 */
@Mixin({Cat.class})
public abstract class CatMixin {
    public CatMixin() {
    }

    @Inject(
        method = {"registerGoals"},
        at = {@At("TAIL")}
    )
    private void kaleidoscope$addCroakerTemptGoal(CallbackInfo ci) {
        ((MobAccessor) this).getGoalSelector().addGoal(4, YellowCroakerEvents.makeCroakerTemptGoal((Cat) (Object) this));
    }
}
