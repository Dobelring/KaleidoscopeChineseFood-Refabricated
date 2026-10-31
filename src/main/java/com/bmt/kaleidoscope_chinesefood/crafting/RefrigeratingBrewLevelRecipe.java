package com.bmt.kaleidoscope_chinesefood.crafting;

import com.bmt.kaleidoscope_chinesefood.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * 官方 1.1.16 新增（changelog 未列）：冷藏时保留冲泡等级的配方类型——
 * 拷贝酒馆的 {@code kaleidoscope_tavern:brew_level} 数据组件，并把
 * propagate_nbt 列出的键从输入 CUSTOM_DATA 复制到输出
 * （用于 smc:ice_tea → liquor cool_tea）。
 */
public class RefrigeratingBrewLevelRecipe extends RefrigeratingRecipe {
    private static final ResourceLocation BREW_LEVEL_ID = ResourceLocation.fromNamespaceAndPath("kaleidoscope_tavern", "brew_level");
    private final List<String> propagateNbtKeys;

    public RefrigeratingBrewLevelRecipe(Ingredient input, ItemStack output, int baseTime, List<String> propagateNbtKeys) {
        super(input, output, baseTime);
        this.propagateNbtKeys = Collections.unmodifiableList(propagateNbtKeys);
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull FreezerInput input, @NotNull Provider registries) {
        ItemStack result = this.output.copy();
        ItemStack inputStack = input.getItem(0);
        if (inputStack.isEmpty()) {
            return result;
        } else {
            DataComponentType<Integer> brewLevelType = (DataComponentType<Integer>)BuiltInRegistries.DATA_COMPONENT_TYPE.get(BREW_LEVEL_ID);
            if (brewLevelType != null) {
                Integer brewLevel = (Integer)inputStack.get(brewLevelType);
                if (brewLevel != null) {
                    result.set(brewLevelType, brewLevel);
                }
            }

            if (!this.propagateNbtKeys.isEmpty()) {
                CustomData inputData = (CustomData)inputStack.get(DataComponents.CUSTOM_DATA);
                if (inputData != null) {
                    CompoundTag inputTag = inputData.getUnsafe();
                    CustomData updatedData = ((CustomData)result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).update(resultTag -> {
                        for (String key : this.propagateNbtKeys) {
                            if (inputTag.contains(key)) {
                                resultTag.put(key, inputTag.get(key).copy());
                            }
                        }
                    });
                    result.set(DataComponents.CUSTOM_DATA, updatedData);
                }
            }

            return result;
        }
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer<?>)ModRecipes.REFRIGERATING_BREW_LEVEL_SERIALIZER;
    }

    public List<String> getPropagateNbtKeys() {
        return this.propagateNbtKeys;
    }

    public static class Serializer implements RecipeSerializer<RefrigeratingBrewLevelRecipe> {
        private static final MapCodec<RefrigeratingBrewLevelRecipe> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                  Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(r -> r.input),
                  ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output),
                  Codec.INT.optionalFieldOf("base_time", 100).forGetter(r -> r.baseTime),
                  Codec.STRING.listOf().optionalFieldOf("propagate_nbt", Collections.emptyList()).forGetter(r -> r.propagateNbtKeys)
               )
               .apply(inst, RefrigeratingBrewLevelRecipe::new)
        );
        private static final StreamCodec<RegistryFriendlyByteBuf, RefrigeratingBrewLevelRecipe> STREAM_CODEC = StreamCodec.of(
            RefrigeratingBrewLevelRecipe.Serializer::encodeNetwork, RefrigeratingBrewLevelRecipe.Serializer::decodeNetwork
        );

        private static RefrigeratingBrewLevelRecipe decodeNetwork(RegistryFriendlyByteBuf buf) {
            Ingredient input = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
            ItemStack output = (ItemStack)ItemStack.STREAM_CODEC.decode(buf);
            int time = buf.readVarInt();
            int count = buf.readVarInt();
            List<String> keys = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                keys.add(buf.readUtf());
            }

            return new RefrigeratingBrewLevelRecipe(input, output, time, keys);
        }

        private static void encodeNetwork(RegistryFriendlyByteBuf buf, RefrigeratingBrewLevelRecipe recipe) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
            ItemStack.STREAM_CODEC.encode(buf, recipe.output);
            buf.writeVarInt(recipe.baseTime);
            buf.writeVarInt(recipe.propagateNbtKeys.size());

            for (String key : recipe.propagateNbtKeys) {
                buf.writeUtf(key);
            }
        }

        @NotNull
        @Override
        public MapCodec<RefrigeratingBrewLevelRecipe> codec() {
            return CODEC;
        }

        @NotNull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RefrigeratingBrewLevelRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
