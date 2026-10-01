package com.bmt.kaleidoscope_chinesefood.compat.kaleidoscope_contraption;

import com.bmt.kaleidoscope_chinesefood.init.ModCookeryBlocks;
import com.bmt.kaleidoscope_chinesefood.init.ModFoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

/**
 * 放置菜品与竹躺椅与 Kaleidoscope Contraption（Create 附属模组）的兼容：
 * 让水煮鱼/水煮肉片/黄鱼汤/红米卷/黄鱼豆腐汤等大份菜肴方块、竹躺椅
 * 在机械动力装置（列车等）移动时仍可交互/乘坐。
 *
 * 1.1.10 官方版通过 NeoForge ModList.isLoaded 做软依赖检测，直接编译引用 Create 的
 * MovingInteractionBehaviour 与 kaleidoscope_contraption 的行为类。
 * 本端口环境未声明 kaleidoscope_contraption 编译依赖（该模组无 Fabric 构建），
 * 故改用反射调用：仅当 create 与 kaleidoscope_contraption 均加载时才执行，
 * 任一模组缺失则安全跳过，不崩溃。Create 侧注册表包名为 create-fly 的
 * {@code com.zurrtum.create}（不是官方的 {@code com.simibubi.create}）。
 *
 * 官方 1.1.14 新增：对竹躺椅的三行注册（座位高度 0.5 + 移动行为 + 移动交互）。
 * kaleidoscope_contraption 未加载时，长凳的等价功能由
 * {@link com.bmt.kaleidoscope_chinesefood.compat.create.BenchSeatChecks}
 * 通过 create-fly 原生 Seat API 提供（两条路径互斥，见主入口守卫）。
 */
public class KaleidoscopeContraptionCompat {
    public static void register() {
        try {
            Class<?> interactionClass = Class.forName(
                    "com.sshakusora.kaleidoscope_contraption.content.behaviour.interaction.FoodBiteBlockMovingInteraction");
            Constructor<?> ctor = interactionClass.getConstructor();
            Object interaction = ctor.newInstance();

            register(ModFoodBiteRegistry.SICHUAN_BOILED_FISH, interaction);
            register(ModFoodBiteRegistry.SICHUAN_BOILED_PORK_SLICES, interaction);
            register(ModFoodBiteRegistry.YELLOW_CROAKER_SOUP, interaction);
            register(ModFoodBiteRegistry.RED_RICE_ROLL, interaction);
            register(ModFoodBiteRegistry.YELLOW_CROAKER_TOFU_SOUP, interaction);
        } catch (Throwable t) {
            // 静默跳过
        }

        // 官方 1.1.14：竹躺椅三注册（座位高度 0.5 / MovementBehaviour / MovingInteractionBehaviour）
        registerBenchSeatHeight();
        registerBenchMovement();
        registerBenchInteraction();
    }

    /** 官方：CookeryContraptionSeatRegistry.register(bench, state -> 0.5)（SAM 接口用动态代理返回 0.5）。 */
    private static void registerBenchSeatHeight() {
        try {
            Class<?> seatRegistry = Class.forName(
                    "com.sshakusora.kaleidoscope_contraption.api.seat.CookeryContraptionSeatRegistry");
            Block bench = ModCookeryBlocks.STRIPPED_BAMBOO_BENCH;
            for (Method m : seatRegistry.getMethods()) {
                if (!m.getName().equals("register") || m.getParameterCount() != 2) {
                    continue;
                }
                if (!m.getParameterTypes()[0].isInstance(bench) || !m.getParameterTypes()[1].isInterface()) {
                    continue;
                }
                Class<?> sam = m.getParameterTypes()[1];
                Object heightFn = Proxy.newProxyInstance(sam.getClassLoader(), new Class<?>[]{sam},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) {
                                if (method.getReturnType() == double.class || method.getReturnType() == Double.class) {
                                    return 0.5D;
                                }
                                return defaultValue(method.getReturnType());
                            }
                        });
                m.invoke(null, bench, heightFn);
                return;
            }
        } catch (Throwable t) {
            // 静默跳过
        }
    }

    /** 官方：MovementBehaviour.REGISTRY.register(bench, new ChairBlockMovementBehaviour())。 */
    private static void registerBenchMovement() {
        try {
            Class<?> behaviourClass = Class.forName(
                    "com.sshakusora.kaleidoscope_contraption.content.behaviour.movement.ChairBlockMovementBehaviour");
            Object behaviour = behaviourClass.getConstructor().newInstance();
            registerBlockBehaviour(
                    "com.zurrtum.create.api.behaviour.movement.MovementBehaviour",
                    ModCookeryBlocks.STRIPPED_BAMBOO_BENCH, behaviour);
        } catch (Throwable t) {
            // 静默跳过
        }
    }

    /** 官方：MovingInteractionBehaviour.REGISTRY.register(bench, new CookerySeatMovingInteraction())。 */
    private static void registerBenchInteraction() {
        try {
            Class<?> interactionClass = Class.forName(
                    "com.sshakusora.kaleidoscope_contraption.content.behaviour.interaction.CookerySeatMovingInteraction");
            Object interaction = interactionClass.getConstructor().newInstance();
            registerBlockBehaviour(
                    "com.zurrtum.create.api.behaviour.interaction.MovingInteractionBehaviour",
                    ModCookeryBlocks.STRIPPED_BAMBOO_BENCH, interaction);
        } catch (Throwable t) {
            // 静默跳过
        }
    }

    private static void register(Identifier id, Object interaction) {
        if (id == null) {
            return;
        }
        try {
            Block block = FoodBiteRegistry.getBlock(id);
            if (block == null) {
                return;
            }
            registerBlockBehaviour(
                    "com.zurrtum.create.api.behaviour.interaction.MovingInteractionBehaviour", block, interaction);
        } catch (Throwable t) {
            // 静默跳过
        }
    }

    /** BehaviourClass.REGISTRY.register(block, behaviour)——create-fly 的 SimpleRegistry。 */
    private static void registerBlockBehaviour(String behaviourClassName, Block block, Object behaviour) throws Exception {
        Class<?> behaviourClass = Class.forName(behaviourClassName);
        Field registryField = behaviourClass.getField("REGISTRY");
        Object registry = registryField.get(null);
        Method registerMethod = registry.getClass().getMethod("register", Block.class, behaviourClass);
        registerMethod.invoke(registry, block, behaviour);
    }

    private static Object defaultValue(Class<?> type) {
        if (type == boolean.class) {
            return false;
        }
        if (type == byte.class) {
            return (byte) 0;
        }
        if (type == short.class) {
            return (short) 0;
        }
        if (type == int.class) {
            return 0;
        }
        if (type == long.class) {
            return 0L;
        }
        if (type == float.class) {
            return 0F;
        }
        if (type == char.class) {
            return (char) 0;
        }
        return null;
    }
}
