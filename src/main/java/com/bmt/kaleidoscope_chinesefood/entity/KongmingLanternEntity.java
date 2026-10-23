package com.bmt.kaleidoscope_chinesefood.entity;

import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class KongmingLanternEntity extends Entity {
   private int lifeTime = 400;
   private int spawnTick = 0;
   private static final int INITIAL_SAFE_TICKS = 1;
   private double riseSpeed = -1.0;
   private Random sharedRandom;
   private boolean spinInitialized;
   private boolean spinning;
   private int spinPhaseTicks;
   private float spinTargetSpeed;
   private float spinCurrentSpeed;
   private float visualYaw;
   private float visualYawO;

   public KongmingLanternEntity(EntityType<?> entityType, Level level) {
      super(entityType, level);
      this.blocksBuilding = true;
   }

   private Random seededRandom() {
      if (this.sharedRandom == null) {
         UUID uuid = this.getUUID();
         this.sharedRandom = new Random(uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits());
      }

      return this.sharedRandom;
   }

   protected void defineSynchedData(@NotNull Builder builder) {
   }

   public void tick() {
      super.tick();
      this.spawnTick++;
      if (this.riseSpeed < 0.0) {
         this.riseSpeed = 0.1 + this.seededRandom().nextDouble() * 0.07;
      }

      double currentY = this.getDeltaMovement().y;
      if (currentY < this.riseSpeed) {
         this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.008, 0.0));
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      if (this.level().isClientSide) {
         this.tickVisualSpin();
      }

      if (this.random.nextFloat() < 0.05F) {
         this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() - 0.5F) * 0.02, 0.0, (this.random.nextFloat() - 0.5F) * 0.02));
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply(0.95, 0.98, 0.95));
      if (!this.level().isClientSide) {
         this.lifeTime--;
         if (this.lifeTime <= 0) {
            this.discard();
         }

         if (this.spawnTick > 1) {
            // 官方 1.1.15 此处改为无声移除头顶方块；按拍板保留移植版原有的撞头自毁
            if (this.level().getBlockState(this.blockPosition().above()).isSolid()) {
               this.discard();
            }

            if (this.level().getBlockState(this.blockPosition()).is(Blocks.WATER)) {
               this.discard();
            }
         }

         if (this.getY() > this.level().getMaxBuildHeight() + 20) {
            this.discard();
         }
      }
   }

   private void tickVisualSpin() {
      Random rand = this.seededRandom();
      if (!this.spinInitialized) {
         this.spinInitialized = true;
         this.visualYaw = 0.0F;
         this.visualYawO = 0.0F;
         this.spinning = rand.nextBoolean();
         this.spinPhaseTicks = this.spinning ? 60 + rand.nextInt(181) : 30 + rand.nextInt(151);
         this.spinTargetSpeed = this.spinning ? nextSpinSpeed(rand) : 0.0F;
         this.spinCurrentSpeed = 0.0F;
      }

      if (--this.spinPhaseTicks <= 0) {
         this.spinning = !this.spinning;
         this.spinPhaseTicks = this.spinning ? 60 + rand.nextInt(181) : 30 + rand.nextInt(151);
         this.spinTargetSpeed = this.spinning ? nextSpinSpeed(rand) : 0.0F;
      }

      this.spinCurrentSpeed = this.spinCurrentSpeed + (this.spinTargetSpeed - this.spinCurrentSpeed) * 0.05F;
      this.visualYawO = this.visualYaw;
      this.visualYaw = this.visualYaw + this.spinCurrentSpeed;
   }

   private static float nextSpinSpeed(Random rand) {
      float magnitude = 0.25F + rand.nextFloat() * 1.05F;
      return rand.nextBoolean() ? magnitude : -magnitude;
   }

   public float getVisualYaw(float partialTicks) {
      return this.visualYawO + (this.visualYaw - this.visualYawO) * partialTicks;
   }

   public boolean isInWall() {
      return this.spawnTick > 1 && super.isInWall();
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
      if (tag.contains("LifeTime")) {
         this.lifeTime = tag.getInt("LifeTime");
      }

      if (tag.contains("SpawnTick")) {
         this.spawnTick = tag.getInt("SpawnTick");
      }
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
      tag.putInt("LifeTime", this.lifeTime);
      tag.putInt("SpawnTick", this.spawnTick);
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
}
