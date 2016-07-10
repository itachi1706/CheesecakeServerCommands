package com.itachi1706.cheesecakeservercommands.server.commands.util;

import com.itachi1706.cheesecakeservercommands.server.commands.util.CommandsEventHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;

/**
 * Created by Kenneth on 2/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class PlayerInvChest extends InventoryBasic {
    public EntityPlayerMP vieuwer;
    public EntityPlayerMP owner;
    public boolean allowUpdate;

    public PlayerInvChest(EntityPlayerMP owner, EntityPlayerMP vieuwer)
    {
        super(owner.getName() + "'s inventory", false, owner.inventory.mainInventory.length);
        this.owner = owner;
        this.vieuwer = vieuwer;
    }

    @Override
    public void openInventory()
    {
        CommandsEventHandler.register(this);
        allowUpdate = false;
        for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
        {
            setInventorySlotContents(id, owner.inventory.mainInventory[id]);
        }
        allowUpdate = true;
        super.openInventory();
    }

    @Override
    public void closeInventory()
    {
        CommandsEventHandler.remove(this);
        if (allowUpdate)
        {
            for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
            {
                owner.inventory.mainInventory[id] = getStackInSlot(id);
            }
        }
        markDirty();
        super.closeInventory();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if (allowUpdate)
        {
            for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
            {
                owner.inventory.mainInventory[id] = getStackInSlot(id);
            }
        }
    }

    public void update()
    {
        allowUpdate = false;
        for (int id = 0; id < owner.inventory.mainInventory.length; ++id)
        {
            setInventorySlotContents(id, owner.inventory.mainInventory[id]);
        }
        allowUpdate = true;
        markDirty();
    }
}
