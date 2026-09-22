# 国味 1.20.1 Fabric 转换规则（Forge 1.20.1 → Fabric 1.20.1）

> 目标：把官方 **Forge 1.20.1** 国味（1.1.12-fix）转换成 **Fabric 1.20.1** 工程。
> **同 MC 版本**，所以**不涉及任何原版 API 版本回退**，只做加载器 API 翻译。

## 0. 硬性纪律

1. **绝不参考其他 MC 版本的代码**（1.21.1 / 1.21.11 / 26.x 一律不看）。同一版本内可参考的只有：
   - Forge 1.20.1 反编译源码（权威源）：`1.20.1/_refsources/official-1.1.12-1201-mojmap/`
   - **同版本 Fabric 参考工程**：`1.20.1/KaleidoscopeCookery-Refabricated-1.20.1-fabric/`（Forge-1.20.1→Fabric-1.20.1 的同类转换，**所有 Fabric API 写法以它为准**）
   - Fabric 官方文档（离线副本见 memory）
2. 保持原有的类名、包名、字段名、方法名、常量名不变，**只改加载器相关的类型与调用**。不要顺手重构、不要改命名、不要合并类。
3. 保留原有的中文注释与英文注释，**不要翻译、不要删除**。
4. 代码风格：与原文件一致（4 空格缩进；原文件是 3 空格缩进的反编译产物，请统一改成 4 空格）。

## 1. 注册表：DeferredRegister/RegistryObject → eager 字段 + Registry.register

Forge：
```java
public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
public static final RegistryObject<Item> SALT_BUCKET = ITEMS.register("salt_bucket", () -> new Item(new Properties()));
public static void register(IEventBus bus) { ITEMS.register(bus); }
```
Fabric（**照抄 cookery 的 `init/ModItems.java` 范式**）：
```java
public static final Item SALT_BUCKET = new Item(new Item.Properties());
// ...
public static void registerItems() {
    Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "salt_bucket"), SALT_BUCKET);
}
```
- `ForgeRegistries.ITEMS` → `BuiltInRegistries.ITEM`；`BLOCKS` → `BuiltInRegistries.BLOCK`；`MOB_EFFECTS` → `BuiltInRegistries.MOB_EFFECT`；`MENU_TYPES` → `BuiltInRegistries.MENU`；`ENTITY_TYPES` → `BuiltInRegistries.ENTITY_TYPE`；`BLOCK_ENTITY_TYPES` → `BuiltInRegistries.BLOCK_ENTITY_TYPE`；`SOUND_EVENTS` → `BuiltInRegistries.SOUND_EVENT`；`RECIPE_TYPES` → `BuiltInRegistries.RECIPE_TYPE`；`RECIPE_SERIALIZERS` → `BuiltInRegistries.RECIPE_SERIALIZER`；`CREATIVE_MODE_TABS` → `BuiltInRegistries.CREATIVE_MODE_TAB`；`PARTICLE_TYPES` → `BuiltInRegistries.PARTICLE_TYPE`。
- **字段引用**：`ModItems.SALT_BUCKET.get()` → `ModItems.SALT_BUCKET`；`ModBlocks.X.get()` → `ModBlocks.X`。
- 所有 `(Block)ModBlocks.X.get()` 的强制转换直接去掉：`ModBlocks.X`。
- **类初始化顺序**：`ModBlocks.registerBlocks()` 必须在 `ModItems.registerItems()` **之前**调用（主类里按此顺序）。任何 **ModBlocks ↔ ModItems 的互相引用**（例如作物方块要种子物品、方块物品要方块）必须用 **lambda/Supplier 延迟**，例如 cookery 的 `new BaseCropBlock(() -> ModItems.TOMATO, () -> ModItems.TOMATO_SEED)`。**绝不允许**在静态字段初始化时直接读取另一个注册类的字段。

## 2. ResourceLocation

- **禁止** `ResourceLocation.fromNamespaceAndPath(a, b)`（这是 Forge 47.x 的便利方法，**Fabric 1.20.1 没有**）→ 改成 `new ResourceLocation(a, b)`。
- `ResourceLocation.tryBuild(a, b)` 原版 1.20.1 **有**，可以保留；但为统一起见，能确定参数合法时也用 `new ResourceLocation(a, b)`。
- `ResourceLocation.parse(s)` 不存在 → `new ResourceLocation(s)`。
- `ResourceLocation.tryParse(s)` 原版 1.20.1 有，保留。

