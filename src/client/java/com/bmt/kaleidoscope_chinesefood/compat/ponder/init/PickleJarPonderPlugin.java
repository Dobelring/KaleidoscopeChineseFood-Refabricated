package com.bmt.kaleidoscope_chinesefood.compat.ponder.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.compat.ponder.scenes.PickleJarScenes;
import com.zurrtum.create.client.ponder.api.registration.PonderPlugin;import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import com.zurrtum.create.client.ponder.foundation.PonderIndex;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;

/**
 * 向 Create 的 Ponder 注册本模组的讲解场景。
 * <p>
 * 官方用 {@code net.createmod.ponder.*}，Create Fly 是整体重打包的 Create 移植，
 * 对应 {@code com.zurrtum.create.client.ponder.*}，故此处只改包名。
 * 场景标题/文案的 lang 键由 Ponder 按 {@code <命名空间>.ponder.<场景 id>.header|text_N}
 * 自行推导，与 {@code title()}/{@code text()} 传入的字符串无关（那只是编辑模式下的默认值），
 * 所以 lang 里的 {@code kaleidoscope_chinesefood.ponder.pickle_jar.*} 直接生效。
 */
@Environment(EnvType.CLIENT)
public final class PickleJarPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return KaleidoscopeChineseFood.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<Identifier> helper) {
        PickleJarPonderScenes.register(helper);
        KaleidoscopeChineseFood.LOGGER.info("Registered the Pickle Jar ponder scene (kaleidoscope_chinesefood:pickle_jar)");
    }

    public static void init() {
        PonderIndex.addPlugin(new PickleJarPonderPlugin());
    }
}
