package com.bmt.kaleidoscope_chinesefood.compat.ponder.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.compat.ponder.scenes.PickleJarScenes;
import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;

/** 把泡菜坛方块的讲解场景挂到 Create 的 Ponder 索引上。 */
@Environment(EnvType.CLIENT)
public final class PickleJarPonderScenes {
    private PickleJarPonderScenes() {
    }

    public static void register(PonderSceneRegistrationHelper<Identifier> helper) {
        helper.forComponents(new Identifier[]{KaleidoscopeChineseFood.id("pickle_jar")})
                .addStoryBoard("pickle_jar/introduction", PickleJarScenes::introduction);
    }
}
