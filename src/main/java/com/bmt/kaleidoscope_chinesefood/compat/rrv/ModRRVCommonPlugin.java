package com.bmt.kaleidoscope_chinesefood.compat.rrv;

import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;

/**
 * RRV 8.10.4（26.1.2）：服务端侧经由 "rrv" 入口点，用 synchronizeRecipeType
 * 将自定义配方序列化器整类型同步到客户端（取代 1.21.11 的自建 ServerRecipe 通道）。
 */
public class ModRRVCommonPlugin implements cc.cassian.rrv.api.ReliableRecipeViewerPlugin {
    @Override
    public void onIntegrationInitialize() {
        ServerRecipeManager.INSTANCE.synchronizeRecipeType(ModRecipes.PICKLE_JAR_SERIALIZER, ModRecipes.PICKLE_JAR_TYPE);
        ServerRecipeManager.INSTANCE.synchronizeRecipeType(ModRecipes.FREEZING_SERIALIZER, ModRecipes.FREEZING_TYPE);
        ServerRecipeManager.INSTANCE.synchronizeRecipeType(ModRecipes.REFRIGERATING_SERIALIZER, ModRecipes.REFRIGERATING_TYPE);
    }
}
