package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class BaseProcessingRecipe implements Recipe<FreezerInput> {
   protected final Ingredient input;
   // 26.x 配方解析阶段 Item 组件尚未绑定，直接构造 ItemStack 会抛
   // "does not have components yet"；原版配方已改用 ItemStackTemplate 延迟物化。
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

   public boolean showNotification() {
      return false;
   }

   @NotNull
   public String group() {
      return "";
   }

   /** 物化产物（运行期调用，此时 Item 组件已绑定）。 */
   @NotNull
   public ItemStack getOutput() {
      return this.output.create();
   }

   @NotNull
   public ItemStack getResultItem(@NotNull Provider registries) {
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
      return PlacementInfo.NOT_PLACEABLE;
   }

   @NotNull
   public RecipeBookCategory recipeBookCategory() {
      return ProcessingBookCategories.get(this.bookCategoryName());
   }

   /** Subclasses provide the category name registered under the mod namespace. */
   protected abstract String bookCategoryName();

   protected static <T extends BaseProcessingRecipe> MapCodec<T> buildCodec(BaseProcessingRecipe.RecipeFactory<T> factory) {
      return RecordCodecBuilder.mapCodec(
         inst -> inst.group(
               Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
               ItemStackTemplate.CODEC.fieldOf("output").forGetter(r -> r.output),
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
            ItemStackTemplate result = (ItemStackTemplate)ItemStackTemplate.STREAM_CODEC.decode(buf);
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

   /**
    * 26.1: {@code RecipeSerializer} is now a final record of (MapCodec, StreamCodec);
    * custom serializer classes can no longer implement it.
    */
   protected static <T extends BaseProcessingRecipe> RecipeSerializer<T> makeSerializer(BaseProcessingRecipe.RecipeFactory<T> factory) {
      return new RecipeSerializer<>(buildCodec(factory), buildStreamCodec(factory));
   }

   /** Lazily registers and caches RecipeBookCategory entries under the mod namespace. */
   static final class ProcessingBookCategories {
      private static RecipeBookCategory FREEZING;
      private static RecipeBookCategory REFRIGERATING;

      static RecipeBookCategory get(String name) {
         return switch (name) {
            case "freezing" -> FREEZING != null ? FREEZING : (FREEZING = register("freezing"));
            case "refrigerating" -> REFRIGERATING != null ? REFRIGERATING : (REFRIGERATING = register("refrigerating"));
            default -> throw new IllegalArgumentException("Unknown processing book category: " + name);
         };
      }

      private static RecipeBookCategory register(String name) {
         Identifier id = Identifier.fromNamespaceAndPath(KaleidoscopeChineseFood.MODID, name);
         return (RecipeBookCategory)Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, id, new RecipeBookCategory());
      }
   }

   @FunctionalInterface
   protected interface RecipeFactory<T extends BaseProcessingRecipe> {
      T create(Ingredient var1, ItemStackTemplate var2, int var3);
   }
}
