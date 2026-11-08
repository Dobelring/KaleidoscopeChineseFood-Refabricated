package com.bmt.kaleidoscope_chinesefood.compat.create;

import com.bmt.kaleidoscope_chinesefood.block.BowlStackBlock;
import com.bmt.kaleidoscope_chinesefood.block.FreezerBlock;
import com.bmt.kaleidoscope_chinesefood.block.KongmingLanternBlock;
import com.bmt.kaleidoscope_chinesefood.block.MooncakeBlock;
import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.block.SaltBlock;
import com.bmt.kaleidoscope_chinesefood.compat.create.behaviour.BowlStackMovingInteraction;
import com.bmt.kaleidoscope_chinesefood.compat.create.behaviour.FreezerMovingInteraction;
import com.bmt.kaleidoscope_chinesefood.compat.create.behaviour.KongmingLanternMovingInteraction;
import com.bmt.kaleidoscope_chinesefood.compat.create.behaviour.MooncakeMovingInteraction;
import com.bmt.kaleidoscope_chinesefood.compat.create.behaviour.PickleJarMovingInteraction;
import com.bmt.kaleidoscope_chinesefood.compat.create.behaviour.SaltMovingInteraction;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

/**
 * Create 联动入口（对齐官方 CreateCompat）：注册 6 个移动交互行为、
 * 机械方块数据同步包、虚拟 BE 的服务端 tick 驱动。
 * 由主类在 create 已加载时调用。
 */
public class CreateCompat {
    public static void register() {
        MovingInteractionBehaviour.REGISTRY.register(ModBlocks.FREEZER, new FreezerMovingInteraction());
        MovingInteractionBehaviour.REGISTRY.register(ModBlocks.PICKLE_JAR, new PickleJarMovingInteraction());
        MovingInteractionBehaviour.REGISTRY.register(ModBlocks.BOWL_STACK, new BowlStackMovingInteraction());
        MovingInteractionBehaviour.REGISTRY.register(ModBlocks.MOONCAKE_BLOCK, new MooncakeMovingInteraction());
        MovingInteractionBehaviour.REGISTRY.register(ModBlocks.SALT_BLOCK, new SaltMovingInteraction());
        MovingInteractionBehaviour.REGISTRY.register(ModBlocks.KONGMING_LANTERN, new KongmingLanternMovingInteraction());
        ServerTickEvents.END_SERVER_TICK.register(server -> ContraptionVirtualBlockEntities.tickAll(server));
    }

    private CreateCompat() {
    }
}
