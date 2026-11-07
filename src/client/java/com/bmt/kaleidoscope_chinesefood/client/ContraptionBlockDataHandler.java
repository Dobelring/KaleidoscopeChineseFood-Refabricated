package com.bmt.kaleidoscope_chinesefood.client;

import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionBlockDataPayload;
import com.bmt.kaleidoscope_chinesefood.compat.create.ContraptionBlocks;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import com.zurrtum.create.content.contraptions.Contraption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * 客户端把同步包写回 contraption 的方块表（对齐官方 ClientContraptionBlockDataHandler）。
 * create-fly 没有 resetClientContraption 钩子，BE 渲染缓存的刷新交给渲染器侧每帧读取。
 */
public final class ContraptionBlockDataHandler {
    private ContraptionBlockDataHandler() {
    }

    public static void handle(ContraptionBlockDataPayload payload) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Entity entity = level.getEntity(payload.entityId());
        if (!(entity instanceof AbstractContraptionEntity contraptionEntity)) {
            return;
        }
        Contraption contraption = contraptionEntity.getContraption();
        if (payload.state().isAir()) {
            contraption.getBlocks().remove(payload.localPos());
            contraption.getInteractors().remove(payload.localPos());
        } else {
            CompoundTag tag = payload.nbt().isEmpty() ? null : payload.nbt();
            contraption.getBlocks().put(payload.localPos(), new StructureTemplate.StructureBlockInfo(payload.localPos(), payload.state(), tag));
        }
        contraption.invalidateColliders();
        ContraptionBlocks.invalidateCachedColliders(contraption);
    }
}
