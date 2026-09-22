package com.bmt.kaleidoscope_chinesefood.block.entity;

public interface IWallTextBlockEntity {
    String getText();

    void setText(String var1);

    int getMaxChars();

    int getSegmentCount();

    boolean isGlowing();

    void setGlowing(boolean var1);
}
