package com.itachi1706.cheesecakeservercommands.util;

import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.nbtstorage.AdminSilenced;
import com.itachi1706.cheesecakeservercommands.reference.CommandPermissionsLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

/**
 * Created by Kenneth on 11/7/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class ServerUtil {

    private ServerUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static MinecraftServer getServerInstance() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static DedicatedServer getDedicatedServerInstance() {
        return (DedicatedServer) getServerInstance();
    }

    public static PlayerList getServerPlayers() {
        return getServerInstance().getPlayerList();
    }

    public static boolean checkIfAdminSilenced(CommandSourceStack sender) {
       if (sender.hasPermission(CommandPermissionsLevel.OPS) && AdminSilenced.getState()) {
           UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(sender.getTextName());
           if (uuid != null && AdminSilenced.contains(uuid)) return true; // Don't send admin message
           return UUID.fromString(AdminSilenced.MY_UUID).equals(uuid);
       }

       return false;
    }

    public static boolean checkIfAdminSilenced(ServerPlayer player) {
       if (player != null && AdminSilenced.getState()) {
           UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(player.getName().getString());
           if (uuid != null && AdminSilenced.contains(uuid)) return true; // Don't send admin message
           return UUID.fromString(AdminSilenced.MY_UUID).equals(uuid);
       }
       return false;
    }

    public static boolean checkIfCommandUseIgnored(String name) {
       return AdminSilenced.isIgnored(name);
    }
}
