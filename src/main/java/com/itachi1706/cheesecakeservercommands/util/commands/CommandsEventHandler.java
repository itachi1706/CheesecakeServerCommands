package com.itachi1706.cheesecakeservercommands.util.commands;

import net.minecraft.server.level.ServerLevel;

/**
 * Created by Kenneth on 2/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class CommandsEventHandler {
    // public static HashMultimap<Player, PlayerInvChest> map = HashMultimap.create();

    public static int getWorldHour(ServerLevel world)
    {
        return (int) ((world.getGameTime() % 24000) / 1000);
    }

    private static long getWorldDays(ServerLevel world)
    {
        return (int) (world.getGameTime() / 24000);
    }

    public static void makeWorldTimeHours(ServerLevel world, long target)
    {
        world.setDayTime((getWorldDays(world) + 1) * 24000 + (target * 1000));
    }

    // static void register(PlayerInvChest inv)
    // {
    //     map.put(inv.owner, inv);
    // }
    //
    // static void remove(PlayerInvChest inv)
    // {
    //     map.remove(inv.owner, inv);
    // }

    private CommandsEventHandler()
    {
        super();
    }
}
