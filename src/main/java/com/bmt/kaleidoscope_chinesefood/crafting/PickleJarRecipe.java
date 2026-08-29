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
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PickleJarRecipe implements Recipe<PickleJarInput> {
   public static final int DEFAULT_FERMENT_TIME = 200;
   private final NonNullList<Ingredient> inputs;
   private final ItemStack output;
   private final int fermentTime;
   public static final Ingredient EMPTY_SLOT = Ingredient.of(net.minecraft.world.item.Items.AIR);
   public static final MapCodec<PickleJarRecipe> CODEC = RecordCodecBuilder.mapCodec(
      inst -> inst.group(
            Ingredient.CODEC
               .listOf()
               .fieldOf("ingredients")
               .xmap(list -> NonNullList.of(EMPTY_SLOT, (Ingredient[])list.toArray(new Ingredient[0])), List::copyOf)
               .forGetter(r -> r.inputs),
            ItemStack.CODEC.fieldOf("result").forGetter(r -> r.output),
            Codec.INT.optionalFieldOf("fermentTime", 200).forGetter(r -> r.fermentTime)
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
      List<ItemStack> containerItems = new ArrayList<>();

      for (int i = 0; i < 4; i++) {
         ItemStack stack = input.getItem(i);
         if (!stack.isEmpty()) {
            if (stack.getCount() != 4) {
               return false;
            }

            containerItems.add(stack.copy());
         }
      }

      if (containerItems.size() != this.inputs.size()) {
         return false;
      } else {
         for (Ingredient ingredient : this.inputs) {
            boolean found = false;

            for (int ix = 0; ix < containerItems.size(); ix++) {
               if (ingredient.test(containerItems.get(ix))) {
                  containerItems.remove(ix);
                  found = true;
                  break;
               }
            }

            if (!found) {
               return false;
            }
         }

         return true;
      }
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
   public RecipeSerializer<PickleJarRecipe> getSerializer() {
      return ModRecipes.PICKLE_JAR_SERIALIZER;
   }

   @NotNull
   public RecipeType<PickleJarRecipe> getType() {
      return ModRecipes.PICKLE_JAR_TYPE;
   }

   @NotNull
      
   public PlacementInfo placementInfo() {
      return PlacementInfo.NOT_PLACEABLE;
   }

   public RecipeBookCategory recipeBookCategory() {
      return ModRecipes.PICKLE_JAR_CATEGORY;
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
               NonNullList<Ingredient> inputs = NonNullList.withSize(size, EMPTY_SLOT);

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
