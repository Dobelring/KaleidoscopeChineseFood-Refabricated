package com.bmt.kaleidoscope_chinesefood.compat.ponder.scenes;

import com.bmt.kaleidoscope_chinesefood.block.PickleJarBlock;
import com.bmt.kaleidoscope_chinesefood.block.entity.PickleJarBlockEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.PositionUtil;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Environment(EnvType.CLIENT)
public class PickleJarScenes {
    public PickleJarScenes() {
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
        scene.overlay()
            .showText(60)
            .text("ponder.kaleidoscope_chinesefood.pickle_jar.step1")
            .pointAt(vector.blockSurface(jarPos, Direction.WEST))
            .placeNearTarget();
        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35).rightClick().whileSneaking();
        scene.idle(7);
        scene.world().modifyBlock(jarPos, s -> s.setValue(PickleJarBlock.OPEN, true), false);
        scene.idle(55);
        scene.addKeyframe();
        scene.idle(20);
        scene.overlay()
            .showText(80)
            .text("ponder.kaleidoscope_chinesefood.pickle_jar.step2")
            .pointAt(vector.blockSurface(jarPos, Direction.WEST))
            .placeNearTarget();
        scene.overlay()
            .showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
            .rightClick()
            .withItem(new ItemStack(ModBlocks.SALT_BLOCK));
        scene.idle(7);
        scene.world()
            .modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> be.inventory.setStackInSlot(0, new ItemStack(ModBlocks.SALT_BLOCK, 1)));
        scene.idle(35);
        scene.overlay()
            .showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
            .rightClick()
            .withItem(new ItemStack(ModItems.LETTUCE));
        scene.idle(7);
        scene.world()
            .modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> be.inventory.setStackInSlot(1, new ItemStack(ModItems.LETTUCE, 1)));
        scene.idle(35);
        scene.overlay()
            .showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
            .rightClick()
            .withItem(new ItemStack(ModBlocks.SALT_BLOCK));
        scene.idle(7);
        scene.world()
            .modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> be.inventory.setStackInSlot(2, new ItemStack(ModBlocks.SALT_BLOCK, 1)));
        scene.idle(30);
        scene.addKeyframe();
        scene.idle(20);
        scene.overlay()
            .showText(60)
            .text("ponder.kaleidoscope_chinesefood.pickle_jar.step3")
            .pointAt(vector.blockSurface(jarPos, Direction.WEST))
            .placeNearTarget();
        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35).rightClick().whileSneaking();
        scene.idle(7);
        scene.world()
            .modifyBlock(
                jarPos,
                s -> s.setValue(PickleJarBlock.OPEN, false).setValue(PickleJarBlock.FERMENTING, true).setValue(PickleJarBlock.DONE, false),
                false
            );
        scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> {
            be.setMaxProgress(36);
            be.setProgress(0);
        });
        scene.idle(55);
        scene.addKeyframe();
        scene.idle(20);
        scene.overlay().showText(60).text("ponder.kaleidoscope_chinesefood.pickle_jar.fermenting").placeNearTarget();
        scene.overlay().showControls(vector.blockSurface(grid.at(2, 3, 2), Direction.UP), Pointing.DOWN, 55).withItem(new ItemStack(Items.CLOCK));

        for (int i = 0; i < 4; i++) {
            int prog = (i + 1) * 9;
            scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> be.setProgress(prog));
            scene.idle(6);
        }

        scene.idle(30);
        scene.world()
            .modifyBlock(
                jarPos,
                s -> s.setValue(PickleJarBlock.FERMENTING, false).setValue(PickleJarBlock.DONE, true).setValue(PickleJarBlock.OPEN, true),
                false
            );
        scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> {
            be.setProgress(0);
            be.inventory.setStackInSlot(0, ItemStack.EMPTY);
            be.inventory.setStackInSlot(1, ItemStack.EMPTY);
            be.inventory.setStackInSlot(2, ItemStack.EMPTY);
            be.inventory.setStackInSlot(0, new ItemStack(com.bmt.kaleidoscope_chinesefood.init.ModItems.CHINESE_SAUERKRAUT, 1));
        });
        scene.overlay()
            .showText(60)
            .text("ponder.kaleidoscope_chinesefood.pickle_jar.step4")
            .pointAt(vector.blockSurface(jarPos, Direction.WEST))
            .placeNearTarget();
        scene.overlay().showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35).rightClick();
        scene.idle(60);
        scene.world().modifyBlockEntity(jarPos, PickleJarBlockEntity.class, be -> be.inventory.setStackInSlot(0, ItemStack.EMPTY));
        scene.overlay()
            .showControls(vector.blockSurface(jarPos, Direction.UP), Pointing.DOWN, 35)
            .withItem(new ItemStack(com.bmt.kaleidoscope_chinesefood.init.ModItems.CHINESE_SAUERKRAUT));
        scene.idle(50);
        scene.world().hideSection(jarSel, Direction.DOWN);
        scene.idle(20);
    }
}
