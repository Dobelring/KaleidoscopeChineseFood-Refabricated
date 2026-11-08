package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.bmt.kaleidoscope_chinesefood.mixins.accessor.ContraptionAccessor;
import com.bmt.kaleidoscope_chinesefood.mixins.accessor.TranslatingContraptionAccessor;
import com.bmt.kaleidoscope_chinesefood.network.ModNetwork;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import com.zurrtum.create.content.contraptions.Contraption;
import com.zurrtum.create.content.contraptions.TranslatingContraption;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Create 机械上「方块 + NBT」的读写工具（对齐官方 compat.create.ContraptionBlocks）。
 * 官方经由 kaleidoscope_contraption 的 accessor 访问 Create 内部字段；
 * Fabric 侧 create-fly 的字段本就存在，改用本模组自建 accessor。
 */
public final class ContraptionBlocks {
    private ContraptionBlocks() {
    }

    public static boolean isKaleidoscopeContraptionLoaded() {
        return net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("kaleidoscope_contraption");
    }

    @Nullable
    public static StructureTemplate.StructureBlockInfo getInfo(AbstractContraptionEntity entity, BlockPos localPos) {
        return entity.getContraption().getBlocks().get(localPos);
    }

    public static boolean is(AbstractContraptionEntity entity, BlockPos localPos, Block block) {
        StructureTemplate.StructureBlockInfo info = getInfo(entity, localPos);
        return info != null && info.state().getBlock() == block;
    }

    public static CompoundTag save(BlockEntity be, Level level) {
        CompoundTag tag = be.saveWithFullMetadata(level.registryAccess());
        tag.remove("x");
        tag.remove("y");
        tag.remove("z");
        return tag;
    }

    public static void update(AbstractContraptionEntity entity, BlockPos localPos, BlockState state, @Nullable CompoundTag nbt) {
        Contraption contraption = entity.getContraption();
        contraption.getBlocks().put(localPos, new StructureTemplate.StructureBlockInfo(localPos, state, nbt));
        writeUpdateTag(contraption, localPos, nbt);
        contraption.invalidateColliders();
        invalidateCachedColliders(contraption);
        sync(entity, localPos, state, nbt);
    }

    public static void updateNbt(AbstractContraptionEntity entity, BlockPos localPos, CompoundTag nbt) {
        StructureTemplate.StructureBlockInfo info = getInfo(entity, localPos);
        if (info == null) {
            return;
        }
        Contraption contraption = entity.getContraption();
        contraption.getBlocks().put(localPos, new StructureTemplate.StructureBlockInfo(localPos, info.state(), nbt));
        writeUpdateTag(contraption, localPos, nbt);
    }

    public static void remove(AbstractContraptionEntity entity, BlockPos localPos) {
        Contraption contraption = entity.getContraption();
        contraption.getBlocks().remove(localPos);
        contraption.getInteractors().remove(localPos);
        writeUpdateTag(contraption, localPos, null);
        contraption.invalidateColliders();
        invalidateCachedColliders(contraption);
        sync(entity, localPos, Blocks.AIR.defaultBlockState(), null);
    }

    public static void invalidateCachedColliders(Contraption contraption) {
        if (contraption instanceof TranslatingContraption translating) {
            TranslatingContraptionAccessor accessor = (TranslatingContraptionAccessor) translating;
            accessor.kaleidoscope_chinesefood$setCachedColliders(null);
            accessor.kaleidoscope_chinesefood$setCachedColliderDirection(null);
        }
    }

    private static void writeUpdateTag(Contraption contraption, BlockPos localPos, @Nullable CompoundTag nbt) {
        Map<BlockPos, CompoundTag> updateTags = ((ContraptionAccessor) contraption).kaleidoscope_chinesefood$getUpdateTags();
        if (updateTags == null) {
            return;
        }
        if (nbt == null) {
            updateTags.remove(localPos);
        } else {
            updateTags.put(localPos, nbt.copy());
        }
    }

    private static void sync(AbstractContraptionEntity entity, BlockPos localPos, BlockState state, @Nullable CompoundTag nbt) {
        if (entity.level().isClientSide() || !(entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        ContraptionBlockDataPayload payload = new ContraptionBlockDataPayload(entity.getId(), localPos, state, nbt == null ? new CompoundTag() : nbt);
        // 原 Forge 用 PacketDistributor.TRACKING_ENTITY，Fabric 侧遍历玩家按可见距离过滤
        for (ServerPlayer player : serverLevel.getServer().getPlayerList().getPlayers()) {
            if (player.level() == entity.level() && player.distanceToSqr(entity) < 256.0 * 256.0) {
                ModNetwork.sendToPlayer(payload, player);
            }
        }
    }

    public static void playSound(AbstractContraptionEntity entity, BlockPos localPos, SoundEvent sound, SoundSource source, float volume, float pitch) {
        Vec3 soundPos = entity.toGlobalVector(Vec3.atLowerCornerOf(localPos), 1.0F);
        entity.level().playSound(null, soundPos.x, soundPos.y, soundPos.z, sound, source, volume, pitch);
    }
}
