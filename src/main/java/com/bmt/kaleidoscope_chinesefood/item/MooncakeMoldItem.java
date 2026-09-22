package com.bmt.kaleidoscope_chinesefood.item;

import com.bmt.kaleidoscope_chinesefood.init.ModItems;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MooncakeMoldItem extends Item {
    private static final int CRAFTING_TIME = 20;

    public MooncakeMoldItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack doughStack = player.getItemInHand(otherHand);
        if (this.isStuffedDoughFood(doughStack)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        } else {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            InteractionHand usedHand = player.getUsedItemHand();
            InteractionHand otherHand = usedHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack doughStack = player.getItemInHand(otherHand);
            if (this.isStuffedDoughFood(doughStack)) {
                doughStack.shrink(1);
                ItemStack rawMooncake = new ItemStack(ModItems.RAW_MOONCAKE);
                if (!player.getInventory().add(rawMooncake)) {
                    player.drop(rawMooncake, false);
                }

                level.playSound(null, player.blockPosition(), SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1.0F, 1.2F);
            }

            return stack;
        } else {
            return stack;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 20;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(
            Component.translatable("item.kaleidoscope_chinesefood.mooncake_mold.tooltip")
                .withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
        );
    }

    private boolean isStuffedDoughFood(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems.STUFFED_DOUGH_FOOD;
    }
}
