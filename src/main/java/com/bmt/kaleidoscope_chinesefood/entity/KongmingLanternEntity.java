package com.bmt.kaleidoscope_chinesefood.entity;

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
      if (!this.level().isClientSide() && this.spawnTick > 1) {
         BlockPos currentPos = this.blockPosition().above();
         if (!currentPos.equals(this.lastLightPos)) {
            if (this.level().getBlockState(this.lastLightPos).is(Blocks.LIGHT)) {
               this.level().removeBlock(this.lastLightPos, false);
            }

            // 只把尾焰光方块放进空气格：原版无条件 setBlock 会静默吞掉路径上的
            // 任意方块（另一盏孔明灯、屋顶等）。跳过非空气格后，
            // "上方实心即自毁"的判定也能正常生效（停在天花板下熄灭而非打洞）。
            if (this.level().getBlockState(currentPos).isAir()) {
               BlockState lightState = (BlockState)Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15);
               this.level().setBlock(currentPos, lightState, 3);
               this.lastLightPos = currentPos;
            }
         }
      }

      if (!this.level().isClientSide()) {
         this.lifeTime--;
         if (this.lifeTime <= 0) {
            this.discard();
         }

         if (this.spawnTick > 1) {
            if (this.level().getBlockState(this.blockPosition().above()).isSolid()) {
               this.discard();
            }

            if (this.level().getBlockState(this.blockPosition()).is(Blocks.WATER)) {
               this.discard();
            }
         }

         if (this.getY() > this.level().getMaxY() + 20) {
            this.discard();
         }
      }
   }

   public boolean isInWall() {
      return this.spawnTick > 1 && super.isInWall();
   }

   @Override
   public boolean hurtServer(@NotNull net.minecraft.server.level.ServerLevel level, @NotNull net.minecraft.world.damagesource.DamageSource source, float amount) {
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