## 3. 事件

| Forge | Fabric 1.20.1 |
|---|---|
| `@Mod.EventBusSubscriber` + `@SubscribeEvent`（MOD 总线） | 在 `ModInitializer` 里直接调用注册方法 |
| `MinecraftForge.EVENT_BUS.register(this)` + `@SubscribeEvent`（FORGE 总线） | 用对应的 Fabric API 回调，见下表 |
| `BuildCreativeModeTabContentsEvent` | `ItemGroupEvents.modifyEntriesEvent(ResourceKey<CreativeModeTab>)` → `entries.accept(stack)` |
| `ItemTooltipEvent` | `net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback` |
| `VillagerTradesEvent` | `net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper.registerVillagerOffers(villagerType, level, list -> {...})` |
| `global_loot_modifiers`（Forge GLM，`data/forge/loot_modifiers/`） | `net.fabricmc.fabric.api.loot.v2.LootTableEvents.MODIFY`（**只对改过的表返回**；不要用 REPLACE） |
| `FMLCommonSetupEvent` / `FMLClientSetupEvent` / `FMLLoadCompleteEvent` | `ModInitializer.onInitialize()` / `ClientModInitializer.onInitializeClient()` |
| `ModConfigEvent.Loading` / `.Reloading` | 用 Forge Config API Port 的 `ModConfigEvent`（同包名，见第 4 节）；或直接省掉空实现 |

**注意**：原 Forge 代码里那些**空实现的 `@SubscribeEvent` 方法**（如 `ModConfig.onLoad/onReload` 里什么都没做）→ 直接删除方法与 `@EventBusSubscriber` 注解。

## 4. 配置（ForgeConfigSpec）

`Forge Config API Port` 在 Fabric 1.20.1 上**原样提供 `net.minecraftforge.common.ForgeConfigSpec`**，所以 `config/ModConfig.java` 里 `ForgeConfigSpec`/`Builder`/`BooleanValue` 的写法**全部保留不动**。

只改注册方式（照抄 cookery 的 `KaleidoscopeCookery.java`）：
```java
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
// 主类里：
ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, ModConfig.SPEC);
```
- `net.minecraftforge.fml.config.ModConfig.Type` 由该 port 提供，保留。
- 主类里 `container.addConfig(...)` 那套（`ModContainer`/`ModList.getModContainerById`）→ 删掉，换成上面一行。

## 5. 网络

Forge 的 `SimpleChannel` + `registerMessage` → Fabric API 的 `FabricPacket` + `PacketType`（**照抄 cookery 的 `network/NetworkHandler.java` 与 `network/message/ThrowBaoziMessage.java`**）：
```java
public class XxxPacket implements FabricPacket, ServerPlayNetworking.PlayPacketHandler<XxxPacket> {
    public static final PacketType<XxxPacket> TYPE = PacketType.create(ID, XxxPacket::new);
    public XxxPacket(FriendlyByteBuf buf) { /* 原 decode 内容 */ }
    public XxxPacket(...) { /* 原构造 */ }
    @Override public void write(FriendlyByteBuf buf) { /* 原 encode 内容 */ }
    @Override public PacketType<?> getType() { return TYPE; }
    @Override public void receive(XxxPacket p, ServerPlayer player, PacketSender sender) { /* 原 handle 内容 */ }
}
```
- 注册：`ServerPlayNetworking.registerGlobalReceiver(XxxPacket.TYPE, new XxxPacket(...))`（服务端收）/ `ClientPlayNetworking.registerGlobalReceiver(TYPE, handler)`（客户端收）。
- 发送：`ServerPlayNetworking.send(player, packet)` / `ClientPlayNetworking.send(packet)`；发送前用 `canSend(TYPE)` 守卫（客户端）。
- 客户端收包处理类（原 `ClientPacketHandler`）→ 在 `ClientModInitializer` 里注册，并加 `@Environment(EnvType.CLIENT)`。
- **原 Forge 的 `NetworkEvent.Context`（`ctx.enqueueWork(...)` / `ctx.setPacketHandled(true)`）→ 直接删掉**，Fabric 的 `receive` 已经在主线程执行。
- `NetworkHooks.openScreen(player, ...)` → `player.openMenu(...)`（配 `ExtendedScreenHandlerFactory` 时用 Fabric 的 `net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory`）。

