package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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

/**
 * 26.1.2：result 用 ItemStackTemplate（decode 期组件未初始化）；serializer 变 record
 * 直接 new RecipeSerializer<>(codec, streamCodec)；assemble(T) 无注册表参数。
 */
public class PickleJarRecipe implements Recipe<PickleJarInput> {
   public static final int DEFAULT_FERMENT_TIME = 200;
   private final NonNullList<Ingredient> inputs;
   private final ItemStackTemplate output;
   private final int fermentTime;
   public static final MapCodec<PickleJarRecipe> CODEC = RecordCodecBuilder.mapCodec(
      inst -> inst.group(
            // 输入列表只保存实际配料；matches() 按列表长度比较，无需占位填充
            Ingredient.CODEC
               .listOf()
               .fieldOf("ingredients")
               .xmap(list -> {
                  NonNullList<Ingredient> inputs = NonNullList.create();
                  inputs.addAll(list);
                  return inputs;
               }, List::copyOf)
               .forGetter(r -> r.inputs),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.output),
            Codec.INT.optionalFieldOf("fermentTime", 200).forGetter(r -> r.fermentTime)
         )
         .apply(inst, PickleJarRecipe::new)
   );

   public PickleJarRecipe(NonNullList<Ingredient> inputs, ItemStackTemplate output, int fermentTime) {
      this.inputs = inputs;
      this.output = output;
      this.fermentTime = fermentTime;
   }

   public int getFermentTime() {
      return this.fermentTime;
   }

   public ItemStack getOutput() {
      return this.output.create();
   }

   /** 流同步用：原始模板 */
   public ItemStackTemplate getOutputTemplate() {
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
   public ItemStack assemble(@NotNull PickleJarInput input) {
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
      return PlacementInfo.create(this.inputs);
   }

   public RecipeBookCategory recipeBookCategory() {
      return ModRecipes.PICKLE_JAR_CATEGORY;
   }

   @NotNull
   public NonNullList<Ingredient> getIngredients() {
      return this.inputs;
   }
}
