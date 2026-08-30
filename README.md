## About
# Kaleidoscope Chinese Food (Fabric)
# 森罗物语：国味 - Fabric 移植

> A Minecraft Fabric mod that adds a rich Chinese / Sichuan cuisine expansion to Kaleidoscope Cookery.

## Compendium
- This is the **Fabric port** of [**Kaleidoscope Chinese Food**](https://modrinth.com/mod/kaleidoscopechinesefood) (森罗物语：国味), corresponding to NeoForge `1.1.8`.
- Requires [**Kaleidoscope Cookery Refabricated**](https://modrinth.com/mod/kaleidoscope-cookery-refabricated) (森罗物语：厨房 重制) and [**Forge Config API Port**](https://modrinth.com/mod/forge-config-api-port).
- No backport planned.

## Overview
![Minecraft](https://img.shields.io/badge/Minecraft-Java%20Edition-brightgreen)
![Fabric](https://img.shields.io/badge/1.21.1%20%7C%2026.1.2%20%7C%2026.2-orange)
![License](https://img.shields.io/badge/License-CC_BY--NC--ND_4.0-lightgrey)

This mod brings a variety of Chinese cuisine and folk items to "Kaleidoscope: Cookery".

## Content
- A large collection of Chinese dishes: mooncake (月饼), wonton (抄手), wonton noodles, yangrou paomo (羊肉泡馍), sauerkraut beef noodles, maocai (冒菜), twice-cooked pork (回锅肉), big plate chicken (大盘鸡), Yangzhou fried rice (扬州炒饭), four-joy meatballs (四喜丸子), and many more.
- Freezer (冰箱): a multi-slot storage block, available in 6 colors.
- Pickle Jar (泡菜坛): pickles ingredients into preserved food.
- Couplet (对联), Fu character (福字) and horizontal banner (横批): display player-written text.
- Kongming lantern (孔明灯) and firecracker (鞭炮).
- Eggplant crop (茄子).
- Two unique status effects: Lava Swim (熔岩游泳) and Saturation Shield (饱食护盾).

## Compat
JEI / Jade integration is **not yet available** for Minecraft 26.1 — neither mod has shipped a 26.1 build yet. The integration code is kept in `compat-staging/` and will be re-enabled once those builds are released.
- **JEI**: view pickling / refrigerating / freezing recipes.
- **Jade**: shows the pickle jar's progress.

## Build
Requires JDK 26 to build (the Gradle toolchain targets Java 25 bytecode); Minecraft 26.1 runs on Java 25.

Dependency jars (Kaleidoscope Cookery, Forge Config API Port) are committed under `libs/` and referenced directly by `build.gradle`.

```
./gradlew build
```

The artifact is produced in `build/libs/`.

## License
This project is a **Fabric port** of the original Kaleidoscope Chinese Food (森罗物语：国味), which is licensed under **CC BY-NC-ND 4.0**. As a derivative work, the whole port is distributed under the same license — see [LICENSE](LICENSE).

Original mod by 辰笺渡月 (chenjdy / ChenjdyUltra), 白馒头 (BmtUltra), 白帆小喵L (bf_meow), witzig梦行粥. Fabric port by [Dobelring](https://github.com/Dobelring).
