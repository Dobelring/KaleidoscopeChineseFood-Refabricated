package com.bmt.kaleidoscope_chinesefood.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * 配置界面入口。
 * <p>
 * 原 Forge 通过 {@code ModContainer.registerExtensionPoint(ConfigScreenFactory.class, ...)}
 * 把界面挂到模组列表的 Config 按钮上。Fabric 1.20.1 没有这个扩展点：
 * Forge Config API Port 8.x 只提供 {@code fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry}
 * （无客户端界面注册 API），本工程也没有依赖 ModMenu / Cloth Config，
 * 因此 {@link #register()} 目前只是保留注册入口，不做任何挂载。
 * <p>
 * TODO(fabric): 引入 ModMenu（{@code modCompileOnly com.terraformersmc:modmenu}）并在
 * fabric.mod.json 增加 {@code modmenu} 入口点后，应在 register() 里注册
 * {@code ModMenuApi#getModConfigScreenFactory}，返回 {@code parent -> new ModConfigScreen(parent)}。
 */
@Environment(EnvType.CLIENT)
public class ModConfigScreenHandler {
    public ModConfigScreenHandler() {
    }

    /**
     * 仅客户端调用（由客户端入口 {@code KaleidoscopeChineseFoodClient#onInitializeClient} 调用）。
     */
    public static void register() {
        // 见类注释：Fabric 侧暂无可用的配置界面扩展点。
    }

    /**
     * 供其他入口（命令 / 兼容模组）直接打开配置界面，保留原界面可用性。
     */
    public static void open(Screen parentScreen) {
        Minecraft.getInstance().setScreen(new ModConfigScreen(parentScreen));
    }
}
