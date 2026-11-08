package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.bmt.kaleidoscope_chinesefood.block.BowlStackBlock;
import com.bmt.kaleidoscope_chinesefood.block.FreezerBlock;
import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.BowlStackBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.FreezerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.TagValueInput;
import org.jetbrains.annotations.Nullable;

/**
 * 机械上方块实体的虚拟驱动（对齐官方 compat.create.ContraptionVirtualBlockEntities）：
 * 每 20t 发现新 contraption，为其上的冰箱/泡菜坛/碗架建虚拟 BE 并手动 tick，
 * 每 4t 把 NBT 写回 contraption 持久化。
 * <p>
 * 26.3 的 BE 读取走 TagValueInput（原版 saveWithFullMetadata 的对称路径）。
 */
public final class ContraptionVirtualBlockEntities {
    private static final int SAVE_INTERVAL = 4;
    private static final int DISCOVER_INTERVAL = 20;
    private static final Map<UUID, Data> DATA = new HashMap<>();
    private static int discoverTimer;

    private ContraptionVirtualBlockEntities() {
    }

    public static boolean isTickedBlock(BlockState state) {
        return state.getBlock() instanceof FreezerBlock
            || state.getBlock() instanceof PickleJarBlock
            || state.getBlock() instanceof BowlStackBlock;
    }

    public static Data getOrCreate(AbstractContraptionEntity entity) {
        return DATA.computeIfAbsent(entity.getUUID(), id -> new Data(entity));
    }

    @Nullable
    public static BlockEntity getOrCreateBlockEntity(AbstractContraptionEntity entity, BlockPos localPos, StructureTemplate.StructureBlockInfo info) {
        Data data = getOrCreate(entity);
        BlockEntity existing = data.blockEntities.get(localPos);
        if (existing != null && existing.getBlockState() == info.state()) {
            return existing;
        }
        if (existing != null) {
            data.level.removeBlockEntity(localPos);
            data.blockEntities.remove(localPos);
        }
        BlockEntity be = create(info.state(), localPos, entity);
        if (be == null) {
            return null;
        }
        be.setLevel(data.level);
        be.setBlockState(info.state());
        if (info.nbt() != null && !info.nbt().isEmpty()) {
            be.loadWithComponents(TagValueInput.create(
                ProblemReporter.DISCARDING, data.level.registryAccess(), info.nbt().copy()));
        }
        data.blockEntities.put(localPos, be);
        data.level.putBlockEntity(localPos, be);
        return be;
    }

    @Nullable
    private static BlockEntity create(BlockState state, BlockPos pos, AbstractContraptionEntity entity) {
        if (state.getBlock() instanceof FreezerBlock) {
            return new ContraptionFreezerBlockEntity(pos, state, entity);
        }
        if (state.getBlock() instanceof PickleJarBlock) {
            return new PickleJarBlockEntity(pos, state);
        }
        if (state.getBlock() instanceof BowlStackBlock) {
            return new BowlStackBlockEntity(pos, state);
        }
        return null;
    }

    public static void tickAll(MinecraftServer server) {
        if (++discoverTimer >= DISCOVER_INTERVAL) {
            discoverTimer = 0;
            discover(server);
        }
        Iterator<Map.Entry<UUID, Data>> iterator = DATA.entrySet().iterator();
        while (iterator.hasNext()) {
            Data data = iterator.next().getValue();
            AbstractContraptionEntity entity = data.entity;
            if (entity.isRemoved() || !(entity.level() instanceof ServerLevel)) {
                iterator.remove();
                continue;
            }
            tickContraption(data);
        }
    }

    private static void discover(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (!(entity instanceof AbstractContraptionEntity contraptionEntity)) {
                    continue;
                }
                if (DATA.containsKey(contraptionEntity.getUUID())) {
                    continue;
                }
                Data data = getOrCreate(contraptionEntity);
                if (data.positions.isEmpty()) {
                    DATA.remove(contraptionEntity.getUUID());
                }
            }
        }
    }

    private static void tickContraption(Data data) {
        AbstractContraptionEntity entity = data.entity;
        for (BlockPos pos : data.positions) {
            StructureTemplate.StructureBlockInfo info = ContraptionBlocks.getInfo(entity, pos);
            if (info == null) {
                data.blockEntities.remove(pos);
                data.level.removeBlockEntity(pos);
                continue;
            }
            BlockEntity be = getOrCreateBlockEntity(entity, pos, info);
            if (be == null) {
                continue;
            }
            BlockState state = info.state();
            if (be instanceof PickleJarBlockEntity jar) {
                PickleJarBlockEntity.tick(data.level, pos, state, jar);
            } else if (be instanceof FreezerBlockEntity freezer) {
                FreezerBlockEntity.serverTick(data.level, pos, state, freezer);
            } else if (be instanceof BowlStackBlockEntity bowlStack) {
                BowlStackBlockEntity.tick(data.level, pos, state, bowlStack);
            }
            if (data.tickCount % SAVE_INTERVAL != 0) {
                continue;
            }
            ContraptionBlocks.updateNbt(entity, pos, ContraptionBlocks.save(be, data.level));
        }
        data.tickCount++;
    }

    public static void clear() {
        DATA.clear();
        discoverTimer = 0;
    }

    public static final class Data {
        public final AbstractContraptionEntity entity;
        public final ContraptionVirtualLevel level;
        public final List<BlockPos> positions;
        public final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();
        int tickCount;

        Data(AbstractContraptionEntity entity) {
            this.entity = entity;
            this.level = new ContraptionVirtualLevel(entity);
            ArrayList<BlockPos> list = new ArrayList<>();
            for (StructureTemplate.StructureBlockInfo info : entity.getContraption().getBlocks().values()) {
                if (!isTickedBlock(info.state())) {
                    continue;
                }
                list.add(info.pos());
            }
            this.positions = list;
        }
    }
}
