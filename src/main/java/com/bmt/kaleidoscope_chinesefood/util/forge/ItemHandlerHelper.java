package com.bmt.kaleidoscope_chinesefood.util.forge;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemHandlerHelper
{
    @NotNull
    public static ItemStack insertItem(IItemHandler dest, @NotNull ItemStack stack, boolean simulate)
    {
        if (dest == null || stack.isEmpty())
            return stack;

        for (int i = 0; i < dest.getSlots(); i++)
        {
            stack = dest.insertItem(i, stack, simulate);
            if (stack.isEmpty())
            {
                return ItemStack.EMPTY;
            }
        }

        return stack;
    }

    public static boolean canItemStacksStack(@NotNull ItemStack a, @NotNull ItemStack b)
    {
        if (a.isEmpty() || !ItemStack.isSameItem(a, b) || a.hasTag() != b.hasTag())
            return false;

        return (!a.hasTag() || a.getTag().equals(b.getTag()));
    }

    public static boolean canItemStacksStackRelaxed(@NotNull ItemStack a, @NotNull ItemStack b)
    {
        if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem())
            return false;

        if (!a.isStackable())
            return false;

        if (a.hasTag() != b.hasTag())
            return false;

        return (!a.hasTag() || a.getTag().equals(b.getTag()));
    }

    @NotNull
    public static ItemStack copyStackWithSize(@NotNull ItemStack itemStack, int size)
    {
        if (size == 0)
            return ItemStack.EMPTY;
        ItemStack copy = itemStack.copy();
        copy.setCount(size);
        return copy;
    }


    @NotNull
    public static ItemStack insertItemStacked(IItemHandler inventory, @NotNull ItemStack stack, boolean simulate)
    {
        if (inventory == null || stack.isEmpty())
            return stack;


        if (!stack.isStackable())
        {
            return insertItem(inventory, stack, simulate);
        }

        int sizeInventory = inventory.getSlots();


        for (int i = 0; i < sizeInventory; i++)
        {
            ItemStack slot = inventory.getStackInSlot(i);
            if (canItemStacksStackRelaxed(slot, stack))
            {
                stack = inventory.insertItem(i, stack, simulate);

                if (stack.isEmpty())
                {
                    break;
                }
            }
        }


        if (!stack.isEmpty())
        {

            for (int i = 0; i < sizeInventory; i++)
            {
                if (inventory.getStackInSlot(i).isEmpty())
                {
                    stack = inventory.insertItem(i, stack, simulate);
                    if (stack.isEmpty())
                    {
                        break;
                    }
                }
            }
        }

        return stack;
    }


    public static void giveItemToPlayer(Player player, @NotNull ItemStack stack) {
        giveItemToPlayer(player, stack, -1);
    }


    public static void giveItemToPlayer(Player player, @NotNull ItemStack stack, int preferredSlot)
    {
        if (stack.isEmpty()) return;

        IItemHandler inventory = wrapPlayerInventory(player.getInventory());
        Level level = player.level();


        ItemStack remainder = stack;

        if (preferredSlot >= 0 && preferredSlot < inventory.getSlots())
        {
            remainder = inventory.insertItem(preferredSlot, stack, false);
        }

        if (!remainder.isEmpty())
        {
            remainder = insertItemStacked(inventory, remainder, false);
        }


        if (remainder.isEmpty() || remainder.getCount() != stack.getCount())
        {
            level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }


        if (!remainder.isEmpty() && !level.isClientSide)
        {
            ItemEntity entityitem = new ItemEntity(level, player.getX(), player.getY() + 0.5, player.getZ(), remainder);
            entityitem.setPickUpDelay(40);
            entityitem.setDeltaMovement(entityitem.getDeltaMovement().multiply(0, 1, 0));

            level.addFreshEntity(entityitem);
        }
    }


    public static int calcRedstoneFromInventory(@Nullable IItemHandler inv)
    {
        if (inv == null)
        {
            return 0;
        }
        else
        {
            int itemsFound = 0;
            float proportion = 0.0F;

            for (int j = 0; j < inv.getSlots(); ++j)
            {
                ItemStack itemstack = inv.getStackInSlot(j);

                if (!itemstack.isEmpty())
                {
                    proportion += (float)itemstack.getCount() / (float)Math.min(inv.getSlotLimit(j), itemstack.getMaxStackSize());
                    ++itemsFound;
                }
            }

            proportion = proportion / (float)inv.getSlots();
            return Mth.floor(proportion * 14.0F) + (itemsFound > 0 ? 1 : 0);
        }
    }

    // Fabric：cookery 的 PlayerMainInvWrapper / RangedWrapper / InvWrapper 未随本工程复制，
    // 这里用等价的匿名 IItemHandler 直接包装玩家背包（仅少了 PlayerMainInvWrapper 里的物品栏弹跳动效）
    private static IItemHandler wrapPlayerInventory(Inventory inv)
    {
        return new IItemHandler()
        {
            @Override
            public int getSlots()
            {
                return inv.getContainerSize();
            }

            @Override
            @NotNull
            public ItemStack getStackInSlot(int slot)
            {
                return inv.getItem(slot);
            }

            @Override
            @NotNull
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
            {
                if (stack.isEmpty() || !inv.canPlaceItem(slot, stack))
                    return stack;

                ItemStack existing = inv.getItem(slot);

                int limit = Math.min(inv.getMaxStackSize(), stack.getMaxStackSize());

                if (!existing.isEmpty())
                {
                    if (!canItemStacksStack(stack, existing))
                        return stack;

                    limit -= existing.getCount();
                }

                if (limit <= 0)
                    return stack;

                boolean reachedLimit = stack.getCount() > limit;

                if (!simulate)
                {
                    if (existing.isEmpty())
                    {
                        inv.setItem(slot, reachedLimit ? copyStackWithSize(stack, limit) : stack.copy());
                    }
                    else
                    {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }
                    inv.setChanged();
                }

                return reachedLimit ? copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
            }

            @Override
            @NotNull
            public ItemStack extractItem(int slot, int amount, boolean simulate)
            {
                ItemStack existing = inv.getItem(slot);
                if (existing.isEmpty() || amount <= 0)
                    return ItemStack.EMPTY;

                int toExtract = Math.min(amount, existing.getCount());
                ItemStack result = copyStackWithSize(existing, toExtract);

                if (!simulate)
                {
                    existing.shrink(toExtract);
                    inv.setItem(slot, existing.isEmpty() ? ItemStack.EMPTY : existing);
                    inv.setChanged();
                }

                return result;
            }

            @Override
            public int getSlotLimit(int slot)
            {
                return inv.getMaxStackSize();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack)
            {
                return inv.canPlaceItem(slot, stack);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack)
            {
                inv.setItem(slot, stack);
                inv.setChanged();
            }
        };
    }
}
