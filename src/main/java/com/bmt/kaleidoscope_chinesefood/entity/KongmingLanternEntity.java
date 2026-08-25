package com.bmt.kaleidoscope_chinesefood.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class KongmingLanternEntity extends Entity {
   private int lifeTime = 300;
   private BlockPos lastLightPos = BlockPos.ZERO;
   private int spawnTick = 0;
   private static final int INITIAL_SAFE_TICKS = 1;

   public KongmingLanternEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.blocksBuilding = true;
   }

   protected void defineSynchedData(@NotNull Builder builder) {
   }

   public void tick() {
      super.tick();
      this.spawnTick++;
      double currentY = this.getDeltaMovement().y;
      if (currentY < 0.15) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.008, 0.0));
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      float rotationYawSpeed = 0.8F;
      this.setYRot(this.getYRot() + rotationYawSpeed);
      if (this.random.nextFloat() < 0.05F) {
         this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() - 0.5F) * 0.02, 0.0, (this.random.nextFloat() - 0.5F) * 0.02));
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply(0.95, 0.98, 0.95));
      if (!this.level().isClientSide()) {
         BlockPos abovePos = this.blockPosition().above();
         if (this.spawnTick > 1 && this.level().getBlockState(abovePos).isSolid()) {
            // 上方被实体方块挡住：直接熄灭，绝不覆盖对方的方块。
            // remove() 会顺带清理尾焰光方块。
            this.discard();
         } else {
            // 尾焰光源只允许放进空气中；遇到草/花等可替换方块时跳过，
            // 不更新 lastLightPos（等飞回空中再继续），保证任何方块都不会被吞掉。
            if (this.spawnTick > 1 && !abovePos.equals(this.lastLightPos) && this.level().getBlockState(abovePos).isAir()) {
               if (this.level().getBlockState(this.lastLightPos).is(Blocks.LIGHT)) {
                  this.level().removeBlock(this.lastLightPos, false);
               }

               BlockState lightState = (BlockState)Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15);
               this.level().setBlock(abovePos, lightState, 3);
               this.lastLightPos = abovePos;
            }

            this.lifeTime--;
            if (this.lifeTime <= 0) {
               this.discard();
            }

            if (this.spawnTick > 1 && this.level().getBlockState(this.blockPosition()).is(Blocks.WATER)) {
               this.discard();
            }

            if (this.getY() > this.level().getMaxY() + 20) {
               this.discard();
            }
         }
      }
   }

   public boolean isInWall() {
      return this.spawnTick > 1 && super.isInWall();
   }

   @Override
   public boolean hurtServer(@NotNull ServerLevel serverLevel, @NotNull DamageSource damageSource, float amount) {
      return false;
   }

   protected void readAdditionalSaveData(@NotNull ValueInput input) {
      this.lifeTime = input.getIntOr("LifeTime", 300);
      this.spawnTick = input.getIntOr("SpawnTick", 0);
   }

   protected void addAdditionalSaveData(@NotNull ValueOutput output) {
      output.putInt("LifeTime", this.lifeTime);
      output.putInt("SpawnTick", this.spawnTick);
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket(@NotNull ServerEntity serverEntity) {
      return new ClientboundAddEntityPacket(this, serverEntity);
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public void remove(@NotNull RemovalReason reason) {
      super.remove(reason);
      if (!this.level().isClientSide() && this.level().getBlockState(this.lastLightPos).is(Blocks.LIGHT)) {
         this.level().removeBlock(this.lastLightPos, false);
      }
   }
}
