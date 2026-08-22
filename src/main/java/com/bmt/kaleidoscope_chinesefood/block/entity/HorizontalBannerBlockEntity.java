package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.block.HorizontalBannerBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class HorizontalBannerBlockEntity extends BlockEntity {
   private static final String BANNER_TEXT_KEY = "BannerText";
   private static final int DEFAULT_MAX_CHARS = 4;
   private String bannerText = "";

   public HorizontalBannerBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.HORIZONTAL_BANNER, pos, state);
   }

   protected void saveAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
      super.saveAdditional(tag, registries);
      tag.putString("BannerText", this.bannerText);
   }

   protected void loadAdditional(@NotNull CompoundTag tag, @NotNull Provider registries) {
      super.loadAdditional(tag, registries);
      this.bannerText = tag.getString("BannerText");
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public void onDataPacket(@NotNull Connection net, ClientboundBlockEntityDataPacket pkt, @NotNull Provider registries) {
      CompoundTag tag = pkt.getTag();
      this.loadAdditional(tag, registries);
      this.setChanged();
   }

   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putString("BannerText", this.bannerText);
      return tag;
   }

   public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull Provider registries) {
      this.loadAdditional(tag, registries);
   }

   public void setText(String newText) {
      this.bannerText = newText;
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public String getText() {
      return this.bannerText;
   }

   public String getTruncatedLine(int line) {
      if (line != 0) {
         return "";
      } else {
         int maxLength = this.getMaxChars();
         return this.bannerText.length() > maxLength ? this.bannerText.substring(0, maxLength) : this.bannerText;
      }
   }

   private int getMaxChars() {
      if (this.level == null) {
         return 4;
      } else {
         BlockState state = this.getBlockState();
         int totalWidth = 1;
         Direction facing = (Direction)state.getValue(HorizontalBannerBlock.FACING);

         for (BlockPos currentPos = this.getBlockPos().relative(facing.getCounterClockWise());
            this.isSameBannerBlock(this.level.getBlockState(currentPos), facing);
            currentPos = currentPos.relative(facing.getCounterClockWise())
         ) {
            totalWidth++;
         }
         return switch (totalWidth) {
            case 1 -> 4;
            case 2 -> 8;
            case 3 -> 13;
            default -> 4;
         };
      }
   }

   private boolean isSameBannerBlock(BlockState state, Direction facing) {
      return state.is(this.getBlockState().getBlock()) && state.getValue(HorizontalBannerBlock.FACING) == facing;
   }
}
