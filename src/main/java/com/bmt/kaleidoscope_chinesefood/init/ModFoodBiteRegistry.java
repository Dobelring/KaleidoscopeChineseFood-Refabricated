package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.item.KCFBowlFoodBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteOneByTwoBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry.FoodData;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModFoodBiteRegistry {
   public static Identifier SICHUAN_BOILED_PORK_SLICES;
   public static Identifier SICHUAN_BOILED_FISH;
   public static Identifier YELLOW_CROAKER_SOUP;
   public static Identifier RED_RICE_ROLL;
   public static Identifier YELLOW_CROAKER_TOFU_SOUP;

   public static void init() {
      // 1.21.11 的 FoodBiteRegistry 改为实例注册，FoodData.create 需要成对 FoodProperties + Consumable
      FoodBiteRegistry registry = new FoodBiteRegistry();
      YELLOW_CROAKER_TOFU_SOUP = registry.registerFoodData(
         KaleidoscopeChineseFood.id("yellow_croaker_tofu_soup"),
         FoodData.create(3, ModFoods.YELLOW_CROAKER_TOFU_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_TOFU_SOUP_ITEM, ModFoods.YELLOW_CROAKER_TOFU_SOUP_BLOCK_C, ModFoods.YELLOW_CROAKER_TOFU_SOUP_ITEM_C).bowlAABB()
      );
      RED_RICE_ROLL = registry.registerFoodData(
         KaleidoscopeChineseFood.id("red_rice_roll"),
         FoodData.create(3, ModFoods.RED_RICE_ROLL_BLOCK, ModFoods.RED_RICE_ROLL_ITEM, ModFoods.RED_RICE_ROLL_BLOCK_C, ModFoods.RED_RICE_ROLL_ITEM_C)
      );
      SICHUAN_BOILED_PORK_SLICES = registry.registerFoodData(
         KaleidoscopeChineseFood.id("sichuan_boiled_pork_slices"),
         FoodData.create(3, ModFoods.SICHUAN_BOILED_PORK_SLICES_BLOCK, ModFoods.SICHUAN_BOILED_PORK_SLICES_ITEM, ModFoods.SICHUAN_BOILED_PORK_SLICES_BLOCK_C, ModFoods.SICHUAN_BOILED_PORK_SLICES_ITEM_C).bowlAABB()
      );
      SICHUAN_BOILED_FISH = registry.registerFoodData(
         KaleidoscopeChineseFood.id("sichuan_boiled_fish"),
         FoodData.create(4, ModFoods.SICHUAN_BOILED_FISH_BLOCK, ModFoods.SICHUAN_BOILED_FISH_ITEM, ModFoods.SICHUAN_BOILED_FISH_BLOCK_C, ModFoods.SICHUAN_BOILED_FISH_ITEM_C).bowlAABB()
      );
      YELLOW_CROAKER_SOUP = registry.registerFoodData(
         KaleidoscopeChineseFood.id("yellow_croaker_soup"),
         FoodData.create(3, ModFoods.YELLOW_CROAKER_SOUP_BLOCK, ModFoods.YELLOW_CROAKER_SOUP_ITEM, ModFoods.YELLOW_CROAKER_SOUP_BLOCK_C, ModFoods.YELLOW_CROAKER_SOUP_ITEM_C)
            .setLootItem(Items.FLOWER_POT)
            .soupPotAABB()
            .setAnimateTick(FoodBiteAnimateTicks.POT_SOUP_ANIMATE_TICK)
      );

      registerFoodBiteBlocksAndItems();
   }

   /**
    * cookery 1.3.0.9 把 FoodBite 方块/物品的创建移到了 CommonRegistry.registerFoodBiteBlocks，
    * 该迭代发生在 cookery 自身初始化时——那时我们的 FoodData 还没进 map，
    * 导致我们的食物方块与物品从未被注册（创造栏只能取到 AIR，还会引发后续崩溃）。
    * 这里自行创建并注册，行为与 cookery / 下界端口的 registerFoodBiteBlocks 完全一致。
    */
   private static void registerFoodBiteBlocksAndItems() {
      FoodBiteRegistry.FOOD_DATA_MAP.forEach((id, data) -> {
         if (!KaleidoscopeChineseFood.MODID.equals(id.getNamespace())) {
            return;
         }
         if (BuiltInRegistries.BLOCK.getOptional(id).isPresent()) {
            return; // 防御：避免与上游行为变化冲突导致重复注册
         }
         FoodBiteBlock block = createFoodBiteBlock(data, id);
         Registry.register(BuiltInRegistries.BLOCK, id, block);
         ItemLike first = data.getLootItems().getFirst();
         Consumable itemConsumable = data.itemConsumable();
         KCFBowlFoodBlockItem item = Registry.register(BuiltInRegistries.ITEM, id,
            new KCFBowlFoodBlockItem(block, data.itemFood(), itemConsumable, first, id.getPath()));
         FoodBiteRegistry.FOOD_ITEM_MAP.put(id, item);
      });
   }

   private static FoodBiteBlock createFoodBiteBlock(FoodData data, Identifier id) {
      Properties properties = Properties.of()
         .forceSolidOn()
         .instabreak()
         .mapColor(MapColor.WOOD)
         .sound(SoundType.WOOD)
         .pushReaction(PushReaction.DESTROY)
         .noOcclusion()
         .setId(ResourceKey.create(Registries.BLOCK, id));

      FoodBiteBlock block;
      if (data.blockType() == FoodBiteRegistry.BlockType.ONE_BY_TWO) {
         block = new FoodBiteOneByTwoBlock(properties, data.blockFood(), data.blockConsumable(), data.maxBites(), data.animateTick());
      } else {
         block = new FoodBiteBlock(properties, data.blockFood(), data.blockConsumable(), data.maxBites(), data.animateTick());
      }

      VoxelShape aabb = data.getAABB();
      if (aabb != null) {
         block.setAABB(aabb);
      }
      return block;
   }
}
