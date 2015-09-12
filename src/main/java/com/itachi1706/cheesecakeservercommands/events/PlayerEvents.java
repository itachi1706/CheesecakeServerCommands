package com.itachi1706.cheesecakeservercommands.events;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Kenneth on 9/12/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.events
 */
public class PlayerEvents {

    @SubscribeEvent
    public void playerLoginEvent(PlayerEvent.PlayerLoggedInEvent event){
        LogHelper.info(">>> Player Logged In Event Triggered");
        EntityPlayer player = event.player;
        if (player == null)
            return;

        LogHelper.info("Player " + player.getDisplayName() + " with UUID " + player.getUniqueID().toString() + " logged in");
        LastKnownUsernameJsonHelper.logUsernameToList(player);
    }

    @SubscribeEvent
    public void playerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event){
        LogHelper.info(">>> Player Logged Out Event Triggered");
        EntityPlayer player = event.player;
        if (player == null)
            return;

        LogHelper.info("Player " + player.getDisplayName() + " with UUID " + player.getUniqueID().toString() + " logged out");
    }
}
