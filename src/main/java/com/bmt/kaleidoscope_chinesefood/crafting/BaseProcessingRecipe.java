package com.bmt.kaleidoscope_chinesefood.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * 26.1.2 Recipe 接口：assemble(T) 不再接收 HolderLookup；group()/showNotification()
 * 变为抽象方法必须实现；getResultItem 已从接口删除（JEI 展示走本类 getOutput()）。
 * 26.1 起 decode 期 item 组件未初始化，配方 result 必须用 ItemStackTemplate 存储，
 * JSON 格式 {"id","count"} 与 ItemStack 完全兼容。
 */
public abstract class BaseProcessingRecipe implements Recipe<FreezerInput> {
   protected final Ingredient input;
   protected final ItemStackTemplate output;
   protected final int baseTime;
   public static final int TIME_MULTIPLIER = 5;
   public static final int DEFAULT_BASE_TIME = 100;

   public BaseProcessingRecipe(Ingredient input, ItemStackTemplate output, int baseTime) {
      this.input = input;
      this.output = output;
      this.baseTime = baseTime;
   }

   public int calculateProcessingTime(int count) {
      return this.baseTime + Math.max(0, (count - 1) * 5);
   }

   public boolean matches(@NotNull FreezerInput input, @NotNull Level level) {
      return this.input.test(input.getItem(0));
   }

   @NotNull
   public ItemStack assemble(@NotNull FreezerInput input) {
      return this.output.create();
   }

   public boolean isSpecial() {
      return false;
   }

   @NotNull
   public String group() {
      return "";
   }

   public boolean showNotification() {
      return false;
   }

   /** 供 JEI/RRV 展示：模板即时物化成 ItemStack */
   public ItemStack getOutput() {
      return this.output.create();
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> ingredients = NonNullList.create();
      ingredients.add(this.input);
      return ingredients;
   }

   @NotNull
   public PlacementInfo placementInfo() {
      return PlacementInfo.create(this.input);
   }

   public static <T extends BaseProcessingRecipe> MapCodec<T> buildCodec(BaseProcessingRecipe.RecipeFactory<T> factory) {
      return RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
               ItemStackTemplate.CODEC.fieldOf("output").forGetter(r -> r.output),
               Codec.INT.optionalFieldOf("base_time", 100).forGetter(r -> r.baseTime)
            )
            .apply(inst, factory::create)
      );
   }

   public static <T extends BaseProcessingRecipe> StreamCodec<RegistryFriendlyByteBuf, T> buildStreamCodec(
      final BaseProcessingRecipe.RecipeFactory<T> factory
   ) {
      return new StreamCodec<RegistryFriendlyByteBuf, T>() {
         public T decode(RegistryFriendlyByteBuf buf) {
            Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(buf);
            int time = buf.readVarInt();
            return factory.create(ingredient, result, time);
         }

         public void encode(RegistryFriendlyByteBuf buf, T recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.output);
            buf.writeVarInt(recipe.baseTime);
         }
      };
   }

   @FunctionalInterface
   public interface RecipeFactory<T extends BaseProcessingRecipe> {
      T create(Ingredient input, ItemStackTemplate output, int baseTime);
   }
}
