package com.itachi1706.cheesecakeservercommands.events;

import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.noteblocksongs.NoteblockSongs;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
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
        // TODO: Reimplement
//        LoginLogoutDB.addLoginLog(player);
    }

    @SubscribeEvent
    public void playerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event){
        Player player = event.getPlayer();
        if (player == null)
            return;

        LogHelper.info("Player " + player.getDisplayName().toString() + " with UUID " + player.getStringUUID() + " logged out");
        LastKnownUsernameJsonHelper.logLastSeenToList(player, false);
        // TODO: Reimplement
//        LoginLogoutDB.addLogoutLog(player);
        if (player instanceof ServerPlayer){
            LastKnownUsernameJsonHelper.logGamemodeToLit((ServerPlayer)player);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
       if (NoteblockSongs.playing)
           NoteblockSongs.player.onTick();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCommandUse(CommandEvent event) {
        CommandSourceStack sender = event.getParseResults().getContext().getSource();
        // TODO: Reimplement
//        if (ServerUtil.checkIfAdminSilenced(sender)) return; // Don't log admin silenced players

        String commandExecuted = event.getParseResults().getReader().getString();
        String commandBase = commandExecuted.split(" ")[0].substring(1);
        String ip = "localhost";
        String name = sender.getTextName();

        // TODO: Reimplement
//        if (ServerUtil.checkIfCommandUseIgnored(name)) return; // Don't log ignored users
        UUID uuid = UUID.randomUUID();
        try {
            ServerPlayer playerMP = sender.getPlayerOrException();
            if (playerMP instanceof FakePlayer) {
                FakePlayer fp = (FakePlayer) playerMP;
                name = "FP-" + fp.getClass().getSimpleName();
                ip = "fp-" + fp.getClass().getSimpleName() + "-" + playerMP.getName();
            } else {
                ip = playerMP.getIpAddress();
            }
            uuid = playerMP.getUUID();
        } catch (CommandSyntaxException e) {
            // Not player, ignore
        }

        // TODO: Reimplement
        // Add log should now change to base and full commands and change accordingly
//        CommandsLogDB.addLog(name, uuid, commandBase.getName(), args, ip);
    }
}
