package com.itachi1706.cheesecakeservercommands.server.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;

/**
 * Created by Kenneth on 2/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class PlayerInvChest extends InventoryBasic {
    public EntityPlayerMP viewer;
    public EntityPlayerMP owner;
    private boolean allowUpdate;

    public PlayerInvChest(EntityPlayerMP owner, EntityPlayerMP viewer)
    {
        super(owner.getName() + "'s inventory", false, owner.inventory.mainInventory.size());
        this.owner = owner;
        this.viewer = viewer;
    }



    @Override
    public void openInventory(EntityPlayer player)
    {
        CommandsEventHandler.register(this);
        allowUpdate = false;
        for (int id = 0; id < owner.inventory.mainInventory.size(); ++id)
        {
            setInventorySlotContents(id, owner.inventory.mainInventory.get(id));
        }
        allowUpdate = true;
        super.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        CommandsEventHandler.remove(this);
        if (allowUpdate)
        {
            for (int id = 0; id < owner.inventory.mainInventory.size(); ++id)
            {
                owner.inventory.mainInventory.set(id, getStackInSlot(id));
            }
        }
        markDirty();
        super.closeInventory(player);
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if (allowUpdate)
        {
            for (int id = 0; id < owner.inventory.mainInventory.size(); ++id)
            {
                owner.inventory.mainInventory.set(id, getStackInSlot(id));
            }
        }
    }

    public void update()
    {
        allowUpdate = false;
        for (int id = 0; id < owner.inventory.mainInventory.size(); ++id)
        {
            setInventorySlotContents(id, owner.inventory.mainInventory.get(id));
        }
        allowUpdate = true;
        markDirty();
    }
}
