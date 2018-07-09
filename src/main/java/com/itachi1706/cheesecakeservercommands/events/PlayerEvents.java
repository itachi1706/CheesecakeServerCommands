package com.itachi1706.cheesecakeservercommands.events;

import com.itachi1706.cheesecakeservercommands.dbstorage.CommandsLogDB;
import com.itachi1706.cheesecakeservercommands.dbstorage.LoginLogoutDB;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.UUID;

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

        LogHelper.info("Player " + player.getDisplayNameString() + " with UUID " + player.getUniqueID().toString() + " logged in");
        LastKnownUsernameJsonHelper.logUsernameToList(player);
        LastKnownUsernameJsonHelper.logLastSeenToList(player, true);
        LoginLogoutDB.addLoginLog(player);
    }

    @SubscribeEvent
    public void playerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event){
        EntityPlayer player = event.player;
        if (player == null)
            return;

        LogHelper.info("Player " + player.getDisplayNameString() + " with UUID " + player.getUniqueID().toString() + " logged out");
        LastKnownUsernameJsonHelper.logLastSeenToList(player, false);
        LoginLogoutDB.addLogoutLog(player);
        if (player instanceof EntityPlayerMP){
            LastKnownUsernameJsonHelper.logGamemodeToLit((EntityPlayerMP)player);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (NoteblockSongs.playing)
            NoteblockSongs.player.onTick();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommandUse(CommandEvent event) {
        ICommandSender sender = event.getSender();
        ICommand commandBase = event.getCommand();
        String[] args = event.getParameters();
        String ip = "localhost";
        String name = sender.getDisplayName().getFormattedText();
        UUID uuid = UUID.randomUUID();
        if (sender instanceof EntityPlayerMP) {
            ip = ((EntityPlayerMP) sender).getPlayerIP();
            uuid = ((EntityPlayerMP) sender).getUniqueID();
        }
        CommandsLogDB.addLog(name, uuid, commandBase.getName(), args, ip);
    }

    /**
     * Somehow only gets called if client times out, disconnect or stuff (Ignore)
     * When want to use, call @SubscribeEvent
     * @param event The Server Disconnection Event
     */
    public void serverDisconnectFromClientEvent(FMLNetworkEvent.ServerDisconnectionFromClientEvent event){
        LogHelper.info(">>> Server Disconnection From Client Event Called");
        LogHelper.info(">>> " + event.getManager().getExitMessage());
        //PlayerMPUtil.getServerInstance().getPlayerList().sendChatMsg(new TextComponentString("Client DC"));
    }
}
