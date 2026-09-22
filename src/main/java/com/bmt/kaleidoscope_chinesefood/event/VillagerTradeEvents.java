package com.bmt.kaleidoscope_chinesefood.event;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModVillager;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

/**
 * 村民交易追加。
 * 原 Forge 的 {@code VillagerTradesEvent} 改为 Fabric 的
 * {@code TradeOfferHelper.registerVillagerOffers(villagerProfession, level, list -> ...)}
 * （范式照抄 cookery 的 {@code init/ModTrades.java}）。
 */
public class VillagerTradeEvents {
    public VillagerTradeEvents() {
    }

    public static void register() {
        // 原 event.getTrades().get(1)：厨师村民 1 级交易表
        TradeOfferHelper.registerVillagerOffers(ModVillager.CHEF, 1, trades ->
            // 原 Forge BasicItemListing(12 个茄子 -> 1 个绿宝石, 16 次, 2 经验, 0.05F)
            // Fabric 没有 BasicItemListing，改用 ItemListing 的 lambda 直接构造 MerchantOffer
            trades.add((trader, random) -> new MerchantOffer(
                new ItemStack(ModItems.EGGPLANT, 12),
                new ItemStack(Items.EMERALD, 1),
                16,
                2,
                0.05F
            ))
        );
    }
}
