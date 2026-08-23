package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.item.BowlFoodBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteOneByTwoBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;

/**
 * 26.1: cookery's CommonRegistry builds food-bite blocks/items with Properties.setId derived from
 * PortHelper.createItemId(getPath()) (hard-coded kaleidoscope_cookery namespace) but registers under
 * the original key. Cross-mod entries therefore create a ghost component initializer and crash the
 * world load ("Missing element"). We mirror the Kaleidoscope Nether port and register the food-bite
 * blocks/items ourselves under our own namespace.
 */
public final class ModFoodBiteRegistry {
    public static final Map<Identifier, FoodBiteRegistry.FoodData> FOOD_DATA_MAP = new ConcurrentHashMap<>();
    private static final CopyOnWriteArrayList<Identifier> FOOD_DATA_ORDER = new CopyOnWriteArrayList<>();

    public static Identifier SICHUAN_BOILED_PORK_SLICES;
    public static Identifier SICHUAN_BOILED_FISH;
    public static Identifier YELLOW_CROAKER_SOUP;
    public static Identifier RED_RICE_ROLL;
    public static Identifier YELLOW_CROAKER_TOFU_SOUP;

    public static void init() {
        YELLOW_CROAKER_TOFU_SOUP = registerFoodData(KaleidoscopeChineseFood.id("yellow_croaker_tofu_soup"),
                FoodBiteRegistry.FoodData.create(3,
                        ModFoods.YELLOW_CROAKER_TOFU_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_TOFU_SOUP_ITEM,
                        ModConsumables.YELLOW_CROAKER_TOFU_SOUP_BLOCK, ModConsumables.YELLOW_CROAKER_TOFU_SOUP_ITEM).bowlAABB());
        RED_RICE_ROLL = registerFoodData(KaleidoscopeChineseFood.id("red_rice_roll"),
                FoodBiteRegistry.FoodData.create(3,
                        ModFoods.RED_RICE_ROLL_BLOCK, ModFoods.RED_RICE_ROLL_ITEM,
                        ModConsumables.RED_RICE_ROLL_BLOCK, ModConsumables.RED_RICE_ROLL_ITEM));
        SICHUAN_BOILED_PORK_SLICES = registerFoodData(KaleidoscopeChineseFood.id("sichuan_boiled_pork_slices"),
                FoodBiteRegistry.FoodData.create(3,
                        ModFoods.SICHUAN_BOILED_PORK_SLICES_BLOCK, ModFoods.SICHUAN_BOILED_PORK_SLICES_ITEM,
                        ModConsumables.SICHUAN_BOILED_PORK_SLICES_BLOCK, ModConsumables.SICHUAN_BOILED_PORK_SLICES_ITEM).bowlAABB());
        SICHUAN_BOILED_FISH = registerFoodData(KaleidoscopeChineseFood.id("sichuan_boiled_fish"),
                FoodBiteRegistry.FoodData.create(4,
                        ModFoods.SICHUAN_BOILED_FISH_BLOCK, ModFoods.SICHUAN_BOILED_FISH_ITEM,
                        ModConsumables.SICHUAN_BOILED_FISH_BLOCK, ModConsumables.SICHUAN_BOILED_FISH_ITEM).bowlAABB());
        YELLOW_CROAKER_SOUP = registerFoodData(KaleidoscopeChineseFood.id("yellow_croaker_soup"),
                FoodBiteRegistry.FoodData.create(3,
                        ModFoods.YELLOW_CROAKER_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_SOUP_ITEM,
                        ModConsumables.YELLOW_CROAKER_SOUP_BLOCK, ModConsumables.YELLOW_CROAKER_SOUP_ITEM)
                        .setLootItem(Items.FLOWER_POT)
                        .soupPotAABB()
                        .potSoupAnimateTick());
    }

    public static void registerFoodBiteBlocks() {
        ModFoodBiteRegistry.init();

        FOOD_DATA_MAP.forEach((id, data) -> {
            FoodBiteBlock biteBlock = getFoodBiteBlock(data, id.getPath());
            Registry.register(BuiltInRegistries.BLOCK, id, biteBlock);

            Block block = BuiltInRegistries.BLOCK.getValue(id);
            // 选取第一个掉落物作为 usingConvertsTo
            ItemLike first = data.getLootItems().getFirst();
            BowlFoodBlockItem item = new BowlFoodBlockItem(block, data.itemFood(), data.itemConsumable(), first, id.getPath());
            item.registerBlocks(Item.BY_BLOCK, item);
            Registry.register(BuiltInRegistries.ITEM, id, item);
        });
    }

    private static @NotNull FoodBiteBlock getFoodBiteBlock(FoodBiteRegistry.FoodData data, String name) {
        FoodBiteBlock biteBlock;
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion();

        if (data.blockType() == FoodBiteRegistry.BlockType.ONE_BY_TWO) {
            biteBlock = new FoodBiteOneByTwoBlock(
                    properties.setId(ResourceKey.create(Registries.BLOCK, KaleidoscopeChineseFood.id(name))),
                    data.blockFood(),
                    data.blockConsumable(),
                    data.maxBites(),
                    data.animateTick()
            );
        } else {
            biteBlock = new FoodBiteBlock(
                    properties.setId(ResourceKey.create(Registries.BLOCK, KaleidoscopeChineseFood.id(name))),
                    data.blockFood(),
                    data.blockConsumable(),
                    data.maxBites(),
                    data.animateTick()
            );
        }

        VoxelShape aabb = data.getAABB();
        if (aabb != null) {
            biteBlock.setAABB(aabb);
        }
        return biteBlock;
    }

    private static Identifier registerFoodData(Identifier id, FoodBiteRegistry.FoodData data) {
        FOOD_DATA_MAP.put(id, data);
        FOOD_DATA_ORDER.addIfAbsent(id);
        return id;
    }

    public static void forEach(BiConsumer<Identifier, FoodBiteRegistry.FoodData> consumer) {
        for (Identifier id : FOOD_DATA_ORDER) {
            FoodBiteRegistry.FoodData data = FOOD_DATA_MAP.get(id);
            if (data != null) {
                consumer.accept(id, data);
            }
        }
    }
}
