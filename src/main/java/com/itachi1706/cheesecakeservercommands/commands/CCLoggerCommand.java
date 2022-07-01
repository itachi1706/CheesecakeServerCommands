package com.itachi1706.cheesecakeservercommands.commands;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CCLoggerCommand extends BaseCommand {

    /*
    Commands List
    /cheesecakelogger viewlogins <player> <#>
    /cheesecakelogger viewplayerstats <player>
    /cheesecakelogger delloginhistory <player>
    /cheesecakelogger lastknownusername <player/uuid>
    /cheesecakelogger lastseen <player>
    /cheesecakelogger help
    /cheesecakelogger stats
     */

    public CCLoggerCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        final String PLAYER_NAME = "playername";
        return builder
                .then(Commands.literal("help").executes(context -> sendHelp(context.getSource())))
                .then(Commands.literal("test").executes(context -> testCommand(context.getSource())))
                .then(Commands.literal("lastknownusername")
                        .then(Commands.argument("uuidorname", StringArgumentType.string()).suggests((context, builder) -> SharedSuggestionProvider.suggest(getListOfLastKnownUsernames(), builder))
                                .executes(context -> getLastKnownUsername(context.getSource(), StringArgumentType.getString(context, "uuidorname")))))
                .then(Commands.literal("viewlogins")
                        .then(Commands.argument(PLAYER_NAME, StringArgumentType.string()).suggests((context, builder) -> SharedSuggestionProvider.suggest(getListOfLastKnownUsernames(), builder))
                                .executes(context -> getLoginsOfUser(context.getSource(), StringArgumentType.getString(context, PLAYER_NAME)))
                                .then(Commands.argument("number", IntegerArgumentType.integer(1))
                                        .executes(context -> getLoginsOfUser(context.getSource(), StringArgumentType.getString(context, PLAYER_NAME),
                                                IntegerArgumentType.getInteger(context, "number"))))))
                .then(Commands.literal("delloginhistory")
                        .then(Commands.argument(PLAYER_NAME, StringArgumentType.string()).suggests((context, builder) -> SharedSuggestionProvider.suggest(getListOfLastKnownUsernames(), builder))
                                .executes(context -> deleteLoginHistory(context.getSource(), StringArgumentType.getString(context, PLAYER_NAME)))))
                .then(Commands.literal("viewplayerstats")
                        .then(Commands.argument(PLAYER_NAME, StringArgumentType.string()).suggests((context, builder) -> SharedSuggestionProvider.suggest(getListOfLastKnownUsernames(), builder))
                                .executes(context -> viewPlayerStatistics(context.getSource(), StringArgumentType.getString(context, PLAYER_NAME)))))
                .then(Commands.literal("lastseen")
                        .then(Commands.argument(PLAYER_NAME, StringArgumentType.string()).suggests((context, builder) -> SharedSuggestionProvider.suggest(getListOfLastKnownUsernames(), builder))
                        .executes(context -> viewLastSeenOfPlayer(context.getSource(), StringArgumentType.getString(context, PLAYER_NAME)))))
                .then(Commands.literal("stats").executes(context -> viewLoggerStatistics(context.getSource())));
    }

    private String[] getListOfLastKnownUsernames() {
        String[] names = new String[CheesecakeServerCommands.getLastKnownUsernames().size()];
        int i = 0;
        for (LastKnownUsernames name : CheesecakeServerCommands.getLastKnownUsernames()) {
            names[i] = name.getLastKnownUsername();
            i++;
        }
        return names;
    }

    private int sendHelp(CommandSourceStack sender) {
        sendSuccessMessage(sender, "Commands List:");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/cclogger stats"
                + ChatFormatting.AQUA + " Gets General Statistics Logged.");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/cclogger viewlogins <player> <#>"
                + ChatFormatting.AQUA + " View Player Login Info");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/cclogger viewplayerstats <player>"
                + ChatFormatting.AQUA + " View Player Stats");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/cclogger delloginhistory <player>"
                + ChatFormatting.AQUA + " Delete Player History");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/cclogger lastseen <player>"
                + ChatFormatting.AQUA + " Gets Last Seen of Player");
        sendSuccessMessage(sender, ChatFormatting.GOLD + "/cclogger lastknownusername <player/UUID>"
                + ChatFormatting.AQUA + " Get list of last " + ChatFormatting.AQUA +
                "known names of a player");

        return Command.SINGLE_SUCCESS;
    }

    private int testCommand(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "You must be a player to use this command!");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        assert player != null; // Validated above already
        player.getAbilities().mayfly = !player.getAbilities().mayfly;
        if (!player.isOnGround()) {
            player.getAbilities().flying = player.getAbilities().mayfly;
        }
        player.onUpdateAbilities();

        return Command.SINGLE_SUCCESS;
    }

    private int getLastKnownUsername(CommandSourceStack sender, String playerNameOrUuid) {
        boolean isUUID = true;
        UUID uid = null;
        try {
            uid = UUID.fromString(playerNameOrUuid);
        } catch (IllegalArgumentException e){
            //Not UUID
            isUUID = false;
        }

        if (isUUID){
            getListOfKnownUsernames(uid, sender);
        } else {
            getListOfKnownUsernames(playerNameOrUuid, sender);
        }

        return Command.SINGLE_SUCCESS;
    }

    private static final String COMING_SOON = "Feature is being reimplemented and will return soon";

    private int getLoginsOfUser(CommandSourceStack sender, String playerName) {
        return getLoginsOfUser(sender, playerName, 1);
    }

    private int getLoginsOfUser(CommandSourceStack sender, String playerName, int count) {
        // TODO: To Implement
        // LoginLogoutDB.checkLoginLogs(sender, playerName, count);

        // Temp command
        sendFailureMessage(sender, COMING_SOON);

        return Command.SINGLE_SUCCESS;
    }

    private int deleteLoginHistory(CommandSourceStack sender, String playerName) {
        // TODO: To implement
        // LoginLogoutDB.deleteLogs(sender, playerName);

        sendFailureMessage(sender, COMING_SOON);

        return Command.SINGLE_SUCCESS;
    }

    private int viewPlayerStatistics(CommandSourceStack sender, String playerName) {
        // TODO: To implement
        UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerName);
        if (uuid == null){
            notOnServerError(playerName, sender);
            return 0;
        }

        LastKnownUsernames name = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
        if (name == null){
            notOnServerError(playerName, sender);
            return 0;
        }

        // LoginLogoutDB.checkLoginStats(sender, playerName, uuid, convertTime(name.getFirstJoined()), convertTime(name.getLastSeen()));

        sendFailureMessage(sender, COMING_SOON);

        return Command.SINGLE_SUCCESS;
    }

    private int viewLastSeenOfPlayer(CommandSourceStack sender, String playerName) {
        UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerName);
        if (uuid == null){
            notOnServerError(playerName, sender);
            return 0;
        }
        getLastSeenFromUUID(uuid, sender, playerName);
        
        return Command.SINGLE_SUCCESS;
    }
    
    private int viewLoggerStatistics(CommandSourceStack sender) {
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.centerText("General Stats"));
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, "Total Players Logged: " + ChatFormatting.AQUA + CheesecakeServerCommands.getLastKnownUsernames().size());
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        
        return Command.SINGLE_SUCCESS;
    }
    
    // Utility classes
    private void notOnServerError(String playerName, CommandSourceStack sender){
        sendFailureMessage(sender, ChatFormatting.RED + playerName + " has never logged into the server");
    }

    private void getLastSeenFromUUID(UUID uuid, CommandSourceStack sender, String playerName){
        LastKnownUsernames name = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
        if (name == null){
            notOnServerError(playerName, sender);
            return;
        }

        List<ServerPlayer> onlinePlayers = ServerPlayerUtil.getOnlinePlayers();
        for (ServerPlayer player : onlinePlayers){
            if (player.getUUID().equals(uuid)){
                sendSuccessMessage(sender, ChatFormatting.GOLD + player.getDisplayName().getString() + ChatFormatting.WHITE + " is currently " + ChatFormatting.GREEN + "Online");
                return;
            }
        }

        sendSuccessMessage(sender, ChatFormatting.GOLD + name.getLastKnownUsername() + ChatFormatting.WHITE + " is last seen on " + ChatFormatting.ITALIC + convertTime(name.getLastSeen()));
    }

    private void getListOfKnownUsernames(UUID uid, CommandSourceStack sender){
        for (LastKnownUsernames u : CheesecakeServerCommands.getLastKnownUsernames()){
            if (u.getUuid().equals(uid)){
                //Found, send list of usernames to sender
                foundAndTellRequesterAboutKnownUsernames(u, sender);
                return;
            }
        }

        sendSuccessMessage(sender, ChatFormatting.RED + "Unable to find any player with UUID of " + uid.toString());
    }

    private void getListOfKnownUsernames(String player, CommandSourceStack sender){
        for (LastKnownUsernames u : CheesecakeServerCommands.getLastKnownUsernames()){
            if (u.getLastKnownUsername().equalsIgnoreCase(player)){
                foundAndTellRequesterAboutKnownUsernames(u, sender);
                return;
            }
        }

        sendSuccessMessage(sender, ChatFormatting.RED + "Unable to find any player named " + player);
    }

    private void foundAndTellRequesterAboutKnownUsernames(LastKnownUsernames u, CommandSourceStack sender){
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.centerText("Usernames for " + ChatFormatting.WHITE + u.getUuid()));
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, ChatFormatting.WHITE + "Size: " + ChatFormatting.AQUA + u.getHistoryOfKnownUsernames().size());
        for (String names : u.getHistoryOfKnownUsernames()){
            sendSuccessMessage(sender, ChatFormatting.GOLD + names);
        }
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
    }

    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z");
        return format.format(date);
    }
}
