package com.bmt.kaleidoscope_chinesefood.compat.rrv;

import cc.cassian.rrv.api.ReliableRecipeViewerPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.freezer.FreezingServerRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.freezer.FreezingViewRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.freezer.RefrigeratingServerRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.freezer.RefrigeratingViewRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.mooncake_mold.MooncakeMoldServerRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.mooncake_mold.MooncakeMoldViewRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.pickle_jar.PickleJarServerRecipe;
import com.bmt.kaleidoscope_chinesefood.compat.rrv.pickle_jar.PickleJarViewRecipe;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import java.util.List;

/**
 * Reliable Recipe Viewer (RRV) 插件。
 * 通过 fabric.mod.json 的 "rrv" 入口点发现；覆盖腌菜罐 / 冷冻 / 冷藏 / 月饼模具四类展示。
 */
public class ModRRVPlugin implements ReliableRecipeViewerPlugin {
    @Override
    public void onIntegrationInitialize() {
        ItemView.addServerRecipeProvider(list -> {
            ServerRecipeManager.INSTANCE.getRecipesForType(ModRecipes.PICKLE_JAR_TYPE)
                    .forEach(r -> list.add(new PickleJarServerRecipe(r.getIngredients(), r.getOutput())));
            ServerRecipeManager.INSTANCE.getRecipesForType(ModRecipes.FREEZING_TYPE)
                    .forEach(r -> list.add(new FreezingServerRecipe(r.getIngredients().getFirst(), r.getOutput())));
            ServerRecipeManager.INSTANCE.getRecipesForType(ModRecipes.REFRIGERATING_TYPE)
                    .forEach(r -> list.add(new RefrigeratingServerRecipe(r.getIngredients().getFirst(), r.getOutput())));
            list.add(MooncakeMoldServerRecipe.virtual());
        });

        ItemView.addClientRecipeWrapper(PickleJarServerRecipe.TYPE, i -> List.of(new PickleJarViewRecipe(i)));
        ItemView.addClientRecipeWrapper(FreezingServerRecipe.TYPE, i -> List.of(new FreezingViewRecipe(i)));
        ItemView.addClientRecipeWrapper(RefrigeratingServerRecipe.TYPE, i -> List.of(new RefrigeratingViewRecipe(i)));
        ItemView.addClientRecipeWrapper(MooncakeMoldServerRecipe.TYPE, i -> List.of(new MooncakeMoldViewRecipe(i)));
    }
}
