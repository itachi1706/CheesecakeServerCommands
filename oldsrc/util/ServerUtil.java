package com.itachi1706.cheesecakeservercommands.util;

import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.nbtstorage.AdminSilenced;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

/**
 * Created by Kenneth on 11/7/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class ServerUtil {

    public static MinecraftServer getServerInstance() {
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    public static boolean checkIfAdminSilenced(ICommandSender sender) {
        if (sender instanceof EntityPlayer && AdminSilenced.getState()) {
            UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(sender.getName());
            if (uuid != null && AdminSilenced.contains(uuid)) return true; // Don't send admin message
            return UUID.fromString(AdminSilenced.MY_UUID).equals(uuid);
        }
        return false;
    }

    public static boolean checkIfCommandUseIgnored(String name) {
        return AdminSilenced.isIgnored(name);
    }
}
