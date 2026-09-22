package com.bmt.kaleidoscope_chinesefood.compat.carryon;

import net.fabricmc.loader.api.FabricLoader;

public class CarryOnCompat {
    public static final String CARRY_ON_MOD_ID = "carryon";

    public CarryOnCompat() {
    }

    public static void registerBlacklist() {
        if (FabricLoader.getInstance().isModLoaded(CARRY_ON_MOD_ID)) {
            // TODO(fabric): Fabric 版 Carry On 没有 Forge 的 InterModComms("blacklistBlock") 机制，
            //  冰箱、横幅、对联等方块的黑名单需改用 Carry On 的配置/数据包方式实现，待人工决定。
        }
    }
}
