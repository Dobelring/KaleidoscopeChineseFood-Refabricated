package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteOneByTwoBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.PlateBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeacupItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * cookery 先于本模组初始化时的兜底注册。
 * <p>
 * cf 的菜品/茶/拼盘数据只登记在三张数据表（{@code FOOD_DATA_MAP} / {@code TEACUP_DATA_MAP} /
 * {@code PLATE_DATA_MAP}）里，方块与物品由 cookery 的 {@code CommonRegistry.init()} 单次遍历
 * 代注册。开发环境（runServer）下 Fabric Loader 会随机打乱 mod 初始化顺序（生产环境按 mod id
 * 字母序，chinesefood 恒在 cookery 之前）：cookery 先行时它的遍历已经跑完，cf 的条目永远不会
 * 被代注册——表现为 22 个配方 + 8 个战利品表加载失败，茶/拼盘方块静默缺失。
 * <p>
 * 判定"cookery 对某张表的填数据 + 遍历代注册是否已完成"的可靠判据，是<b>表内任一 cookery 键的
 * 方块已注册</b>（方块只在 cookery 的遍历里创建；不能只看"表里有 cookery 键"，因为
 * {@code TeacupRegistry} 在类加载时就会静态引导自家的 8 款茶进表，详见
 * {@link #cookeryAlreadyConsumed}）。判"已完成"时按 cookery 同款行为为本模组命名空间的条目
 * 补注册（带防重守卫，任何情况下恰好注册一次）；判"未完成"时什么都不做，由 cookery 稍后
 * 代注册——cf 先行（生产环境）与 cookery 先行（开发环境洗牌）两条路径都不重不漏。
 */
public final class CookerySelfRegistration {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaleidoscopeChineseFood.MODID);

    private CookerySelfRegistration() {
    }

    public static void registerBlocksIfCookeryAlreadyInitialized() {
        boolean registered = false;
        if (cookeryAlreadyConsumed(FoodBiteRegistry.FOOD_DATA_MAP, "FOOD")) {
            FoodBiteRegistry.FOOD_DATA_MAP.forEach((id, data) -> {
                if (!KaleidoscopeChineseFood.MODID.equals(id.getNamespace())
                        || BuiltInRegistries.BLOCK.getOptional(id).isPresent()) {
                    return;
                }
                FoodBiteBlock block;
                if (data.blockType() == FoodBiteRegistry.BlockType.ONE_BY_TWO) {
                    block = new FoodBiteOneByTwoBlock(data.blockFood(), data.maxBites(), data.animateTick());
                } else {
                    block = new FoodBiteBlock(data.blockFood(), data.maxBites(), data.animateTick());
                }
                VoxelShape aabb = data.getAABB();
                if (aabb != null) {
                    block.setAABB(aabb);
                }
                Registry.register(BuiltInRegistries.BLOCK, id, block);
                ItemLike first = data.getLootItems().getFirst();
                Registry.register(BuiltInRegistries.ITEM, id, new BowlFoodBlockItem(block, data.itemFood(), first));
            });
            registered = true;
        }
        if (cookeryAlreadyConsumed(TeacupRegistry.TEACUP_DATA_MAP, "TEACUP")) {
            TeacupRegistry.TEACUP_DATA_MAP.forEach((id, data) -> {
                if (!KaleidoscopeChineseFood.MODID.equals(id.getNamespace())
                        || BuiltInRegistries.BLOCK.getOptional(id).isPresent()) {
                    return;
                }
                TeacupBlock block = new TeacupBlock(data.getMaxCount(), data.getAnimateTick());
                VoxelShape aabb = data.getAABB();
                if (aabb != null) {
                    block.setAABB(aabb);
                }
                Registry.register(BuiltInRegistries.BLOCK, id, block);
                Registry.register(BuiltInRegistries.ITEM, id, new TeacupItem(block, data.getEffects()));
            });
            registered = true;
        }
        if (cookeryAlreadyConsumed(PlateRegistry.PLATE_DATA_MAP, "PLATE")) {
            PlateRegistry.PLATE_DATA_MAP.forEach((id, data) -> {
                if (!KaleidoscopeChineseFood.MODID.equals(id.getNamespace())
                        || BuiltInRegistries.BLOCK.getOptional(id).isPresent()) {
                    return;
                }
                PlateBlock block = new PlateBlock(data.getMaxCount(), data.getServingItems());
                VoxelShape aabb = data.getAABB();
                if (aabb != null) {
                    block.setAABB(aabb);
                }
                Registry.register(BuiltInRegistries.BLOCK, id, block);
                Registry.register(BuiltInRegistries.ITEM, id, new PlateBlockItem(block, id.getPath()));
            });
            registered = true;
        }
        if (registered) {
            LOGGER.info("[Kaleidoscope ChineseFood] cookery initialized first; self-registered cf dish/tea/plate blocks as fallback");
        }
    }

    /**
     * 判定 cookery 对某张表的"填数据 + 遍历代注册"是否已经完成。
     * <p>
     * 不能只看表里有没有 cookery 命名空间的键：cookery 的 {@code TeacupRegistry} 在类加载时
     * （{@code static { bootstrap(); }}）就会把 8 款自家的茶放进 {@code TEACUP_DATA_MAP}，而
     * cf 的 {@code ModTea.init()} 一碰这个表就会触发该类加载——所以"有 cookery 键"在 cf 先行
     * 的正常顺序下也恒成立。可靠的判据是：<b>表内任一 cookery 键的方块已经注册</b>。方块只在
     * cookery 的遍历里创建，于是：cf 先行时（cookery 未跑）→ 表里要么没有 cookery 键，要么
     * 有键而无方块，均判"未完成"，交由 cookery 稍后代注册；cookery 先行时 → 键与方块俱在，
     * 判"已完成"，cf 按同款行为补注册自己的条目（带防重守卫，恰好注册一次）。
     */
    private static boolean cookeryAlreadyConsumed(Map<ResourceLocation, ?> map, String label) {
        ResourceLocation anyCookeryKey = map.keySet().stream()
                .filter(id -> "kaleidoscope_cookery".equals(id.getNamespace()))
                .findFirst()
                .orElse(null);
        if (anyCookeryKey == null) {
            LOGGER.debug("[Kaleidoscope ChineseFood] self-reg check {}: no cookery key, cookery pending", label);
            return false;
        }
        boolean done = BuiltInRegistries.BLOCK.getOptional(anyCookeryKey).isPresent();
        LOGGER.debug("[Kaleidoscope ChineseFood] self-reg check {}: probe={}, blockRegistered={}", label, anyCookeryKey, done);
        return done;
    }
}