## 6. 物品容器（Forge ItemStackHandler）

Forge 的 `net.minecraftforge.items.{ItemStackHandler, IItemHandler, ItemHandlerHelper}` 在 Fabric 1.20.1 不存在。

**做法：把 cookery 1.20.1 Fabric 工程里这三个类复制进本工程的 `util/forge/` 包**（它们是 NeoForge 源码的拷贝，自包含）：
- `1.20.1/KaleidoscopeCookery-Refabricated-1.20.1-fabric/src/main/java/com/github/ysbbbbbb/kaleidoscopecookery/util/forge/{IItemHandler,ItemStackHandler,ItemHandlerHelper}.java`
- 复制后**包名改成 `com.bmt.kaleidoscope_chinesefood.util.forge`**，类内容保持原样（**注意**：若其内容引用了 cookery 的类，需一并处理；如果它引用了 `HolderLookup` 等新 API，改成 1.20.1 的写法）。
- 业务代码里 `import net.minecraftforge.items.ItemStackHandler;` → `import com.bmt.kaleidoscope_chinesefood.util.forge.ItemStackHandler;`，其余调用签名不变。
- `LazyOptional` / `Capability` / `ForgeCapabilities`（能力系统）→ **直接删掉**，改为直接持有 `ItemStackHandler` 字段并暴露 getter（Fabric 无能力系统；`IPickleJar` 这类接口直接暴露 handler）。

## 7. 环境判定

- `net.minecraftforge.api.distmarker.Dist` → `net.fabricmc.api.EnvType`
- `@OnlyIn(Dist.CLIENT)` → `@Environment(EnvType.CLIENT)`
- `FMLEnvironment.dist.isClient()` → `FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT`
- `DistExecutor.unsafeRunWhenOn(...)` → 直接调（客户端类上加 `@Environment(EnvType.CLIENT)`）
- `ModList.get().isLoaded("x")` → `FabricLoader.getInstance().isModLoaded("x")`
- `ModList.get().getModContainerById(x)` → `FabricLoader.getInstance().getModContainer(x)`

## 8. 方块/物品/实体 的加载器差异

- `ForgeSpawnEggItem` → 原版 `net.minecraft.world.item.SpawnEggItem`（1.20.1 构造器是 public，`(EntityType, int, int, Properties)`）。
- `IForgeMenuType.create(FreezerMenu::new)` → `new net.minecraft.world.inventory.MenuType<>(FreezerMenu::new)`；若需要额外数据同步，用 `net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType`。
- 方块/物品 `Properties` 相关：**1.20.1 原版 API 与 Forge 基本一致**，保留。`Item.Properties` 用 `new Item.Properties()`。
- `ForgeMod` / `FluidType` / `ForgeFlowingFluid`（Forge 流体）→ Fabric 没有对应物。凡是用到的地方（岩浆视野、`FluidTypeMixin`、`LavaSwimEffect` 相关渲染）→ **先按语义改成不依赖 FluidType 的写法**（用 `Fluids.LAVA`/`FluidState` 判定），无法直接对应的用 `// TODO(fabric): ` 注释标记并在最终报告中列出，不要静默删除行为。
- `data/forge/tags/items/**`（Forge 标签）→ 保留原文件，**并**在 `data/c/tags/items/**` 生成镜像（Fabric 约定命名空间是 `c:`）。配方里引用的 `forge:xxx` 标签保留（与官方一致），但镜像标签里要互相 include，保证两个命名空间都能被第三方模组填充。
- `data/forge/loot_modifiers/global_loot_modifiers.json` → Fabric 上无人读取，改用 `LootTableEvents.MODIFY`（见第 3 节）后**删除该文件**。

## 9. Mixin

