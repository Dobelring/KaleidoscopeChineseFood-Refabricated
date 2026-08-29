package com.bmt.kaleidoscope_chinesefood.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class BaseProcessingRecipe implements Recipe<FreezerInput> {
   protected final Ingredient input;
   protected final ItemStack output;
   protected final int baseTime;
   public static final int TIME_MULTIPLIER = 5;
   public static final int DEFAULT_BASE_TIME = 100;

   public BaseProcessingRecipe(Ingredient input, ItemStack output, int baseTime) {
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
   public ItemStack assemble(@NotNull FreezerInput input, @NotNull Provider registries) {
      return this.output.copy();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   @NotNull
   public ItemStack getResultItem(@NotNull Provider registries) {
      return this.output;
   }

   public ItemStack getOutput() {
      return this.output;
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      NonNullList<Ingredient> ingredients = NonNullList.create();
      ingredients.add(this.input);
      return ingredients;
   }

   protected static <T extends BaseProcessingRecipe> MapCodec<T> buildCodec(BaseProcessingRecipe.RecipeFactory<T> factory) {
      return RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
               ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output),
               Codec.INT.optionalFieldOf("base_time", 100).forGetter(r -> r.baseTime)
            )
            .apply(inst, factory::create)
      );
   }

   protected static <T extends BaseProcessingRecipe> StreamCodec<RegistryFriendlyByteBuf, T> buildStreamCodec(
      final BaseProcessingRecipe.RecipeFactory<T> factory
   ) {
      return new StreamCodec<RegistryFriendlyByteBuf, T>() {
         public T decode(RegistryFriendlyByteBuf buf) {
            Ingredient ingredient = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStack result = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
            int time = buf.readVarInt();
            return factory.create(ingredient, result, time);
         }

         public void encode(RegistryFriendlyByteBuf buf, T recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
            buf.writeVarInt(recipe.baseTime);
         }
      };
   }

   @FunctionalInterface
   protected interface RecipeFactory<T extends BaseProcessingRecipe> {
      T create(Ingredient var1, ItemStack var2, int var3);
   }

   public abstract static class Serializer<T extends BaseProcessingRecipe> implements RecipeSerializer<T> {
      private final MapCodec<T> codec;
      private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

      protected Serializer(BaseProcessingRecipe.RecipeFactory<T> factory) {
         this.codec = BaseProcessingRecipe.buildCodec(factory);
         this.streamCodec = BaseProcessingRecipe.buildStreamCodec(factory);
      }

      @NotNull
      public MapCodec<T> codec() {
         return this.codec;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
         return this.streamCodec;
      }
   }
}
