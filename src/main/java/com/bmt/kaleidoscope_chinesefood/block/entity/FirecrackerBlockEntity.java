package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FirecrackerBlockEntity extends BlockEntity {
   private int fuse = -1;
   private static final Random RANDOM = new Random();
   // 26.1: DustParticleOptions now takes an ARGB int color instead of Vector3f
   private static final int[] FIREWORK_COLORS = new int[]{
      0xFF0000,
      0xFF8000,
      0xFFFF00,
      0x00FF00,
      0x00FFFF,
      0x0000FF,
      0x8000FF,
      0xFF00FF
   };

   public FirecrackerBlockEntity(BlockPos pPos, BlockState pBlockState) {
      super(ModBlockEntities.FIRECRACKER, pPos, pBlockState);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, FirecrackerBlockEntity be) {
      if (be.fuse > 0) {
         be.fuse--;
         be.setChanged();
         if (!level.isClientSide()) {
            level.sendBlockUpdated(pos, state, state, 3);
         }

         if (level.isClientSide()) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.6;
            double z = pos.getZ() + 0.5;

            for (int i = 0; i < 2; i++) {
               level.addParticle(ParticleTypes.FLAME, x + (RANDOM.nextDouble() - 0.5) * 0.05, y, z + (RANDOM.nextDouble() - 0.5) * 0.05, 0.0, 0.05, 0.0);
            }

            level.addParticle(ParticleTypes.SMOKE, x + (RANDOM.nextDouble() - 0.5) * 0.03, y + 0.08, z + (RANDOM.nextDouble() - 0.5) * 0.03, 0.0, 0.03, 0.0);
         }

         if (be.fuse == 0 && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, 1.0F);
            spawnFireworks(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            level.removeBlock(pos, false);
         }
      }
   }

   public void ignite() {
      if (this.fuse == -1) {
         this.fuse = 30;
         this.setChanged();
         if (this.level != null && !this.level.isClientSide()) {
            this.level
               .playSound(
                  null,
                  this.worldPosition.getX() + 0.5,
                  this.worldPosition.getY() + 0.5,
                  this.worldPosition.getZ() + 0.5,
                  SoundEvents.CREEPER_PRIMED,
                  SoundSource.BLOCKS,
                  1.0F,
                  1.0F
               );
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         }
      }
   }

   private static void spawnFireworks(ServerLevel level, double x, double y, double z) {
      int type = RANDOM.nextInt(5);
      int color = FIREWORK_COLORS[RANDOM.nextInt(FIREWORK_COLORS.length)];
      DustParticleOptions dust = new DustParticleOptions(color, 1.0F);
      switch (type) {
         case 0:
            level.sendParticles(dust, x, y, z, 50, 0.3, 0.3, 0.3, 0.1);
            level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 24, 0.3, 0.3, 0.3, 0.1);
            break;
         case 1:
            level.sendParticles(dust, x, y, z, 70, 0.7, 0.7, 0.7, 0.18);
            level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 30, 0.7, 0.7, 0.7, 0.18);
            break;
         case 2:
            level.sendParticles(dust, x, y, z, 45, 0.2, 0.6, 0.2, 0.12);
            level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 18, 0.2, 0.6, 0.2, 0.12);
            break;
         case 3:
            int color2 = FIREWORK_COLORS[RANDOM.nextInt(FIREWORK_COLORS.length)];
            DustParticleOptions dust2 = new DustParticleOptions(color2, 1.0F);
            level.sendParticles(dust, x, y, z, 35, 0.35, 0.35, 0.35, 0.08);
            level.sendParticles(dust2, x, y + 0.4, z, 28, 0.5, 0.5, 0.5, 0.12);
            level.sendParticles(ParticleTypes.FIREWORK, x, y + 0.2, z, 24, 0.4, 0.4, 0.4, 0.1);
            break;
         case 4:
            level.sendParticles(dust, x, y, z, 22, 0.6, 0.2, 0.0, 0.12);
            level.sendParticles(dust, x, y, z, 22, 0.0, 0.2, 0.6, 0.12);
            level.sendParticles(dust, x, y, z, 22, -0.6, 0.2, 0.0, 0.12);
            level.sendParticles(dust, x, y, z, 22, 0.0, 0.2, -0.6, 0.12);
            level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 14, 0.4, 0.2, 0.4, 0.12);
      }
   }

   protected void saveAdditional(ValueOutput output) {
      super.saveAdditional(output);
      output.putInt("Fuse", this.fuse);
   }

   protected void loadAdditional(ValueInput input) {
      super.loadAdditional(input);
      this.fuse = input.getIntOr("Fuse", -1);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putInt("Fuse", this.fuse);
      return tag;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