- 结构、`@Mixin` 目标、注入点**全部保留**，只改：Forge 专属 API 调用、`ResourceLocation` 写法、`Dist`/`EnvType`。
- mixin 配置文件**不要写 `refmap` 字段**（loom 1.17 静态重映射，refmap 不存在是正常的）。
- `compatibilityLevel` 用 `JAVA_17`。
- MixinExtras（`com.llamalad7.mixinextras.*`）在 Fabric Loader 0.19.5 **已内置**，直接用，不需要额外依赖。
- mixin 目标方法名在 1.20.1 下**同名保留**（同 MC 版本，无需改名）。参考 `_refsources` 里 jar 自带的 `kaleidoscope_chinesefood.refmap.json` 可核对目标。

## 10. 客户端

- `client/ModClientEvents` + `client/ModScreens` 的注册内容 → 合并到新的 `client/KaleidoscopeChineseFoodClient implements ClientModInitializer`，加 `@Environment(EnvType.CLIENT)`。
- 方块实体渲染器注册：`BlockEntityRenderers.register(ModBlockEntities.X, XRender::new)`。
- 实体渲染器：`EntityRenderers.register(ModEntities.X, XRender::new)`。
- 菜单屏幕：`HandledScreens.register(ModMenuTypes.X, XScreen::new)`（`net.fabricmc.fabric.api.client.screenhandler.v1.HandledScreens`）。
- `MenuScreens.register` / `EntityRenderers.register` 在 Fabric 上同名（`net.minecraft.client.gui.screens.MenuScreens` 是 Forge 的；Fabric 用 `HandledScreens`）。
- `RenderType.create(...)`（自定义渲染类型）→ 原版 1.20.1 的 `RenderType.create` 是 **protected/包内可见**，需要 accessWidener 加宽（写进 `src/main/resources/kaleidoscope_chinesefood.accesswidener`）。

## 11. 联动

- **cookery**：硬依赖，`import com.github.ysbbbbbb.kaleidoscopecookery.*` 全部保留（`BowlFoodOnlyItem`/`FoodWithEffectsItem`/`StackableFoodBlock`/`SoupBaseManager`/`ModSoupBases`/`TeapotRecipeSerializer`/`StockpotBlockEntity`/`TeapotBlockEntity`）。**注意**：同版本 Fabric 版 cookery 的 API 可能与 Forge 版**包路径或签名不同**，务必对照 `1.20.1/KaleidoscopeCookery-Refabricated-1.20.1-fabric/src/main/java/` 的实际源码核实后再写。
- **tavern**：软依赖，`TavernMixinConfigPlugin` 守卫保留。同版本 Fabric tavern 的类路径以 `maven.modrinth:kaleidoscope-tavern-refabricated:1.2.0.9-fabric+mc1.20.1` 为准；`ITapBehavior`/`TapBlock`/`ModParticles` 等若有差异，以实际 jar 为准并记录。
- **doll**：软依赖，`kaleidoscope_doll` 的 `DollBlock`/`DollItem`/`DollEntityItem` + 标签 `kaleidoscope_doll:tags/items/{all_dolls,author_dolls,player_dolls}` 保留。
- **create / ponder**：软依赖，`compileOnly`。Ponder 在 Fabric 1.20.1 上由 `create-fabric 6.0.8.1` 提供，包名是 **`net.createmod.ponder.api.*`**（**不是** Forge 的 `net.createmod.ponder.api` 之外的旧包）。参考 cookery 的 `compat/create/ponder/**` 写法。
- **kaleidoscope_contraption**：**没有 Fabric 版本**，其 `KaleidoscopeContraptionCompat` 无法移植 → 用 `isModLoaded` 守卫跳过，并在报告里列为「未移植」。
- **carryon（Carry On）**：Fabric 1.20.1 有 Carry On（`carryon`），若 API 差异大，用守卫跳过并记录。

## 12. 交付要求

- 每个转换后的文件：`package` 声明与目标路径一致；**不要**留下任何 `net.minecraftforge.*` / `net.neoforged.*` 的 import（ForgeConfigSpec 与 ModConfig.Type 例外，它们由 Forge Config API Port 提供）。
- 完成后自检：`grep -rn "net.minecraftforge\|net.neoforged" <你负责的文件>` 只应命中 `ForgeConfigSpec` / `fml.config.ModConfig`。
- 报告：列出你转换的文件、你做的**非机械判断**（尤其是有行为差异的地方）、以及**无法转换/需要人工决定**的清单。
