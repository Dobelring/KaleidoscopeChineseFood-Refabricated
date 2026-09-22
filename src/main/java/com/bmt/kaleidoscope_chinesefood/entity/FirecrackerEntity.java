package com.bmt.kaleidoscope_chinesefood.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModEntities;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import java.util.Random;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

public class FirecrackerEntity extends ThrowableItemProjectile {
    private static final Random RANDOM = new Random();
    private static final Vector3f[] FIREWORK_COLORS = new Vector3f[]{
        new Vector3f(1.0F, 0.0F, 0.0F),
        new Vector3f(1.0F, 0.5F, 0.0F),
        new Vector3f(1.0F, 1.0F, 0.0F),
        new Vector3f(0.0F, 1.0F, 0.0F),
        new Vector3f(0.0F, 1.0F, 1.0F),
        new Vector3f(0.0F, 0.0F, 1.0F),
        new Vector3f(0.5F, 0.0F, 1.0F),
        new Vector3f(1.0F, 0.0F, 1.0F)
    };

    public FirecrackerEntity(EntityType<? extends ThrowableItemProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public FirecrackerEntity(Level pLevel, LivingEntity pShooter) {
        super(ModEntities.FIRECRACKER, pShooter, pLevel);
    }

    public FirecrackerEntity(Level pLevel, double pX, double pY, double pZ) {
        super(ModEntities.FIRECRACKER, pX, pY, pZ, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.FIRECRACKER;
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 1.0F, 1.0F);
            spawnFireworks(serverLevel, this.getX(), this.getY(), this.getZ());
            this.discard();
        }
    }

    private static void spawnFireworks(ServerLevel level, double x, double y, double z) {
        int type = RANDOM.nextInt(5);
        Vector3f color = FIREWORK_COLORS[RANDOM.nextInt(FIREWORK_COLORS.length)];
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
                Vector3f color2 = FIREWORK_COLORS[RANDOM.nextInt(FIREWORK_COLORS.length)];
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
}
