package com.bmt.kaleidoscope_chinesefood.item;

import com.bmt.kaleidoscope_chinesefood.block.MooncakeBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MooncakeItem extends BlockItem {
    private static final Random RANDOM = new Random();

    public MooncakeItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockState clickedState = level.getBlockState(context.getClickedPos());
        if (clickedState.getBlock() instanceof MooncakeBlock) {
            int currentStack = (Integer)clickedState.getValue(MooncakeBlock.STACK_COUNT);
            if (currentStack >= 4) {
                return InteractionResult.FAIL;
            } else {
                if (!level.isClientSide) {
                    level.setBlock(context.getClickedPos(), (BlockState)clickedState.setValue(MooncakeBlock.STACK_COUNT, currentStack + 1), 3);
                    if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
                        context.getItemInHand().shrink(1);
                    }
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            return context.getPlayer() != null && context.getPlayer().isCrouching() ? super.place(context) : InteractionResult.PASS;
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && entity instanceof Player player) {
            long dayTime = level.getDayTime() % 24000L;
            if (dayTime >= 0L && dayTime < 12000L) {
                this.applyRandomPositiveEffect(player);
            } else {
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 0));
            }
        }

        return result;
    }

    private void applyRandomPositiveEffect(Player player) {
        List<MobEffect> positiveEffects = new ArrayList<>();

        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            if (effect.isBeneficial()) {
                positiveEffects.add(effect);
            }
        }

        if (!positiveEffects.isEmpty()) {
            MobEffect randomEffect = positiveEffects.get(RANDOM.nextInt(positiveEffects.size()));
            int duration = 300 + RANDOM.nextInt(201);
            player.addEffect(new MobEffectInstance(randomEffect, duration, 0));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(
            Component.translatable("item.kaleidoscope_chinesefood.mooncake.tooltip").withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})
        );
    }
}
