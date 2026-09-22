package com.bmt.kaleidoscope_chinesefood.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class BambooSteamedEggBlockItem extends BlockItem implements IHasContainer {
    public BambooSteamedEggBlockItem(Block block, FoodProperties properties) {
        super(block, new Properties().food(properties));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        return this.returnContainerToEntity(itemStack, level, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(
            Component.translatable("item.kaleidoscope_chinesefood.bamboo_steamed_egg.tooltip")
                .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
        );
    }

    @Override
    public Item getContainerItem() {
        return Items.BAMBOO;
    }
}
