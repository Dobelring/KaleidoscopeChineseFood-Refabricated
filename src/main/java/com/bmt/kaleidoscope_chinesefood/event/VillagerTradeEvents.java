package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModVillager;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

/** 主厨（厨师生涯）一级交易：12 个茄子换 1 个绿宝石。 */
public class VillagerTradeEvents {
    public static void register() {
        TradeOfferHelper.registerVillagerOffers(ModVillager.CHEF, 1, factories ->
                factories.add((entity, random) -> new MerchantOffer(
                        new ItemCost(ModItems.EGGPLANT, 12),
                        new ItemStack(Items.EMERALD, 1),
                        16,
                        2,
                        0.05F
                ))
        );
    }
}
