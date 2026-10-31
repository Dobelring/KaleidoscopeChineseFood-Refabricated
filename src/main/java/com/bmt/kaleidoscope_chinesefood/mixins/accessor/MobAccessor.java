package com.bmt.kaleidoscope_chinesefood.mixins.accessor;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * goalSelector 声明在 Mob 上（Cat 的父类）；@Shadow 在无 refmap 构建链下跨父类
 * 解析失败（见 CatMixin），改走 Accessor。
 */
@Mixin({Mob.class})
public interface MobAccessor {
    @Accessor("goalSelector")
    GoalSelector getGoalSelector();
}
