package com.bmt.kaleidoscope_chinesefood.compat.rrv;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.client.recipe.ClientRecipeManager;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.freezer.FreezingViewRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.freezer.RefrigeratingViewRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.mooncake_mold.MooncakeMoldViewRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.pickle_jar.PickleJarViewRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import java.util.List;

/**
 * RRV 8.10.4（26.1.2）：客户端侧经由 "rrv_client" 入口点。配方由 ModRRVCommonPlugin
 * 的 synchronizeRecipeType 同步，这里从 ClientRecipeManager 读取完整配方并包装成视图；
 * 月饼模具是纯代码交互（非配方系统），作为静态虚拟条目直接加入。
 */
public class ModRRVPlugin implements ReliableRecipeViewerClientPlugin {
    @Override
    public void onIntegrationInitialize() {
        ItemView.addClientRecipeProvider(recipeList -> {
            ClientRecipeManager.INSTANCE
                    .<com.bmt.kaleidoscope_chinesefood.crafting.PickleJarInput, com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe>getRecipesForType(ModRecipes.PICKLE_JAR_TYPE)
                    .forEach(holder -> recipeList.add(new PickleJarViewRecipe(holder.id().identifier(), holder.value())));
            ClientRecipeManager.INSTANCE
                    .<com.bmt.kaleidoscope_chinesefood.crafting.FreezerInput, com.bmt.kaleidoscope_chinesefood.crafting.FreezingRecipe>getRecipesForType(ModRecipes.FREEZING_TYPE)
                    .forEach(holder -> recipeList.add(new FreezingViewRecipe(holder.id().identifier(), holder.value())));
            ClientRecipeManager.INSTANCE
                    .<com.bmt.kaleidoscope_chinesefood.crafting.FreezerInput, com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingRecipe>getRecipesForType(ModRecipes.REFRIGERATING_TYPE)
                    .forEach(holder -> recipeList.add(new RefrigeratingViewRecipe(holder.id().identifier(), holder.value())));
            recipeList.add(MooncakeMoldViewRecipe.virtual());
        });
    }
}
