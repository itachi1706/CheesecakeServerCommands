package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.dbstorage.LoginLogoutDB;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
public class CCLoggerCommand implements ICommand {

    private List<String> aliases;

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

    public CCLoggerCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("cheesecakelogger");
        this.aliases.add("cclogger");
        this.aliases.add("ccl");
    }

    @Override
    public String getCommandName() {
        return "cheesecakelogger";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "View Command Help: " + EnumChatFormatting.GOLD + "/cheesecakelogger help";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    private void sendHelp(ICommandSender iCommandSender){
        ChatHelper.sendMessage(iCommandSender, "Commands List:");
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "/cclogger stats"
                + EnumChatFormatting.AQUA + " Gets General Statistics Logged.");
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "/cclogger viewlogins <player> <#>"
                + EnumChatFormatting.AQUA + " View Player Login Info");
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "/cclogger viewplayerstats <player>"
                + EnumChatFormatting.AQUA + " View Player Stats");
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "/cclogger delloginhistory <player>"
                + EnumChatFormatting.AQUA + " Delete Player History");
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "/cclogger lastseen <player>"
                + EnumChatFormatting.AQUA + " Gets Last Seen of Player");
        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "/cclogger lastknownusername <player/UUID>"
                + EnumChatFormatting.AQUA + " Get list of last " + EnumChatFormatting.AQUA +
                "known names of a player");
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if(astring.length == 0)
        {
            sendHelp(iCommandSender);
            return;
        }

        String subCommand = astring[0];

        if (subCommand.equalsIgnoreCase("help")){
            sendHelp(iCommandSender);
            return;
        }

        if (subCommand.equalsIgnoreCase("lastknownusername")){
            if (astring.length != 2){
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Usage: /cclogger lastknownusername <player/UUID>");
                return;
            }

            String query = astring[1];
            boolean isUUID = true;
            UUID uid = null;
            try {
                uid = UUID.fromString(query);
            } catch (IllegalArgumentException e){
                //Not UUID
                isUUID = false;
            }

            if (isUUID){
                getListOfKnownUsernames(uid, iCommandSender);
            } else {
                getListOfKnownUsernames(query, iCommandSender);
            }

            return;
        }

        if (subCommand.equalsIgnoreCase("viewlogins")){
            if (astring.length < 2 || astring.length > 3){
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Usage! Usage: /cclogger viewlogins <player> <#>");
                return;
            }

            int value;
            String playerName = astring[1];
            if (astring.length == 2){
                // Check login logs for first page
                LoginLogoutDB.checkLoginLogs(iCommandSender, playerName, 1);
            } else {
                // Check login logs for whatever page is passed
                try {
                    value = Integer.parseInt(astring[2]);
                    if (value == 0){
                        ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Usage. Please specify a number above 0!");
                        return;
                    }

                    LoginLogoutDB.checkLoginLogs(iCommandSender, playerName, value);
                } catch (NumberFormatException e){
                    ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Usage. Please specify a number!");
                    return;
                }
            }
            return;

        }

        if (subCommand.equalsIgnoreCase("delloginhistory")){
            if (astring.length != 2){
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Usage! Usage: /cclogger delloginhistory <player>");
                return;
            }

            LoginLogoutDB.deleteLogs(iCommandSender, astring[1]);
            return;
        }

        if (subCommand.equalsIgnoreCase("viewplayerstats")){
            if (astring.length != 2){
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Usage! Usage: /cclogger viewplayerstats <player>");
                return;
            }

            String playerName = astring[1];
            UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerName);
            if (uuid == null){
                notOnServerError(playerName, iCommandSender);
                return;
            }

            LastKnownUsernames name = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
            if (name == null){
                notOnServerError(playerName, iCommandSender);
                return;
            }

            LoginLogoutDB.checkLoginStats(iCommandSender, astring[1], uuid, convertTime(name.getFirstJoined()), convertTime(name.getLastSeen()));
            return;
        }

        if (subCommand.equalsIgnoreCase("lastseen")){
            if (astring.length != 2){
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Usage! Usage: /cclogger lastseen <player>");
                return;
            }

            String playerName = astring[1];

            UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerName);
            if (uuid == null){
                notOnServerError(playerName, iCommandSender);
                return;
            }
            getLastSeenFromUUID(uuid, iCommandSender, playerName);

            return;
        }

        if (subCommand.equalsIgnoreCase("stats")){
            if (astring.length != 1){
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Usage! Usage: /cclogger stats");
                return;
            }

            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "=====================================================");
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "                           General Stats");
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "=====================================================");
            ChatHelper.sendMessage(iCommandSender, "Total Players Logged: " + EnumChatFormatting.AQUA + CheesecakeServerCommands.lastKnownUsernames.size());
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.GOLD + "=====================================================");
            return;
        }

        sendHelp(iCommandSender);

    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "help", "viewlogins", "viewplayerstats", "delloginhistory", "lastknownusername", "lastseen", "stats");
        if (typedValue.length == 2 && (typedValue[0].equalsIgnoreCase("lastknownusername") || typedValue[0].equalsIgnoreCase("lastseen") ||
                typedValue[0].equalsIgnoreCase("viewplayerstats") || typedValue[0].equalsIgnoreCase("viewlogins") ||
                typedValue[0].equalsIgnoreCase("delloginhistory")))
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        return 0;
    }

    private void notOnServerError(String playerName, ICommandSender sender){
        ChatHelper.sendMessage(sender, EnumChatFormatting.RED + playerName + " has never logged into the server");
    }

    private void getLastSeenFromUUID(UUID uuid, ICommandSender sender, String playerName){
        LastKnownUsernames name = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);
        if (name == null){
            notOnServerError(playerName, sender);
            return;
        }

        List<EntityPlayerMP> onlinePlayers = PlayerMPUtil.getOnlinePlayers();
        for (EntityPlayerMP player : onlinePlayers){
            if (player.getUniqueID().equals(uuid)){
                ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + player.getDisplayName() + EnumChatFormatting.WHITE + " is currently " + EnumChatFormatting.GREEN + "Online");
                return;
            }
        }

        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + name.getLastKnownUsername() + EnumChatFormatting.WHITE + " is last seen on " + EnumChatFormatting.ITALIC + convertTime(name.getLastSeen()));
    }

    private void getListOfKnownUsernames(UUID uid, ICommandSender sender){
        for (LastKnownUsernames u : CheesecakeServerCommands.lastKnownUsernames){
            if (u.getUuid().equals(uid)){
                //Found, send list of usernames to sender
                foundAndTellRequesterAboutKnownUsernames(u, sender);
                return;
            }
        }

        ChatHelper.sendMessage(sender, EnumChatFormatting.RED + "Unable to find any player with UUID of " + uid.toString());
    }

    private void getListOfKnownUsernames(String player, ICommandSender sender){
        for (LastKnownUsernames u : CheesecakeServerCommands.lastKnownUsernames){
            if (u.getLastKnownUsername().equalsIgnoreCase(player)){
                foundAndTellRequesterAboutKnownUsernames(u, sender);
                return;
            }
        }

        ChatHelper.sendMessage(sender, EnumChatFormatting.RED + "Unable to find any player named " + player);
    }

    private void foundAndTellRequesterAboutKnownUsernames(LastKnownUsernames u, ICommandSender sender){
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "=====================================================");
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + " Usernames for " + EnumChatFormatting.WHITE + u.getUuid());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "=====================================================");
        ChatHelper.sendMessage(sender, EnumChatFormatting.WHITE + "Size: " + EnumChatFormatting.AQUA + u.getHistoryOfKnownUsernames().size());
        for (String names : u.getHistoryOfKnownUsernames()){
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + names);
        }
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "=====================================================");
    }

    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z");
        return format.format(date);
    }
}
