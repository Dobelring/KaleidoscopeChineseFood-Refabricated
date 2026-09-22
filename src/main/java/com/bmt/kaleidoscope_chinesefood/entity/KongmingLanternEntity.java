package com.bmt.kaleidoscope_chinesefood.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;

public class KongmingLanternEntity extends Entity {
    private int lifeTime = 400;
    private float rotationYawSpeed = 0.8F;
    private BlockPos lastLightPos = BlockPos.ZERO;
    private int spawnTick = 0;
    private static final int INITIAL_SAFE_TICKS = 1;

    public KongmingLanternEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public KongmingLanternEntity(Level level, double x, double y, double z) {
        this(ModEntities.KONGMING_LANTERN, level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        this.spawnTick++;
        double currentY = this.getDeltaMovement().y;
        if (currentY < 0.15) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.008, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setYRot(this.getYRot() + this.rotationYawSpeed);
        if (this.random.nextFloat() < 0.05F) {
            this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() - 0.5F) * 0.02, 0.0, (this.random.nextFloat() - 0.5F) * 0.02));
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(0.95, 0.98, 0.95));
        if (!this.level().isClientSide && this.spawnTick > 1) {
            BlockPos currentPos = this.blockPosition().above();
            if (!currentPos.equals(this.lastLightPos)) {
                if (this.level().getBlockState(this.lastLightPos).is(Blocks.LIGHT)) {
                    this.level().removeBlock(this.lastLightPos, false);
                }

                BlockState lightState = (BlockState)Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15);
                this.level().setBlock(currentPos, lightState, 3);
                this.lastLightPos = currentPos;
            }
        }

        if (!this.level().isClientSide) {
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

            if (this.getY() > this.level().getMaxBuildHeight() + 20) {
                this.discard();
            }
        }
    }

    @Override
    public boolean isInWall() {
        return this.spawnTick > 1 && super.isInWall();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("LifeTime")) {
            this.lifeTime = tag.getInt("LifeTime");
        }

        if (tag.contains("SpawnTick")) {
            this.spawnTick = tag.getInt("SpawnTick");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("LifeTime", this.lifeTime);
        tag.putInt("SpawnTick", this.spawnTick);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (!this.level().isClientSide && this.level().getBlockState(this.lastLightPos).is(Blocks.LIGHT)) {
            this.level().removeBlock(this.lastLightPos, false);
        }
    }
}
