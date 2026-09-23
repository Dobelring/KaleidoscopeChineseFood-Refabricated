package com.bmt.kaleidoscope_chinesefood.compat.ponder.scenes;

import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.zurrtum.create.catnip.math.Pointing;
import com.zurrtum.create.client.ponder.api.scene.PositionUtil;
import com.zurrtum.create.client.ponder.api.scene.SceneBuilder;
import com.zurrtum.create.client.ponder.api.scene.SceneBuildingUtil;
import com.zurrtum.create.client.ponder.api.scene.Selection;
import com.zurrtum.create.client.ponder.api.scene.SelectionUtil;
import com.zurrtum.create.client.ponder.api.scene.VectorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 泡菜坛的 Ponder 讲解场景（开盖 → 放盐和生菜 → 关盖发酵 → 取成品）。
 * <p>
 * 逐行对应官方 1.1.12 的 {@code compat/ponder/scenes/PickleJarScenes}，只改了两处：
 * Create API 包名（{@code net.createmod.*} → {@code com.zurrtum.create.*}），
 * 以及物品取值方式（官方是 NeoForge 的 DeferredItem {@code ModItems.SALT.get()}，
 * Fabric 侧是普通静态字段 {@code ModItems.SALT}）。
 */
@Environment(EnvType.CLIENT)
public final class PickleJarScenes {
    private PickleJarScenes() {
    }

    public static void introduction(SceneBuilder scene, SceneBuildingUtil util) {
        VectorUtil vector = util.vector();
        SelectionUtil select = util.select();
        PositionUtil grid = util.grid();

        scene.title("pickle_jar", "ponder.kaleidoscope_chinesefood.pickle_jar.title");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();

        BlockPos jarPos = grid.at(2, 1, 2);
        Selection jarSel = select.position(jarPos);

        scene.idle(20);
        scene.world().showSection(jarSel, Direction.DOWN);
        scene.idle(30);

        scene.overlay().showText(60)
                .text("ponder.kaleidoscope_chinesefood.pickle_jar.step1")
                .pointAt(vector.blockSurface(jarPos, Direction.WEST))
                .placeNearTarget();
        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
                .rightClick().whileSneaking();
        scene.idle(7);
        scene.world().modifyBlock(jarPos, s -> s.setValue(PickleJarBlock.OPEN, true), false);
        scene.idle(55);
        scene.addKeyframe();
        scene.idle(20);

        scene.overlay().showText(80)
                .text("ponder.kaleidoscope_chinesefood.pickle_jar.step2")
                .pointAt(vector.blockSurface(jarPos, Direction.WEST))
                .placeNearTarget();
        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
                .rightClick().withItem(new ItemStack(ModItems.SALT));
        scene.idle(7);
        scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class,
                be -> be.inventory.setStackInSlot(0, new ItemStack(ModItems.SALT, 1)));
        scene.idle(35);

        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
                .rightClick().withItem(new ItemStack(com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.LETTUCE));
        scene.idle(7);
        scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> be.inventory.setStackInSlot(
                1, new ItemStack(com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.LETTUCE, 1)));
        scene.idle(35);

        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
                .rightClick().withItem(new ItemStack(ModItems.SALT));
        scene.idle(7);
        scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class,
                be -> be.inventory.setStackInSlot(2, new ItemStack(ModItems.SALT, 1)));
        scene.idle(30);
        scene.addKeyframe();
        scene.idle(20);

        scene.overlay().showText(60)
                .text("ponder.kaleidoscope_chinesefood.pickle_jar.step3")
                .pointAt(vector.blockSurface(jarPos, Direction.WEST))
                .placeNearTarget();
        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
                .rightClick().whileSneaking();
        scene.idle(7);
        scene.world().modifyBlock(jarPos, s -> s.setValue(PickleJarBlock.OPEN, false)
                .setValue(PickleJarBlock.FERMENTING, true)
                .setValue(PickleJarBlock.DONE, false), false);
        scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> {
            be.setMaxProgress(36);
            be.setProgress(0);
        });
        scene.idle(55);
        scene.addKeyframe();
        scene.idle(20);

        scene.overlay().showText(60)
                .text("ponder.kaleidoscope_chinesefood.pickle_jar.fermenting")
                .placeNearTarget();
        scene.overlay().showControls(vector.blockSurface(grid.at(2, 3, 2), Direction.UP), Pointing.DOWN, 55)
                .withItem(new ItemStack(Items.CLOCK));

        for (int i = 0; i < 4; i++) {
            int prog = (i + 1) * 9;
            scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> be.setProgress(prog));
            scene.idle(6);
        }

        scene.idle(30);
        scene.world().modifyBlock(jarPos, s -> s.setValue(PickleJarBlock.FERMENTING, false)
                .setValue(PickleJarBlock.DONE, true)
                .setValue(PickleJarBlock.OPEN, true), false);
        scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> {
            be.setProgress(0);
            be.inventory.setStackInSlot(0, ItemStack.EMPTY);
            be.inventory.setStackInSlot(1, ItemStack.EMPTY);
            be.inventory.setStackInSlot(2, ItemStack.EMPTY);
            be.inventory.setStackInSlot(0, new ItemStack(ModItems.CHINESE_SAUERKRAUT, 1));
        });

        scene.overlay().showText(60)
                .text("ponder.kaleidoscope_chinesefood.pickle_jar.step4")
                .pointAt(vector.blockSurface(jarPos, Direction.WEST))
                .placeNearTarget();
        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35).rightClick();
        scene.idle(60);
        scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class,
                be -> be.inventory.setStackInSlot(0, ItemStack.EMPTY));
        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
                .withItem(new ItemStack(ModItems.CHINESE_SAUERKRAUT));
        scene.idle(50);

        scene.world().hideSection(jarSel, Direction.DOWN);
        scene.idle(20);
    }
}
