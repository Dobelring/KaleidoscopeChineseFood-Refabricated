package com.bmt.kaleidoscope_chinesefood.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * 竹筒蒸蛋（可放置菜品）。吃完返还竹竿。
 * <p>
 * 方块本身由 cookery 的 {@code StackableFoodBlock} 提供（最多 4 个一摞），本类只负责食物与容器语义。
 */
public class BambooSteamedEggBlockItem extends BlockItem implements IHasContainer {
    public BambooSteamedEggBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        return this.returnContainerToEntity(itemStack, level, entity);
    }

    @Override
    public void appendHoverText(
            @NotNull ItemStack stack,
            @NotNull TooltipContext context,
            @NotNull TooltipDisplay tooltipDisplay,
            @NotNull Consumer<Component> consumer,
            @NotNull TooltipFlag flag
    ) {
        super.appendHoverText(stack, context, tooltipDisplay, consumer, flag);
        consumer.accept(
                Component.translatable("item.kaleidoscope_chinesefood.bamboo_steamed_egg.tooltip")
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
        );
    }

    @Override
    public Item getContainerItem() {
        return Items.BAMBOO;
    }
}
