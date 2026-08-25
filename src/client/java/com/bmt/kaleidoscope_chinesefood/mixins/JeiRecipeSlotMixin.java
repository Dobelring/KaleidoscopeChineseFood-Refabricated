package com.bmt.kaleidoscope_chinesefood.mixins;

import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.gui.ingredients.RecipeSlot;
import mezz.jei.library.ingredients.SlotIngredient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

/**
 * JEI 29.x（MC 26.1 线）把"多候选材料"角标画在槽位右下角，
 * 而 MC 26.2 的 JEI 30 已改为右上角。这里覆盖私有绘制方法，
 * 把角标移到右上角贴住槽位上边缘，使两个版本观感一致。
 * <p>
 * 仅当 JEI 存在时该目标类才会加载；JEI 大版本更新导致签名变化时会显式报错。
 */
@Mixin(value = RecipeSlot.class, remap = false)
public abstract class JeiRecipeSlotMixin {

    @Shadow @Final private ImmutableRect2i rect;

    @Shadow public abstract Optional<TagKey<?>> getTagKey();

    @Shadow protected abstract boolean hasCandidates(SlotIngredient<?> displayed);

    /**
     * @author Dobelring
     * @reason 角标位置从右下角改为右上角（对齐 JEI 30 / MC 26.2 行为）
     */
    @Overwrite(remap = false)
    private <T> void drawCandidatesBadge(GuiGraphicsExtractor guiGraphics, SlotIngredient<T> displayed) {
        if (!this.hasCandidates(displayed)) {
            return;
        }
        Textures textures = Internal.getTextures();
        IDrawableStatic badgeIcon = this.getTagKey()
                .map(tagKey -> textures.getTagBadgeIcon())
                .orElseGet(textures::getListBadgeIcon);
        int badgeX = this.rect.getX() + this.rect.getWidth() - badgeIcon.getWidth() + 1;
        int badgeY = this.rect.getY() - 1;
        badgeIcon.draw(guiGraphics, badgeX, badgeY);
    }
}
