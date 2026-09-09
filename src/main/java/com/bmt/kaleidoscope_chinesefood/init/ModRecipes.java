package com.bmt.kaleidoscope_chinesefood.init;

import com.bmt.kaleidoscope_chinesefood.KaleidoscopeChineseFood;
import com.bmt.kaleidoscope_chinesefood.crafting.BaseProcessingRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.FreezingRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.PickleJarRecipe;
import com.bmt.kaleidoscope_chinesefood.crafting.RefrigeratingRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {
    public static RecipeType<PickleJarRecipe> PICKLE_JAR_TYPE;
    public static RecipeSerializer<PickleJarRecipe> PICKLE_JAR_SERIALIZER;
    public static RecipeType<RefrigeratingRecipe> REFRIGERATING_TYPE;
    public static RecipeSerializer<RefrigeratingRecipe> REFRIGERATING_SERIALIZER;
    public static RecipeType<FreezingRecipe> FREEZING_TYPE;
    public static RecipeSerializer<FreezingRecipe> FREEZING_SERIALIZER;
    public static RecipeBookCategory PICKLE_JAR_CATEGORY;
    public static RecipeBookCategory REFRIGERATING_CATEGORY;
    public static RecipeBookCategory FREEZING_CATEGORY;

    public static void register() {
        PICKLE_JAR_TYPE = registerType("pickle_jar");
        PICKLE_JAR_SERIALIZER = registerSerializer("pickle_jar",
                new RecipeSerializer<>(PickleJarRecipe.CODEC, pickleJarStreamCodec()));
        REFRIGERATING_TYPE = registerType("refrigerating");
        REFRIGERATING_SERIALIZER = registerSerializer("refrigerating",
                new RecipeSerializer<>(BaseProcessingRecipe.buildCodec(RefrigeratingRecipe::new),
                        BaseProcessingRecipe.buildStreamCodec(RefrigeratingRecipe::new)));
        FREEZING_TYPE = registerType("freezing");
        FREEZING_SERIALIZER = registerSerializer("freezing",
                new RecipeSerializer<>(BaseProcessingRecipe.buildCodec(FreezingRecipe::new),
                        BaseProcessingRecipe.buildStreamCodec(FreezingRecipe::new)));
        // Recipe 接口要求每个配方提供 recipeBookCategory
        PICKLE_JAR_CATEGORY = registerCategory("pickle_jar");
        REFRIGERATING_CATEGORY = registerCategory("refrigerating");
        FREEZING_CATEGORY = registerCategory("freezing");
    }

    /** 26.1.2 RecipeSerializer 为 record；腌菜罐编解码列表与原 26.1.2 前版本字节流格式一致 */
    private static StreamCodec<RegistryFriendlyByteBuf, PickleJarRecipe> pickleJarStreamCodec() {
        return new StreamCodec<RegistryFriendlyByteBuf, PickleJarRecipe>() {
            public PickleJarRecipe decode(RegistryFriendlyByteBuf buf) {
                int size = buf.readVarInt();
                NonNullList<net.minecraft.world.item.crafting.Ingredient> inputs = NonNullList.create();
                for (int i = 0; i < size; i++) {
                    inputs.add(net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                }
                net.minecraft.world.item.ItemStackTemplate output = net.minecraft.world.item.ItemStackTemplate.STREAM_CODEC.decode(buf);
                int time = buf.readVarInt();
                return new PickleJarRecipe(inputs, output, time);
            }

            public void encode(RegistryFriendlyByteBuf buf, PickleJarRecipe recipe) {
                buf.writeVarInt(recipe.getIngredients().size());
                for (net.minecraft.world.item.crafting.Ingredient ing : recipe.getIngredients()) {
                    net.minecraft.world.item.crafting.Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                }
                net.minecraft.world.item.ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.getOutputTemplate());
                buf.writeVarInt(recipe.getFermentTime());
            }
        };
    }

    private static RecipeBookCategory registerCategory(String name) {
        return Registry.register(BuiltInRegistries.RECIPE_BOOK_CATEGORY, KaleidoscopeChineseFood.id(name), new RecipeBookCategory());
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(String name) {
        Identifier id = KaleidoscopeChineseFood.id(name);
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, id, new RecipeType<T>() {
            @Override
            public String toString() {
                return id.toString();
            }
        });
    }

    private static <T extends Recipe<?>> RecipeSerializer<T> registerSerializer(String name, RecipeSerializer<T> serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, KaleidoscopeChineseFood.id(name), serializer);
    }
}
