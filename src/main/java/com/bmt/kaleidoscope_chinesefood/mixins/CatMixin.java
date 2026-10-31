package com.bmt.kaleidoscope_chinesefood.mixins;

import com.bmt.kaleidoscope_chinesefood.event.YellowCroakerEvents;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.MobAccessor;
import net.minecraft.world.entity.animal.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 官方 1.1.16：给猫挂黄花鱼 TemptGoal（引诱未驯服的猫，goal 的 canUse 已做 isTame 门控）。
 * 原 Forge 在 EntityJoinLevelEvent 里往 goalSelector 加 goal；1.20.1 Fabric 的
 * fabric-api 0.92 还没有 ServerEntityEvents.ENTITY_LOAD（1.20.2+ 才加入），
 * 且 goalSelector 是 protected——改为注入 {@code registerGoals}（Mob 构造器末尾调用，
 * 与「加入世界」时机等价：所有猫都会挂上该 goal，只有未驯服时激活）。
 * goalSelector 声明在父类 Mob 上，@Shadow 跨父类解析在无 refmap 链下失败，走 MobAccessor。
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
