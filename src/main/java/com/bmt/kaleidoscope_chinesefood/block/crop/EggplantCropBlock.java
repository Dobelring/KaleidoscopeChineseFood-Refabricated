package com.bmt.kaleidoscope_chinesefood.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class EggplantCropBlock extends BaseCropBlock {
    public EggplantCropBlock(Properties properties, Supplier<Item> result, Supplier<Item> seed) {
        super(properties, result, seed);
    }

    /**
     * 26.1 特有缺陷：客户端 {@code BoneMealItem.useOn} 生长成功却返回 PASS（26.2 已改为客户端 SUCCESS），
     * 主手交互因此"未消费"，{@code Minecraft.startUseItem} 会继续尝试副手——副手的空手点击包到达
     * 服务端时，作物已被主手包催熟到满级，cookery 的满龄收获立即触发，表现为"骨粉一点又成熟又掉落"。
     *
     * 修复：作物仍可催熟时由方块自行执行生长并返回消费结果（双端），主手交互被消费后副手不再被尝试；
     * 已成熟时不拦截（isValidBonemealTarget=false），与 26.2 一致地走 super 的满龄收获分支。
     */
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() == Items.BONE_MEAL && this.isValidBonemealTarget(level, pos, state)) {
            if (!level.isClientSide()) {
                this.performBonemeal((ServerLevel) level, level.getRandom(), pos, state);
                level.levelEvent(1505, pos, 15);
            }
            stack.shrink(1);
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
