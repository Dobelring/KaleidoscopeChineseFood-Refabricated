package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * 客户端把同步包写回 contraption 的方块表（对齐官方 ClientContraptionBlockDataHandler）。
 * create-fly 没有 resetClientContraption 钩子，BE 渲染缓存的刷新交给渲染器侧每帧读取。
 */
public final class ClientContraptionBlockDataHandler {
    private ClientContraptionBlockDataHandler() {
    }

    @Environment(EnvType.CLIENT)
    public static void handle(ContraptionBlockDataPayload payload) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(payload.getEntityId());
        if (!(entity instanceof AbstractContraptionEntity contraptionEntity)) {
            return;
        }
        Contraption contraption = contraptionEntity.getContraption();
        if (payload.getState().isAir()) {
            contraption.getBlocks().remove(payload.getLocalPos());
            contraption.getInteractors().remove(payload.getLocalPos());
        } else {
            CompoundTag tag = payload.getNbt().isEmpty() ? null : payload.getNbt();
            contraption.getBlocks().put(payload.getLocalPos(), new StructureTemplate.StructureBlockInfo(payload.getLocalPos(), payload.getState(), tag));
        }
        contraption.invalidateColliders();
        ContraptionBlocks.invalidateCachedColliders(contraption);
    }
}
