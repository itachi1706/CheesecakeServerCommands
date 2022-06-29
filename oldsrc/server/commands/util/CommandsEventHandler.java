package com.itachi1706.cheesecakeservercommands.server.commands.util;

import com.google.common.collect.HashMultimap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * Created by Kenneth on 2/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class CommandsEventHandler {
    public static HashMultimap<EntityPlayer, PlayerInvChest> map = HashMultimap.create();

    public static int getWorldHour(World world)
    {
        return (int) ((world.getWorldTime() % 24000) / 1000);
    }

    private static int getWorldDays(World world)
    {
        return (int) (world.getWorldTime() / 24000);
    }

    public static void makeWorldTimeHours(World world, int target)
    {
        world.setWorldTime((getWorldDays(world) + 1) * 24000 + (target * 1000));
    }

    static void register(PlayerInvChest inv)
    {
        map.put(inv.owner, inv);
    }

    static void remove(PlayerInvChest inv)
    {
        map.remove(inv.owner, inv);
    }

    public CommandsEventHandler()
    {
        super();
    }
}
