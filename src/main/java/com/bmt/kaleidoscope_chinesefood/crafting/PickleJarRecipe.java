package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PickleJarRecipe implements Recipe<PickleJarInput> {
   public static final int DEFAULT_FERMENT_TIME = 200;
   private final NonNullList<Ingredient> inputs;
   private final ItemStack output;
   private final int fermentTime;
   public static final MapCodec<PickleJarRecipe> CODEC = RecordCodecBuilder.mapCodec(
      inst -> inst.group(
            Ingredient.CODEC_NONEMPTY
               .listOf()
               .fieldOf("ingredients")
               .xmap(list -> NonNullList.of(Ingredient.EMPTY, (Ingredient[])list.toArray(new Ingredient[0])), List::copyOf)
               .forGetter(r -> r.inputs),
            ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
            Codec.INT.optionalFieldOf("ferment_time", 200).forGetter(r -> r.fermentTime)
         )
         .apply(inst, PickleJarRecipe::new)
   );

   public PickleJarRecipe(NonNullList<Ingredient> inputs, ItemStack output, int fermentTime) {
      this.inputs = inputs;
      this.output = output;
      this.fermentTime = fermentTime;
   }

   public int getFermentTime() {
      return this.fermentTime;
   }

   public ItemStack getOutput() {
      return this.output;
   }

   public boolean matches(@NotNull PickleJarInput input, @NotNull Level level) {
      List<ItemStack> stacks = new ArrayList<>();

      for (int i = 0; i < input.size(); i++) {
         ItemStack stack = input.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getCount() != 4) {
               return false;
            }

            stacks.add(stack);
         }
      }

      if (stacks.size() != this.inputs.size()) {
         return false;
      }

      // 1.2.0:贪心匹配改回溯,多输入交叉时更鲁棒;逐个走原版 Ingredient.test
      return this.matchFrom(0, stacks, new boolean[stacks.size()]);
   }

   private boolean matchFrom(int index, List<ItemStack> stacks, boolean[] used) {
      if (index >= this.inputs.size()) {
         return true;
      }

      Ingredient ingredient = this.inputs.get(index);
      for (int i = 0; i < stacks.size(); i++) {
         if (used[i] || !ingredient.test(stacks.get(i))) {
            continue;
         }

         used[i] = true;
         if (this.matchFrom(index + 1, stacks, used)) {
            return true;
         }

         used[i] = false;
      }

      return false;
   }

   @NotNull
   public ItemStack assemble(@NotNull PickleJarInput input, @NotNull Provider registries) {
      return this.output.copy();
   }

   public boolean canCraftInDimensions(int width, int height) {
      return true;
   }

   @NotNull
   public ItemStack getResultItem(@NotNull Provider registries) {
      return this.output;
   }

   @NotNull
   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer<?>)ModRecipes.PICKLE_JAR_SERIALIZER;
   }

   @NotNull
   public RecipeType<?> getType() {
      return (RecipeType<?>)ModRecipes.PICKLE_JAR_TYPE;
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.inputs;
   }

   public static class Serializer implements RecipeSerializer<PickleJarRecipe> {
      @NotNull
      public MapCodec<PickleJarRecipe> codec() {
         return PickleJarRecipe.CODEC;
      }

      @NotNull
      public StreamCodec<RegistryFriendlyByteBuf, PickleJarRecipe> streamCodec() {
         return new StreamCodec<RegistryFriendlyByteBuf, PickleJarRecipe>() {
            public PickleJarRecipe decode(RegistryFriendlyByteBuf buf) {
               int size = buf.readVarInt();
               NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);

               for (int i = 0; i < size; i++) {
                  inputs.set(i, (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
               }

               ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
               int time = buf.readVarInt();
               return new PickleJarRecipe(inputs, output, time);
            }

            public void encode(RegistryFriendlyByteBuf buf, PickleJarRecipe recipe) {
               buf.writeVarInt(recipe.inputs.size());

               for (Ingredient ing : recipe.inputs) {
                  Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
               }

               ItemStack.STREAM_CODEC.encode(buf, recipe.output);
               buf.writeVarInt(recipe.fermentTime);
            }
         };
      }
   }
}
