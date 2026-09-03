package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PickleJarRecipe implements Recipe<PickleJarInput> {
   public static final int DEFAULT_FERMENT_TIME = 200;
   private final NonNullList<Ingredient> inputs;
   // 26.x 配方解析阶段尚未绑定 Item 组件，使用 ItemStackTemplate 构造配方结果。
   private final ItemStackTemplate output;
   private final int fermentTime;
   public static final MapCodec<PickleJarRecipe> CODEC = RecordCodecBuilder.mapCodec(
      inst -> inst.group(
            Ingredient.CODEC
               .listOf()
               .fieldOf("ingredients")
               .xmap(list -> {
                  NonNullList<Ingredient> nonNullList = NonNullList.create();
                  nonNullList.addAll(list);
                  return (NonNullList<Ingredient>)nonNullList;
               }, List::copyOf)
               .forGetter(r -> r.inputs),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.output),
            Codec.INT.optionalFieldOf("fermentTime", 200).forGetter(r -> r.fermentTime)
         )
         .apply(inst, PickleJarRecipe::new)
   );
   private static RecipeBookCategory BOOK_CATEGORY;

   public PickleJarRecipe(NonNullList<Ingredient> inputs, ItemStackTemplate output, int fermentTime) {
      this.inputs = inputs;
      this.output = output;
      this.fermentTime = fermentTime;
   }

   public int getFermentTime() {
      return this.fermentTime;
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
   public ItemStack assemble(@NotNull PickleJarInput input) {
      return this.output.create();
   }

   /** 物化产物（运行期调用，此时 Item 组件已绑定）。 */
   @NotNull
   public ItemStack getOutput() {
      return this.output.create();
   }

   public boolean showNotification() {
      return false;
   }

   @NotNull
   public String group() {
      return "";
   }

   @NotNull
   public ItemStack getResultItem(@NotNull net.minecraft.core.HolderLookup.Provider registries) {
      return this.output.create();
   }

   @NotNull
   public RecipeSerializer<? extends Recipe<PickleJarInput>> getSerializer() {
      return ModRecipes.PICKLE_JAR_SERIALIZER;
   }

   @NotNull
   public RecipeType<? extends Recipe<PickleJarInput>> getType() {
      return ModRecipes.PICKLE_JAR_TYPE;
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.inputs;
   }

   @NotNull
   public PlacementInfo placementInfo() {
      return PlacementInfo.NOT_PLACEABLE;
   }

   @NotNull
   public RecipeBookCategory recipeBookCategory() {
      if (BOOK_CATEGORY == null) {
         Identifier id = Identifier.fromNamespaceAndPath(KaleidoscopeChineseFood.MODID, "pickle_jar");
         BOOK_CATEGORY = (RecipeBookCategory)Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, id, new RecipeBookCategory());
      }

      return BOOK_CATEGORY;
   }

   /**
    * 26.1: {@code RecipeSerializer} is now a final record of (MapCodec, StreamCodec);
    * custom serializer classes can no longer implement it.
    */
   public static RecipeSerializer<PickleJarRecipe> makeSerializer() {
      StreamCodec<RegistryFriendlyByteBuf, PickleJarRecipe> streamCodec = new StreamCodec<RegistryFriendlyByteBuf, PickleJarRecipe>() {
         public PickleJarRecipe decode(RegistryFriendlyByteBuf buf) {
            int size = buf.readVarInt();
            NonNullList<Ingredient> inputs = NonNullList.create();

            for (int i = 0; i < size; i++) {
               inputs.add((Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
            }

            ItemStackTemplate output = (ItemStackTemplate)ItemStackTemplate.STREAM_CODEC.decode(buf);
            int time = buf.readVarInt();
            return new PickleJarRecipe(inputs, output, time);
         }

         public void encode(RegistryFriendlyByteBuf buf, PickleJarRecipe recipe) {
            buf.writeVarInt(recipe.inputs.size());

            for (Ingredient ing : recipe.inputs) {
               Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
            }

            ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.output);
            buf.writeVarInt(recipe.fermentTime);
         }
      };
      return new RecipeSerializer<>(PickleJarRecipe.CODEC, streamCodec);
   }
}
