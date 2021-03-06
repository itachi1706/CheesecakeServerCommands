package com.itachi1706.cheesecakeservercommands.server.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Kenneth on 5/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class ContainerCheatyWorkbench extends ContainerWorkbench {

    private World world;

    public ContainerCheatyWorkbench(InventoryPlayer par1InventoryPlayer, World par2World)
    {
        super(par1InventoryPlayer, par2World, new BlockPos(0,0,0));
        world = par2World;
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        super.onCraftMatrixChanged(par1IInventory);
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);

        if (!world.isRemote)
        {
            for (int var2 = 0; var2 < 9; ++var2)
            {
                ItemStack var3 = craftMatrix.getStackInSlot(var2);

                if (var3 != null)
                {
                    par1EntityPlayer.dropItem(var3, true);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer par1EntityPlayer)
    {
        return true;
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    @Override
    @Nonnull
    public ItemStack transferStackInSlot(@Nonnull EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = ItemStack.EMPTY;
        Slot var4 = inventorySlots.get(par2);

        if (var4 != null && var4.getHasStack())
        {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (par2 == 0)
            {
                if (!mergeItemStack(var5, 10, 46, true))
                {
                    return ItemStack.EMPTY;
                }

                var4.onSlotChange(var5, var3);
            }
            else if (par2 >= 10 && par2 < 37)
            {
                if (!mergeItemStack(var5, 37, 46, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (par2 >= 37 && par2 < 46)
            {
                if (!mergeItemStack(var5, 10, 37, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!mergeItemStack(var5, 10, 46, false))
            {
                return ItemStack.EMPTY;
            }

            if (var5.getCount() == 0)
            {
                var4.putStack(ItemStack.EMPTY);
            }
            else
            {
                var4.onSlotChanged();
            }

            if (var5.getCount() == var3.getCount())
            {
                return ItemStack.EMPTY;
            }

            var4.onTake(par1EntityPlayer, var5);
        }

        return var3;
    }

}
