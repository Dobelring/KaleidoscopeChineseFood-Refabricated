package com.bmt.kaleidoscope_chinesefood.compat.ponder.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.compat.ponder.scenes.PickleJarScenes;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class PickleJarPonderScenes {
    public PickleJarPonderScenes() {
    }

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(new ResourceLocation[]{KaleidoscopeChineseFood.id("pickle_jar")})
            .addStoryBoard("pickle_jar/introduction", PickleJarScenes::introduction);
    }
}
