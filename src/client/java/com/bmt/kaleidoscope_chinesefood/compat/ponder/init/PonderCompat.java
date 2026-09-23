package com.bmt.kaleidoscope_chinesefood.compat.ponder.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

/**
 * 泡菜坛 Ponder 教学场景的加载守卫，对应官方 1.1.12 的 {@code compat/ponder/init/PonderCompat}。
 * <p>
 * 官方判的是独立的 {@code ponder} 库模组 id（NeoForge 上 Ponder 是 Create 拆出去的库）；
 * Fabric 侧由 Create Fly 提供 Create 本体，Ponder 已并入其中，模组 id 是 {@code create}，
 * 因此这里改为判 {@code create}（与厨房 26.x 的 PonderCompat 一致）。
 * <p>
 * 本类自身不引用任何 Create 类，未装 Create 时也能安全加载。
 * <p>
 * 时机：Fabric 的客户端入口点在 {@code Minecraft} 构造期间执行，而 Create 的
 * {@code PonderIndex.registerAll()} 是在主循环开始后（{@code Minecraft#run}）才调用，
 * 所以此处 addPlugin 一定早于注册阶段。
 */
@Environment(EnvType.CLIENT)
public final class PonderCompat {
    public static final String CREATE_MOD_ID = "create";
    public static boolean PONDER_LOADED = false;

    private PonderCompat() {
    }

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded(CREATE_MOD_ID)) {
            PONDER_LOADED = true;
            PickleJarPonderPlugin.init();
            KaleidoscopeChineseFood.LOGGER.info("Create is present, queued the Pickle Jar ponder plugin");
        }
    }
}
