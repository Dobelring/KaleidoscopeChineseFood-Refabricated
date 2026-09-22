package com.bmt.kaleidoscope_chinesefood.block.entity;

import com.bmt.kaleidoscope_chinesefood.block.HorizontalBannerBlock;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HorizontalBannerBlockEntity extends BlockEntity implements IWallTextBlockEntity {
    private static final String BANNER_TEXT_KEY = "BannerText";
    private static final String OWNER_KEY = "OwnerPos";
    private static final String GLOWING_KEY = "IsGlowing";
    private static final int DEFAULT_MAX_CHARS = 4;
    private String bannerText = "";
    @Nullable
    private BlockPos ownerPos;
    private boolean isGlowing = false;

    public HorizontalBannerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HORIZONTAL_BANNER, pos, state);
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("BannerText", this.bannerText);
        if (this.ownerPos != null) {
            tag.putLong("OwnerPos", this.ownerPos.asLong());
        }

        tag.putBoolean("IsGlowing", this.isGlowing);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.bannerText = tag.getString("BannerText");
        this.ownerPos = tag.contains("OwnerPos") ? BlockPos.of(tag.getLong("OwnerPos")) : null;
        this.isGlowing = tag.getBoolean("IsGlowing");
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.load(tag);
            this.setChanged();
        }
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putString("BannerText", this.bannerText);
        if (this.ownerPos != null) {
            tag.putLong("OwnerPos", this.ownerPos.asLong());
        }

        tag.putBoolean("IsGlowing", this.isGlowing);
        return tag;
    }

    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public void setText(String newText) {
        this.bannerText = newText;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public String getText() {
        return this.bannerText;
    }

    public BlockPos getOwnerPos() {
        return this.ownerPos != null ? this.ownerPos : this.getBlockPos();
    }

    public void setOwnerPos(BlockPos owner) {
        this.ownerPos = owner != null && owner.equals(this.getBlockPos()) ? null : owner;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public boolean isGlowing() {
        return this.isGlowing;
    }

    @Override
    public void setGlowing(boolean glowing) {
        this.isGlowing = glowing;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public String getTruncatedLine(int line) {
        if (line != 0) {
            return "";
        } else {
            int maxLength = this.getMaxChars();
            return this.bannerText.length() > maxLength ? this.bannerText.substring(0, maxLength) : this.bannerText;
        }
    }

    private int countGroupSize() {
        if (this.level == null) {
            return 1;
        } else {
            BlockState state = this.getBlockState();
            Direction facing = (Direction)state.getValue(HorizontalBannerBlock.FACING);
            BlockPos master = this.getOwnerPos();
            int count = 0;
            BlockPos cur = master;

            for (int i = 0; i < 4; i++) {
                BlockState s = this.level.getBlockState(cur);
                if (!(s.getBlock() instanceof HorizontalBannerBlock)
                    || s.getValue(HorizontalBannerBlock.FACING) != facing
                    || !(this.level.getBlockEntity(cur) instanceof HorizontalBannerBlockEntity be)
                    || !be.getOwnerPos().equals(master)) {
                    break;
                }

                count++;
                cur = cur.relative(facing.getCounterClockWise());
            }

            return Math.max(1, count);
        }
    }

    @Override
    public int getSegmentCount() {
        return Math.min(3, this.countGroupSize());
    }

    @Override
    public int getMaxChars() {
        if (this.level == null) {
            return 4;
        } else {
            return switch (this.countGroupSize()) {
                case 2 -> 8;
                case 3 -> 12;
                default -> 4;
            };
        }
    }
}
