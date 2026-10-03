package com.bmt.kaleidoscope_chinesefood.compat.ponder.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.create.ponder.init.KitchenBlockPonderTag;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
@MethodsReturnNonnullByDefault
public class PickleJarPonderPlugin implements PonderPlugin {
    public PickleJarPonderPlugin() {
    }

    @Override
    public String getModId() {
        return "kaleidoscope_chinesefood";
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PickleJarPonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.addToTag(KitchenBlockPonderTag.KITCHEN_BLOCKS).add(KaleidoscopeChineseFood.id("pickle_jar"));
    }

    public static void init() {
        PonderIndex.addPlugin(new PickleJarPonderPlugin());
    }
}
