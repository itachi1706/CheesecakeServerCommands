package com.itachi1706.cheesecakeservercommands.events;

import com.itachi1706.cheesecakeservercommands.dbstorage.LoginLogoutDB;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created by Kenneth on 9/12/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.events
 */
public class PlayerEvents {

    @SubscribeEvent
    public void playerLoginEvent(PlayerEvent.PlayerLoggedInEvent event){
        EntityPlayer player = event.player;
        if (player == null)
            return;

        LogHelper.info("Player " + player.getDisplayName() + " with UUID " + player.getUniqueID().toString() + " logged in");
        LastKnownUsernameJsonHelper.logUsernameToList(player);
        LastKnownUsernameJsonHelper.logLastSeenToList(player, true);
        LoginLogoutDB.addLoginLog(player);
    }

    @SubscribeEvent
    public void playerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event){
        EntityPlayer player = event.player;
        if (player == null)
            return;

        LogHelper.info("Player " + player.getDisplayName() + " with UUID " + player.getUniqueID().toString() + " logged out");
        LastKnownUsernameJsonHelper.logLastSeenToList(player, false);
        LoginLogoutDB.addLogoutLog(player);
        if (player instanceof EntityPlayerMP){
            LastKnownUsernameJsonHelper.logGamemodeToLit((EntityPlayerMP)player);
        }
    }

    /**
     * Somehow only gets called if client times out, disconnect or stuff (Ignore)
     * When want to use, call @SubscribeEvent
     * @param event The Server Disconnection Event
     */
    public void serverDisconnectFromClientEvent(FMLNetworkEvent.ServerDisconnectionFromClientEvent event){
        LogHelper.info(">>> Server Disconnection From Client Event Called");
        LogHelper.info(">>> " + event.manager.getExitMessage());
        //MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentText("Client DC"));
    }
}
