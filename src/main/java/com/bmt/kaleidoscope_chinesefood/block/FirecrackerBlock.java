package com.bmt.kaleidoscope_chinesefood.block;

import com.bmt.kaleidoscope_chinesefood.block.entity.FirecrackerBlockEntity;
import com.bmt.kaleidoscope_chinesefood.entity.FirecrackerEntity;
import com.bmt.kaleidoscope_chinesefood.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FirecrackerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Plane.HORIZONTAL);
    protected static final VoxelShape SHAPE = box(6.0, 0.0, 6.0, 10.0, 8.0, 10.0);

    /** 1.2.0：发射器射出鞭炮投掷物（对齐官方 CommonRegistry 注册）。 */
    public static void registerDispenserBehavior() {
        net.minecraft.world.level.block.DispenserBlock.registerBehavior(
            com.bmt.kaleidoscope_chinesefood.init.ModItems.FIRECRACKER,
            new net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior() {
                @Override
                protected net.minecraft.world.entity.projectile.Projectile getProjectile(
                    net.minecraft.world.level.Level level, net.minecraft.core.Position position, ItemStack stack
                ) {
                    FirecrackerEntity firecracker = net.minecraft.Util.make(
                        new FirecrackerEntity(level, position.x(), position.y(), position.z()), e -> e.setItem(stack)
                    );
                    return firecracker;
                }
            }
        );
    }

    public FirecrackerBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return (BlockState)this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        pBuilder.add(new Property[]{FACING});
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        // 官方 1.1.16：火焰弹也可点燃（消耗 1 个，创造免耗，音效用火弹），打火石照旧扣耐久
        if (!itemstack.is(Items.FLINT_AND_STEEL) && !itemstack.is(Items.FIRE_CHARGE)) {
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        } else {
            pLevel.playSound(
                pPlayer, pPos, itemstack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F
            );
            if (!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof FirecrackerBlockEntity firecrackerBE) {
                firecrackerBE.ignite();
            }

            if (itemstack.is(Items.FIRE_CHARGE)) {
                itemstack.shrink(1);
            } else {
                itemstack.hurtAndBreak(1, pPlayer, p -> p.broadcastBreakEvent(pHand));
            }

            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModBlockEntities.FIRECRACKER, FirecrackerBlockEntity::tick);
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new FirecrackerBlockEntity(pPos, pState);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @NotNull
    public BlockState rotate(@NotNull BlockState state, @NotNull Rotation rot) {
        return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
    }

    @NotNull
    public BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
    }
}
