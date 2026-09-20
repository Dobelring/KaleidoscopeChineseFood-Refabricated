package com.bmt.kaleidoscope_chinesefood.client.renderer.entity;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.entity.YellowCroaker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.fish.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/**
 * 黄花鱼渲染器：直接复用原版鳕鱼的模型与图层，只换贴图。
 * <p>
 * 26.1.2 的渲染器走 render state（getTextureLocation/setupRotations 收的是状态而不是实体），
 * 摆动逻辑照抄原版 CodRenderer。
 */
public class YellowCroakerRenderer extends MobRenderer<YellowCroaker, LivingEntityRenderState, CodModel> {
    private static final Identifier TEXTURE = KaleidoscopeChineseFood.id("textures/entity/yellow_croaker.png");

    public YellowCroakerRenderer(Context context) {
        super(context, new CodModel(context.bakeLayer(ModelLayers.COD)), 0.3F);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(LivingEntityRenderState state, PoseStack poseStack, float bodyRot, float entityScale) {
        super.setupRotations(state, poseStack, bodyRot, entityScale);
        float bodyZRot = 4.3F * Mth.sin(0.6F * state.ageInTicks);
        poseStack.mulPose(Axis.YP.rotationDegrees(bodyZRot));
        if (!state.isInWater) {
            poseStack.translate(0.1F, 0.1F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }
}
