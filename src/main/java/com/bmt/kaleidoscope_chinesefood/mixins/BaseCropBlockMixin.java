package com.bmt.kaleidoscope_chinesefood.mixins;

import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * cookery 作物（辣椒/生菜/稻米/番茄等所有继承 BaseCropBlock 的作物）
 * 26.1 骨粉催熟即掉落：直接调用 doDrop 逻辑，不在 break 后额外处理。
 * 26.1 客户端 {@code BoneMealItem.useOn} 生长成功却返回 PASS（26.2 已改 SUCCESS），
 * 主手交互未消费 → 游戏继续尝试副手 → 副手空点击包到达服务端时作物已被主手包
 * 催熟到满级 → cookery 的满龄收获立即触发，表现为"骨粉一点又成熟又掉落"。
 *
 * 处理方式与 EggplantCropBlock 一致：作物仍可催熟时由方块接管生长并双端消费交互；
 * 已满龄时不拦截，保留 cookery 原有的任意物品右键收获行为。
 * 我们自己的 EggplantCropBlock 重写并覆盖了 useItemOn，基类方法不会执行，无双重生长。
 */
@Mixin(BaseCropBlock.class)
public abstract class BaseCropBlockMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void kcf$bonemealConsume(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                     Player player, InteractionHand hand, BlockHitResult hitResult,
                                     CallbackInfoReturnable<InteractionResult> cir) {
        if (!stack.is(Items.BONE_MEAL)) {
            return;
        }

        CropBlock crop = (CropBlock) (Object) this;
        if (crop.isMaxAge(state)) {
            return;
        }

        if (!level.isClientSide()) {
            crop.performBonemeal((ServerLevel) level, level.getRandom(), pos, state);
            level.levelEvent(1505, pos, 15);
        }
        stack.shrink(1);
        cir.setReturnValue(level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER);
    }
}
