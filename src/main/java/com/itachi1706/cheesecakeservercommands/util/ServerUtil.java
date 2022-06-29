package com.itachi1706.cheesecakeservercommands.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Created by Kenneth on 11/7/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class ServerUtil {

    public static MinecraftServer getServerInstance() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static PlayerList getServerPlayers() {
        return getServerInstance().getPlayerList();
    }

    public static boolean checkIfAdminSilenced(CommandSourceStack sender) {
        // TODO: To implement
        throw new NotImplementedException("To Implement again");
//        if (sender.hasPermission(CommandPermissionsLevel.OPS) && AdminSilenced.getState()) {
//            UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(sender.getName());
//            if (uuid != null && AdminSilenced.contains(uuid)) return true; // Don't send admin message
//            return UUID.fromString(AdminSilenced.MY_UUID).equals(uuid);
//        }
//
//        return false;
    }

    public static boolean checkIfAdminSilenced(ServerPlayer player) {
        // TODO: Code Stub. To Revamp
        throw new NotImplementedException("To Implement again");
//        if (sender instanceof Player && AdminSilenced.getState()) {
//            UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(sender.getName());
//            if (uuid != null && AdminSilenced.contains(uuid)) return true; // Don't send admin message
//            return UUID.fromString(AdminSilenced.MY_UUID).equals(uuid);
//        }
//        return false;
    }

    public static boolean checkIfCommandUseIgnored(String name) {
        // TODO: Code Stub
        throw new NotImplementedException("To Implement again");
//        return AdminSilenced.isIgnored(name);
    }
}
