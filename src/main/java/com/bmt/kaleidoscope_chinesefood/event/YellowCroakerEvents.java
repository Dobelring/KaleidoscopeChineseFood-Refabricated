package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

/**
 * 官方 1.1.16 新增：黄花鱼和猫交互。
 * <ul>
 * <li>右键野猫：85% 概率驯服（吃鱼音效、驯服失败红心/成功烟雾事件、防消失）；</li>
 * <li>右键自家猫：回血 4.0 / 发情繁殖 / 幼崽加速成长（速度 0.3）并切换坐下；</li>
 * <li>给所有猫挂 TemptGoal（CatMixin 注入 registerGoals，仅未驯服时激活）：会被黄花鱼引诱（带「腻了换人」逻辑）。</li>
 * </ul>
 * <p>
 * 原 Forge 用 {@code EntityInteract} / {@code EntityJoinLevelEvent} /
 * {@code EventHooks.onAnimalTame}；Fabric 改为 {@link UseEntityCallback}（返回
 * 非 PASS 即取消原版交互，sidedSuccess 语义一致）、goal 挂载走 {@code CatMixin}
 * （EntityJoinLevel 在 chunk 重载时会重复挂 goal），直接调用 {@code cat.tame}
 * （Fabric 无驯服事件）。
 */
public class YellowCroakerEvents {
    private static final float TAME_CHANCE = 0.85F;
    private static final float CAT_HEAL_AMOUNT = 4.0F;
    private static final float BABY_GROWTH_SPEED_UP = 0.3F;
    private static final double TEMPT_SPEED = 0.6;

    public YellowCroakerEvents() {
    }

    public static void register() {
        UseEntityCallback.EVENT.register(YellowCroakerEvents::onInteractCat);
    }

    /** 原 onInteractCat(EntityInteract)。 */
    private static InteractionResult onInteractCat(Player player, net.minecraft.world.level.Level level, InteractionHand hand, Entity entity, @Nullable net.minecraft.world.phys.EntityHitResult hitResult) {
        if (entity instanceof Cat cat && player.getItemInHand(hand).is(ModItems.YELLOW_CROAKER)) {
            ItemStack stack = player.getItemInHand(hand);
            boolean clientSide = level.isClientSide;
            if (!cat.isTame()) {
                if (!clientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    cat.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
                    if (cat.getRandom().nextFloat() < TAME_CHANCE) {
                        cat.tame(player);
                        cat.setOrderedToSit(true);
                        cat.level().broadcastEntityEvent(cat, (byte)7);
                    } else {
                        cat.level().broadcastEntityEvent(cat, (byte)6);
                    }

                    cat.setPersistenceRequired();
                }

                return InteractionResult.sidedSuccess(clientSide);
            }

            if (!cat.isOwnedBy(player)) {
                return InteractionResult.PASS;
            }

            if (cat.getHealth() < cat.getMaxHealth()) {
                if (!clientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    cat.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
                    cat.heal(CAT_HEAL_AMOUNT);
                }

                return InteractionResult.sidedSuccess(clientSide);
            }

            if (!cat.isBaby() && cat.canFallInLove()) {
                if (!clientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    cat.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
                    cat.setInLove(player);
                }

                return InteractionResult.SUCCESS;
            }

            if (cat.isBaby()) {
                if (!clientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }

                    cat.playSound(SoundEvents.CAT_EAT, 1.0F, 1.0F);
                    cat.ageUp((int)(-cat.getAge() / 20 * BABY_GROWTH_SPEED_UP), true);
                    if (cat.isBaby()) {
                        cat.setOrderedToSit(!cat.isOrderedToSit());
                    }
                }

                return InteractionResult.sidedSuccess(clientSide);
            }
        }

        return InteractionResult.PASS;
    }

    /** 原 onCatJoin(EntityJoinLevelEvent) 的 goal 构造（挂载改由 CatMixin 完成）。 */
    public static TemptGoal makeCroakerTemptGoal(Cat cat) {
        return new TemptGoal(cat, TEMPT_SPEED, Ingredient.of(ModItems.YELLOW_CROAKER), true) {
            @Nullable
            private Player boredOfPlayer;

            @Override
            public boolean canUse() {
                return super.canUse() && !cat.isTame();
            }

            @Override
            public boolean canContinueToUse() {
                return (this.boredOfPlayer == null || !this.boredOfPlayer.equals(this.player)) && super.canContinueToUse();
            }

            @Override
            public void tick() {
                super.tick();
                if (this.boredOfPlayer == null && cat.getRandom().nextInt(reducedTickDelay(600)) == 0) {
                    this.boredOfPlayer = this.player;
                } else if (cat.getRandom().nextInt(reducedTickDelay(500)) == 0) {
                    this.boredOfPlayer = null;
                }
            }
        };
    }
}
