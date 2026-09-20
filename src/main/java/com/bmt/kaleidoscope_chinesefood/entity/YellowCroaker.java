package com.bmt.kaleidoscope_chinesefood.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.fish.AbstractSchoolingFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** 黄花鱼：成群游动的海洋鱼，可装桶。 */
public class YellowCroaker extends AbstractSchoolingFish {
    public YellowCroaker(EntityType<? extends YellowCroaker> type, Level level) {
        super(type, level);
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.YELLOW_CROAKER_BUCKET);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.COD_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.COD_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.COD_HURT;
    }

    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.COD_FLOP;
    }
}
