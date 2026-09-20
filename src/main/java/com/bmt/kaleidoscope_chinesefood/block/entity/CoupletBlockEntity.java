package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.block.CoupletBlock;
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

public class CoupletBlockEntity extends BlockEntity implements IWallTextBlockEntity {
   private static final String COUPLET_TEXT_KEY = "CoupletText";
   private static final String OWNER_POS_KEY = "OwnerPos";
   private static final String IS_GLOWING_KEY = "IsGlowing";
   private static final int DEFAULT_MAX_CHARS = 7;
   private String coupletText = "";
   @Nullable
   private BlockPos ownerPos;
   private boolean isGlowing = false;

   public CoupletBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.COUPLET_BLOCK_ENTITY, pos, state);
   }

   protected void saveAdditional(@NotNull ValueOutput output) {
      super.saveAdditional(output);
      output.putString(COUPLET_TEXT_KEY, this.coupletText);
      if (this.ownerPos != null) {
         output.putLong(OWNER_POS_KEY, this.ownerPos.asLong());
      }

      output.putBoolean(IS_GLOWING_KEY, this.isGlowing);
   }

   protected void loadAdditional(@NotNull ValueInput input) {
      super.loadAdditional(input);
      this.coupletText = input.getStringOr(COUPLET_TEXT_KEY, "");
      this.ownerPos = input.getLong(OWNER_POS_KEY).map(BlockPos::of).orElse(null);
      this.isGlowing = input.getBooleanOr(IS_GLOWING_KEY, false);
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   // 客户端同步链路：sendBlockUpdated → getUpdatePacket → loadWithComponents → loadAdditional
   // （1.21.1 起 BlockEntity 已无 onDataPacket/handleUpdateTag，勿覆写）
   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putString(COUPLET_TEXT_KEY, this.coupletText);
      if (this.ownerPos != null) {
         tag.putLong(OWNER_POS_KEY, this.ownerPos.asLong());
      }

      tag.putBoolean(IS_GLOWING_KEY, this.isGlowing);
      return tag;
   }

   @Override
   public void setText(String newText) {
      this.coupletText = newText;
      this.sync();
   }

   @Override
   public String getText() {
      return this.coupletText;
   }

   /** 分组锚点（最下方那一格）；自身即锚点时返回自身 */
   public BlockPos getOwnerPos() {
      if (this.ownerPos != null) {
         return this.ownerPos;
      }

      // 兼容 1.1.10 存档里没有 OwnerPos 的旧方块：按 PART 反推锚点。
      // 只修内存不再同步，服务端与客户端各自推出相同结果。
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
      if (state.getValue(CoupletBlock.PART) == CoupletBlock.CoupletPart.LOWER) {
         return null;
      }

      Direction facing = state.getValue(CoupletBlock.FACING);

      for (int i = 1; i <= 2; i++) {
         BlockPos below = this.getBlockPos().below(i);
         BlockState belowState = this.level.getBlockState(below);
         if (!(belowState.getBlock() instanceof CoupletBlock) || belowState.getValue(CoupletBlock.FACING) != facing) {
            break;
         }

         if (belowState.getValue(CoupletBlock.PART) == CoupletBlock.CoupletPart.LOWER) {
            return below;
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
      return this.coupletText.length() > maxLength ? this.coupletText.substring(0, maxLength) : this.coupletText;
   }

   /** 沿锚点向上数同组格数 */
   private int countGroupSize() {
      if (this.level == null) {
         return 1;
      }

      BlockPos master = this.getOwnerPos();
      int count = 0;
      BlockPos cur = master;

      for (int i = 0; i < 4; i++) {
         BlockState s = this.level.getBlockState(cur);
         if (!(s.getBlock() instanceof CoupletBlock)
            || !(this.level.getBlockEntity(cur) instanceof CoupletBlockEntity be)
            || !be.getOwnerPos().equals(master)) {
            break;
         }

         count++;
         cur = cur.above();
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
         case 2 -> 7;
         case 3 -> 13;
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
