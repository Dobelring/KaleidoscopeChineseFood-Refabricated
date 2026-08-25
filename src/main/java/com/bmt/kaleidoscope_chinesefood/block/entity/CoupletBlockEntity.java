package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class CoupletBlockEntity extends BlockEntity {
   private static final String COUPLET_TEXT_KEY = "CoupletText";
   private static final int DEFAULT_MAX_CHARS = 7;
   private String coupletText = "";

   public CoupletBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.COUPLET_BLOCK_ENTITY, pos, state);
   }

   protected void saveAdditional(@NotNull ValueOutput output) {
      super.saveAdditional(output);
      output.putString("CoupletText", this.coupletText);
   }

   protected void loadAdditional(@NotNull ValueInput input) {
      super.loadAdditional(input);
      this.coupletText = input.getStringOr("CoupletText", "");
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(@NotNull Provider registries) {
      CompoundTag tag = super.getUpdateTag(registries);
      tag.putString("CoupletText", this.coupletText);
      return tag;
   }

   public void setText(String newText) {
      this.coupletText = newText;
      this.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
         // sendBlockUpdated 只同步方块状态，对联文字属于 BlockEntity 数据，必须显式推送
         // 数据包给附近玩家，否则客户端渲染器永远看不到新文字（与腌菜罐幽灵渲染同源）。
         if (this.level instanceof ServerLevel serverLevel) {
            ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this);
            BlockPos pos = this.getBlockPos();
            for (ServerPlayer player : serverLevel.players()) {
               if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64.0 * 64.0) {
                  player.connection.send(packet);
               }
            }
         }
      }
   }

   public String getText() {
      return this.coupletText;
   }

   public String getTruncatedLine(int line) {
      if (line != 0) {
         return "";
      } else {
         int maxLength = this.getMaxChars();
         return this.coupletText.length() > maxLength ? this.coupletText.substring(0, maxLength) : this.coupletText;
      }
   }

   public int getMaxChars() {
      if (this.level == null) {
         return 7;
      } else {
         int totalHeight = 1;

         for (BlockPos currentPos = this.getBlockPos().above(); this.isSameCoupletBlock(this.level.getBlockState(currentPos)); currentPos = currentPos.above()) {
            totalHeight++;
         }
         return switch (totalHeight) {
            case 2 -> 7;
            case 3 -> 11;
            default -> 7;
         };
      }
   }

   private boolean isSameCoupletBlock(BlockState state) {
      return state.is(this.getBlockState().getBlock());
   }
}
