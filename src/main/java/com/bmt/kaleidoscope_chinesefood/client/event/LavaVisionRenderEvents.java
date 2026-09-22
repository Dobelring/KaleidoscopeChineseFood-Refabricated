package com.bmt.kaleidoscope_chinesefood.client.event;

import com.bmt.kaleidoscope_chinesefood.init.ModEffects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;

/**
 * 岩浆视野：装备 lava_swim 效果并在岩浆中时，把雾染成橙红并拉远可见距离。
 * <p>
 * 原 Forge 用 {@code ViewportEvent.ComputeFogColor} / {@code RenderFog}（岩浆判定走
 * {@code FluidType}/{@code ForgeMod}）。Fabric 没有 FluidType，改用原版
 * {@code Camera#getFluidInCamera() == FogType.LAVA} 判定，行为等价。
 * <p>
 * TODO(fabric): Fabric API 1.20.1 没有雾颜色 / 雾距离事件，这两个方法目前没有任何
 * 事件会调用它们；要让行为真正生效，需要额外写一个 mixin 注入
 * {@code net.minecraft.client.renderer.FogRenderer#setupColor} 与
 * {@code #setupFog}，并分别转发到下面的 onComputeFogColor / onRenderFog。
 * 该 mixin 不在本次客户端包转换的任务范围内，故仅保留等价的逻辑实现并标记。
 */
@Environment(EnvType.CLIENT)
public class LavaVisionRenderEvents {
    public LavaVisionRenderEvents() {
    }

    // 原判定条件：玩家拥有 lava_swim 效果，且相机处于岩浆中
    private static boolean shouldApplyLavaVision(Camera camera) {
        return camera.getEntity() instanceof Player player
            && player.hasEffect(ModEffects.LAVA_SWIM)
            && camera.getFluidInCamera() == FogType.LAVA;
    }

    // 对应 Forge 的 ViewportEvent.ComputeFogColor（event.setRed/setGreen/setBlue）
    public static void onComputeFogColor(Camera camera) {
        if (shouldApplyLavaVision(camera)) {
            RenderSystem.setShaderFogColor(0.9F, 0.6F, 0.3F);
        }
    }

    // 对应 Forge 的 ViewportEvent.RenderFog（setCanceled + setNearPlaneDistance/setFarPlaneDistance）
    public static void onRenderFog(Camera camera) {
        if (shouldApplyLavaVision(camera)) {
            float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
            RenderSystem.setShaderFogStart(0.1F);
            RenderSystem.setShaderFogEnd(Math.min(renderDistance * 16.0F, 48.0F));
        }
    }
}
