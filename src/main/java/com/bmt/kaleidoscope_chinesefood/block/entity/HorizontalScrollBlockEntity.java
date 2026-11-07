package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.block.HorizontalScrollBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HorizontalScrollBlockEntity extends BlockEntity implements IWallTextBlockEntity {
   private static final String BANNER_TEXT_KEY = "BannerText";
   private static final String OWNER_POS_KEY = "OwnerPos";
   private static final String IS_GLOWING_KEY = "IsGlowing";
   private static final int DEFAULT_MAX_CHARS = 4;
   private String bannerText = "";
   @Nullable
   private BlockPos ownerPos;
   private boolean isGlowing = false;

   public HorizontalScrollBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.HORIZONTAL_BANNER, pos, state);
   }

   protected void saveAdditional(@NotNull ValueOutput output) {
      super.saveAdditional(output);
      output.putString(BANNER_TEXT_KEY, this.bannerText);
      if (this.ownerPos != null) {
         output.putLong(OWNER_POS_KEY, this.ownerPos.asLong());
      }

      output.putBoolean(IS_GLOWING_KEY, this.isGlowing);
   }

   protected void loadAdditional(@NotNull ValueInput input) {
      super.loadAdditional(input);
      this.bannerText = input.getStringOr(BANNER_TEXT_KEY, "");
      this.ownerPos = input.getLong(OWNER_POS_KEY).map(BlockPos::of).orElse(null);
      this.isGlowing = input.getBooleanOr(IS_GLOWING_KEY, false);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   // 客户端同步链路：sendBlockUpdated → getUpdatePacket → loadWithComponents → loadAdditional
   // （1.21.1 起 BlockEntity 已无 onDataPacket，勿覆写）
   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putString(BANNER_TEXT_KEY, this.bannerText);
      if (this.ownerPos != null) {
         tag.putLong(OWNER_POS_KEY, this.ownerPos.asLong());
      }

      tag.putBoolean(IS_GLOWING_KEY, this.isGlowing);
      return tag;
   }

   @Override
   public void setText(String newText) {
      this.bannerText = newText;
      this.sync();
   }

   @Override
   public String getText() {
      return this.bannerText;
   }

   /** 分组锚点（最左那一格）；自身即锚点时返回自身 */
   public BlockPos getOwnerPos() {
      if (this.ownerPos != null) {
         return this.ownerPos;
      }

      // 兼容 1.1.10 存档里没有 OwnerPos 的旧方块：按 PART 反推锚点
      BlockPos legacyOwner = this.findLegacyOwner();
      if (legacyOwner != null) {
         this.ownerPos = legacyOwner;
         return legacyOwner;
      }

      return this.getBlockPos();
   }

   @Nullable
   private BlockPos findLegacyOwner() {
      if (this.level == null) {
         return null;
      }

      BlockState state = this.getBlockState();
      HorizontalScrollBlock.BannerPart part = state.getValue(HorizontalScrollBlock.PART);
      if (part == HorizontalScrollBlock.BannerPart.SINGLE || part == HorizontalScrollBlock.BannerPart.LEFT) {
         return null;
      }

      Direction facing = state.getValue(HorizontalScrollBlock.FACING);

      for (int i = 1; i <= 2; i++) {
         BlockPos left = this.getBlockPos().relative(facing.getClockWise(), i);
         BlockState leftState = this.level.getBlockState(left);
         if (!(leftState.getBlock() instanceof HorizontalScrollBlock) || leftState.getValue(HorizontalScrollBlock.FACING) != facing) {
            break;
         }

         HorizontalScrollBlock.BannerPart leftPart = leftState.getValue(HorizontalScrollBlock.PART);
         if (leftPart == HorizontalScrollBlock.BannerPart.SINGLE || leftPart == HorizontalScrollBlock.BannerPart.LEFT) {
            return left;
         }
      }

      return null;
   }

   public void setOwnerPos(@Nullable BlockPos owner) {
      this.ownerPos = owner != null && owner.equals(this.getBlockPos()) ? null : owner;
      this.sync();
   }

   @Override
   public boolean isGlowing() {
      return this.isGlowing;
   }

   @Override
   public void setGlowing(boolean glowing) {
      this.isGlowing = glowing;
      this.sync();
   }

   public String getTruncatedLine(int line) {
      if (line != 0) {
         return "";
      }

      int maxLength = this.getMaxChars();
      return this.bannerText.length() > maxLength ? this.bannerText.substring(0, maxLength) : this.bannerText;
   }

   /** 从锚点沿横批方向数同组格数 */
   private int countGroupSize() {
      if (this.level == null) {
         return 1;
      }

      BlockState state = this.getBlockState();
      Direction facing = state.getValue(HorizontalScrollBlock.FACING);
      BlockPos master = this.getOwnerPos();
      int count = 0;
      BlockPos cur = master;

      for (int i = 0; i < 4; i++) {
         BlockState s = this.level.getBlockState(cur);
         if (!(s.getBlock() instanceof HorizontalScrollBlock)
            || s.getValue(HorizontalScrollBlock.FACING) != facing
            || !(this.level.getBlockEntity(cur) instanceof HorizontalScrollBlockEntity be)
            || !be.getOwnerPos().equals(master)) {
            break;
         }

         count++;
         cur = cur.relative(facing.getCounterClockWise());
      }

      return Math.max(1, count);
   }

   @Override
   public int getSegmentCount() {
      return Math.min(3, this.countGroupSize());
   }

   @Override
   public int getMaxChars() {
      if (this.level == null) {
         return DEFAULT_MAX_CHARS;
      }

      return switch (this.countGroupSize()) {
         case 2 -> 8;
         case 3 -> 12;
         default -> DEFAULT_MAX_CHARS;
      };
   }

   private void sync() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
      }
   }
}
