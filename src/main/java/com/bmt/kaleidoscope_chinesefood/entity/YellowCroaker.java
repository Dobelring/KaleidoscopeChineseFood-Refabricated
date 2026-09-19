package com.bmt.kaleidoscope_chinesefood.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class YellowCroaker extends AbstractSchoolingFish {
   public YellowCroaker(EntityType<? extends YellowCroaker> type, Level level) {
      super(type, level);
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack((ItemLike)ModItems.YELLOW_CROAKER_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.COD_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COD_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.COD_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.COD_FLOP;
   }
}
