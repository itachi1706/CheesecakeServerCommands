package com.itachi1706.cheesecakeservercommands.server.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.nbtstorage.AdminSilenced;
import com.itachi1706.cheesecakeservercommands.nbtstorage.CSCAdminSilenceWorldSavedData;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 1/7/2018.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class AdminSilenceCommand {

    public static void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) {
        if (!ServerUtil.getServerInstance().isDedicatedServer()) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "You can only use this command on dedicated servers");
            return;
        }

        if (!checkPermission(server, iCommandSender)) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "You do not have permission to use this command");
            return;
        }

        if (args.length == 1) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /csc adminsilence add/remove/on/off/list [player]");
            return;
        }

        String subCmd = args[1].toLowerCase();
        CSCAdminSilenceWorldSavedData data = CSCAdminSilenceWorldSavedData.get(iCommandSender.getEntityWorld(), true);
        switch (subCmd) {
            case "list":
                List<UUID> uuidList = AdminSilenced.list();
                List<String> chatString = new ArrayList<>();
                chatString.add(TextFormatting.GOLD + "-------------------------------");
                chatString.add("Found " + uuidList.size() + " names");
                chatString.add("Enabled: " + ((AdminSilenced.getState()) ? TextFormatting.GREEN : TextFormatting.RED) +
                        Boolean.toString(AdminSilenced.getState()));
                chatString.add(TextFormatting.GOLD + "-------------------------------");
                for (UUID uuid : uuidList) {
                    LastKnownUsernames tryPName = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
                    if (tryPName == null) chatString.add(uuid.toString());
                    else chatString.add(tryPName.getLastKnownUsername() + " (" + uuid.toString() + ")");
                }
                if (uuidList.size() > 0)
                    chatString.add(TextFormatting.GOLD + "-------------------------------");
                ChatHelper.sendMessage(iCommandSender, chatString);
                break;
            case "on":
                if (AdminSilenced.enable(data)) {
                    // Enable
                    ChatHelper.sendMessage(iCommandSender, "Enabled Admin Message Silence");
                    ChatHelper.sendAdminMessage(iCommandSender, "Enabled Admin Message Silence");
                } else
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Admin Message Silence already enabled");
                break;
            case "off":
                if (AdminSilenced.disable(data)) {
                    // Enable
                    ChatHelper.sendMessage(iCommandSender, "Disabled Admin Message Silence");
                    ChatHelper.sendAdminMessage(iCommandSender, "Disabled Admin Message Silence");
                } else
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Admin Message Silence already disabled");
                break;
            case "add":
                attemptAddOrRemove(true, args, iCommandSender, data); break;
            case "remove":
                attemptAddOrRemove(false, args, iCommandSender, data); break;
        }
    }

    private static void attemptAddOrRemove(boolean toAdd, String[] args, ICommandSender iCommandSender, CSCAdminSilenceWorldSavedData data) {
        if (args.length < 3) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Please define a UUID or player name");
            return;
        }

        String playerNameOrUUID = args[2];
        UUID uuid;
        try {
            uuid = UUID.fromString(playerNameOrUUID);
        } catch (IllegalArgumentException e) {
            // Player Name
            uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerNameOrUUID);
            if (uuid == null) {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Failed to parse UUID from player name");
                return;
            }
        }
        if (toAdd) {
            if (AdminSilenced.addUUID(data, uuid)) {
                ChatHelper.sendMessage(iCommandSender, "Added " + uuid.toString() + " to Admin Silenced List");
                ChatHelper.sendAdminMessage(iCommandSender, "Added " + uuid.toString() + " to Admin Silenced List");
            } else {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player/UUID already in Admin Silenced List");
            }
        } else {
            if (AdminSilenced.removeUUID(data, uuid)) {
                ChatHelper.sendMessage(iCommandSender, "Removed " + uuid.toString() + " to Admin Silenced List");
                ChatHelper.sendAdminMessage(iCommandSender, "Removed " + uuid.toString() + " to Admin Silenced List");
            } else {
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Player/UUID is not in Admin Silenced List");
            }
        }
    }

    public static List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (!checkPermission(server, sender)) return Collections.emptyList();
        if (args.length == 2) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "add", "remove", "on", "off", "list");
        }
        if (args.length == 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        }
        return Collections.emptyList();
    }

    public static boolean checkPermission(MinecraftServer server, ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }
}
