package com.bmt.kaleidoscope_chinesefood.block.entity;

/** 对联/横批共用的墙面文字方块实体契约（1.1.11 重构引入） */
public interface IWallTextBlockEntity {
   String getText();

   void setText(String text);

   int getMaxChars();

   /** 当前分组包含的方块数量（1-3），用于渲染与编辑界面尺寸 */
   int getSegmentCount();

   boolean isGlowing();

   void setGlowing(boolean glowing);
}
