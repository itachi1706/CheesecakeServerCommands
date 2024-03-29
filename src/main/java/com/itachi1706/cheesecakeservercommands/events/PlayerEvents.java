package com.itachi1706.cheesecakeservercommands.events;

import com.itachi1706.cheesecakeservercommands.dbstorage.CommandsLogDB;
import com.itachi1706.cheesecakeservercommands.dbstorage.LoginLogoutDB;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

/**
 * Created by Kenneth on 9/12/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.events
 */
@SuppressWarnings("unused")
public class PlayerEvents {

    @SubscribeEvent
    public void playerLoginEvent(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getPlayer();
        if (player == null)
            return;

        LogHelper.info("Player " + player.getDisplayName().getString() + " with UUID " + player.getStringUUID() + " logged in");

        LastKnownUsernameJsonHelper.logUsernameToList(player);
        LastKnownUsernameJsonHelper.logLastSeenToList(player, true);
        LoginLogoutDB.getInstance().addLoginLog(player);
    }

    @SubscribeEvent
    public void playerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event){
        Player player = event.getPlayer();
        if (player == null)
            return;

        LogHelper.info("Player " + player.getDisplayName().getString() + " with UUID " + player.getStringUUID() + " logged out");
        LastKnownUsernameJsonHelper.logLastSeenToList(player, false);
        LoginLogoutDB.getInstance().addLogoutLog(player);
        if (player instanceof ServerPlayer serverPlayer){
            LastKnownUsernameJsonHelper.logGamemodeToLit(serverPlayer);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
       if (NoteblockSongs.isPlaying())
           NoteblockSongs.getPlayer().onTick();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommandUse(CommandEvent event) {
        CommandSourceStack sender = event.getParseResults().getContext().getSource();
        if (ServerUtil.checkIfAdminSilenced(sender)) return; // Don't log admin silenced players

        String commandExecuted = event.getParseResults().getReader().getString();
        String commandBase = commandExecuted.split(" ")[0].substring(1);
        String ip = "localhost";
        String name = sender.getTextName();

        if (ServerUtil.checkIfCommandUseIgnored(name)) return; // Don't log ignored users
        UUID uuid = UUID.randomUUID();
        try {
            ServerPlayer playerMP = sender.getPlayerOrException();
            if (playerMP instanceof FakePlayer fp) {
                name = "FP-" + fp.getClass().getSimpleName();
                ip = "fp-" + fp.getClass().getSimpleName() + "-" + playerMP.getName();
            } else {
                ip = playerMP.getIpAddress();
            }
            uuid = playerMP.getUUID();
        } catch (CommandSyntaxException e) {
            // Not player, ignore
        }

        // Add log should now change to base and full commands and change accordingly
        CommandsLogDB.getInstance().addLog(name, uuid, commandBase, commandExecuted, ip);
    }
}
