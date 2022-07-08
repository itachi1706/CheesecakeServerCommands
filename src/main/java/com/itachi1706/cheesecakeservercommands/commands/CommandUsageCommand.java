package com.itachi1706.cheesecakeservercommands.commands;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.dbstorage.CommandsLogDB;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.nbtstorage.AdminSilenced;
import com.itachi1706.cheesecakeservercommands.nbtstorage.CSCAdminSilenceWorldSavedData;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class CommandUsageCommand extends BaseCommand {
    /* Commands List
    /commanduse viewlogs <player> <#>
    /commanduse viewplayerstats <player>
    /commanduse dellogs <player>
    /commanduse ignore <player>
    /commanduse unignore <player>
    /commanduse help
    /commanduse stats */

    private static final String ARG_PLAYER = "player";
    
    public CommandUsageCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> sendHelpMessages(context.getSource()))
                .then(Commands.literal("viewlogs")
                        .then(Commands.argument(ARG_PLAYER, StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(getLastKnownUsernames(), builder))
                                .executes(context -> viewLogs(context.getSource(), StringArgumentType.getString(context, ARG_PLAYER), 1))
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(context -> viewLogs(context.getSource(), StringArgumentType.getString(context, ARG_PLAYER), IntegerArgumentType.getInteger(context, "page"))))))
                .then(Commands.literal("stats").executes(context -> viewGeneralStats(context.getSource())))
                .then(Commands.literal("viewplayerstats")
                        .then(Commands.argument(ARG_PLAYER, StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(getLastKnownUsernames(), builder))
                                .executes(context -> viewPlayerStats(context.getSource(), StringArgumentType.getString(context, ARG_PLAYER)))))
                .then(Commands.literal("dellogs")
                        .then(Commands.argument(ARG_PLAYER, StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(getLastKnownUsernames(), builder))
                                .executes(context -> deleteLogs(context.getSource(), StringArgumentType.getString(context, ARG_PLAYER)))))
                .then(Commands.literal("ignore")
                        .then(Commands.argument(ARG_PLAYER, StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(getLastKnownUsernames(), builder))
                                .executes(context -> ignoreUser(context.getSource(), StringArgumentType.getString(context, ARG_PLAYER)))))
                .then(Commands.literal("unignore")
                        .then(Commands.argument(ARG_PLAYER, StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(getLastKnownUsernames(), builder))
                                .executes(context -> unignoreUser(context.getSource(), StringArgumentType.getString(context, ARG_PLAYER)))))
                .then(Commands.literal("help").executes(context -> sendHelpMessages(context.getSource())));
    }
    
    private int sendHelpMessages(CommandSourceStack sender) {
        sendHelp(sender);
        return Command.SINGLE_SUCCESS;
    }
    
    private int viewLogs(CommandSourceStack sender, String playerName, int page) {
        CommandsLogDB.getInstance().checkCommandLogs(sender, playerName, page);
        return Command.SINGLE_SUCCESS;
    }
    
    private int viewGeneralStats(CommandSourceStack sender) {
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.centerText("General Stats"));
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, "Total Commands Logged: " + ChatFormatting.AQUA + CommandsLogDB.getInstance().getTotalCount());
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        return Command.SINGLE_SUCCESS;
    }

    private int viewPlayerStats(CommandSourceStack sender, String playerName) {
        if (CommandsLogDB.getInstance().checkIfConsole(playerName)) {
            CommandsLogDB.getInstance().checkCommandStats(sender, "Server", UUID.randomUUID());
        } else {
            UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerName);
            if (uuid != null) {
                LastKnownUsernames name = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
                if (name != null) {
                    CommandsLogDB.getInstance().checkCommandStats(sender, playerName, uuid);
                    return Command.SINGLE_SUCCESS;
                }
            }

            if (CommandsLogDB.getInstance().getPlayerNames().contains(playerName)) {
                sendFailureMessage(sender, "User is not a real player, use \"/commanduse viewlogs " + playerName + "\" to see commands executed under this player");
            } else {
                notOnServerError(playerName, sender);
            }

            return 0;
        }
        return Command.SINGLE_SUCCESS;
    }

    private int deleteLogs(CommandSourceStack sender, String playerName) {
        CommandsLogDB.getInstance().deleteLogs(sender, playerName);
        return Command.SINGLE_SUCCESS;
    }

    private int ignoreUser(CommandSourceStack sender, String playerName) {
        toggleCommandUseException(sender, true, playerName);
        return Command.SINGLE_SUCCESS;
    }

    private int unignoreUser(CommandSourceStack sender, String playerName) {
        toggleCommandUseException(sender, false, playerName);
        return Command.SINGLE_SUCCESS;
    }

    private void toggleCommandUseException(@Nonnull CommandSourceStack sender, boolean ignore, String name) {
        CSCAdminSilenceWorldSavedData data = CSCAdminSilenceWorldSavedData.get();
        // Sanity check
        if ((AdminSilenced.isIgnored(name) && ignore) || (!AdminSilenced.isIgnored(name) && !ignore)) {
            sendFailureMessage(sender, "Command Usage logging for " + name + " is " + ((ignore) ? "" : "not ") + "currently ignored");
            return;
        }

        if (ignore) {
            AdminSilenced.ignoreCommandUser(data, name);
            sendSuccessMessage(sender, ChatFormatting.GREEN + name + " has been added to the command logging ignore list");
        } else {
            AdminSilenced.unignoreCommandUser(data, name);
            sendSuccessMessage(sender, ChatFormatting.GREEN + name + " has been removed from the command logging ignore list");
        }
    }

    private void notOnServerError(String playerName, CommandSourceStack sender) {
        sendFailureMessage(sender, playerName + " has never logged into the server");
    }

    private void sendHelp(CommandSourceStack sender){
        sendSuccessMessage(sender, "Commands List:");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/commanduse stats" + ChatFormatting.AQUA + " Gets General Command Statistics Info");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/commanduse viewlogs <player> <#>" + ChatFormatting.AQUA + " View Player Command Usage Info");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/commanduse viewplayerstats <player>" + ChatFormatting.AQUA + " View Player Command Usage Stats");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/commanduse dellogs <player>" + ChatFormatting.AQUA + " Delete Player Command Usage History");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/commanduse ignore <player>" + ChatFormatting.AQUA + " Exempts name from having its command usage logged");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/commanduse unignore <player>" + ChatFormatting.AQUA + " Reinstate name from having its command usage logged");
    }

    private List<String> getLastKnownUsernames() {
        HashSet<String> names = new HashSet<>();
        for (LastKnownUsernames name : CheesecakeServerCommands.getLastKnownUsernames()) {
            names.add(name.getLastKnownUsername());
        }
        names.add("CONSOLE");
        names.addAll(CommandsLogDB.getInstance().getPlayerNames());
        names.remove("Server"); // It's already in CONSOLE

        return names.stream().toList();
    }
}
