package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.dbstorage.CommandsLogDB;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 9/7/2018.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
public class CommandUsageCommand implements ICommand {

    private List<String> aliases;

    /* Commands List
    /commandsuse viewlogs <player> <#>
    /commandsuse viewplayerstats <player>
    /commandsuse dellogs <player>
    /commandsuse help
    /commandsuse stats */

    public CommandUsageCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("commanduse");
    }

    @Override
    @Nonnull
    public String getName() {
        return "commanduse";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/commanduse help";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    private void sendHelp(ICommandSender iCommandSender){
        ChatHelper.sendMessage(iCommandSender, "Commands List:");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/commandsuse stats"
                + TextFormatting.AQUA + " Gets General Command Statistics Info");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/commandsuse viewlogs <player> <#>"
                + TextFormatting.AQUA + " View Player Command Usage Info");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/commandsuse viewplayerstats <player>"
                + TextFormatting.AQUA + " View Player Command Usage Stats");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/commandsuse dellogs <player>"
                + TextFormatting.AQUA + " Delete Player Command Usage History");
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) throws CommandException {

        if(args.length == 0) {
            sendHelp(iCommandSender);
            return;
        }

        String subCommand = args[0];
        String playerName;
        switch (subCommand.toLowerCase()) {
            case "viewlogs":
                if (args.length < 2 || args.length > 3) {
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage! Usage: /commandsuse viewlogs <player> <#>");
                    break;
                }
                playerName = args[1];
                if (args.length == 2)
                    CommandsLogDB.checkCommandLogs(iCommandSender, playerName, 1); // Check command logs for first page
                else {
                    // Check command logs for whatever page is passed
                    int value = CommandBase.parseInt(args[2], 1);
                    CommandsLogDB.checkCommandLogs(iCommandSender, playerName, value);
                }
                break;
            case "stats":
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "=====================================================");
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "                           General Stats");
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "=====================================================");
                ChatHelper.sendMessage(iCommandSender, "Total Commands Logged: " + TextFormatting.AQUA + CommandsLogDB.getTotalCount());
                ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "=====================================================");
                break;
            case "viewplayerstats":
                if (args.length != 2) {
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage! Usage: /commandsuse viewplayerstats <player>");
                    break;
                }

                playerName = args[1];
                if (playerName.equalsIgnoreCase("console") || playerName.equalsIgnoreCase("server"))
                    CommandsLogDB.checkCommandStats(iCommandSender, "Server", UUID.randomUUID());

                UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerName);
                if (uuid == null) {
                    notOnServerError(playerName, iCommandSender);
                    break;
                }

                LastKnownUsernames name = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
                if (name == null) {
                    notOnServerError(playerName, iCommandSender);
                    break;
                }

                CommandsLogDB.checkCommandStats(iCommandSender, args[1], uuid);
                break;
            case "dellogs":
                if (args.length != 2) {
                    ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage! Usage: /commandsuse dellogs <player>");
                    break;
                }

                CommandsLogDB.deleteLogs(iCommandSender, args[1]);
                break;
            case "help":
            default: sendHelp(iCommandSender); break;
        }
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, "help", "viewlogs", "viewplayerstats", "dellogs", "stats");
        if (args.length == 2 && (args[0].equalsIgnoreCase("viewplayerstats") || args[0].equalsIgnoreCase("viewlogs") ||
                args[0].equalsIgnoreCase("dellogs"))) {
            String[] names = new String[CheesecakeServerCommands.lastKnownUsernames.size() + 1];
            int i = 0;
            for (LastKnownUsernames name : CheesecakeServerCommands.lastKnownUsernames) {
                names[i] = name.getLastKnownUsername();
                i++;
            }
            names[names.length - 1] = "CONSOLE";
            return CommandBase.getListOfStringsMatchingLastWord(args, names);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private void notOnServerError(String playerName, ICommandSender sender) {
        ChatHelper.sendMessage(sender, TextFormatting.RED + playerName + " has never logged into the server");
    }
}
