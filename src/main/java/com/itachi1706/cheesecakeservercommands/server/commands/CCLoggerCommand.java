package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.dbstorage.LoginLogoutDB;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
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
        return "View Command Help: " + TextFormatting.GOLD + "/cheesecakelogger help";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    private void sendHelp(ICommandSender iCommandSender){
        ChatHelper.sendMessage(iCommandSender, "Commands List:");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/cclogger stats"
                + TextFormatting.AQUA + " Gets General Statistics Logged.");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/cclogger viewlogins <player> <#>"
                + TextFormatting.AQUA + " View Player Login Info");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/cclogger viewplayerstats <player>"
                + TextFormatting.AQUA + " View Player Stats");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/cclogger delloginhistory <player>"
                + TextFormatting.AQUA + " Delete Player History");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/cclogger lastseen <player>"
                + TextFormatting.AQUA + " Gets Last Seen of Player");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/cclogger lastknownusername <player/UUID>"
                + TextFormatting.AQUA + " Get list of last " + TextFormatting.AQUA +
                "known names of a player");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        if(args.length == 0)
        {
            sendHelp(iCommandSender);
            return;
        }

        String subCommand = args[0];

        if (subCommand.equalsIgnoreCase("help")){
            sendHelp(iCommandSender);
            return;
        }

        if (subCommand.equalsIgnoreCase("test")){
            EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
            player.capabilities.allowFlying = !player.capabilities.allowFlying;
            if (!player.onGround)
                player.capabilities.isFlying = player.capabilities.allowFlying;
            player.sendPlayerAbilities();
            return;
        }

        if (subCommand.equalsIgnoreCase("lastknownusername")){
            if (args.length != 2){
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage: /cclogger lastknownusername <player/UUID>");
                return;
            }

            String query = args[1];
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
            if (args.length < 2 || args.length > 3){
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage! Usage: /cclogger viewlogins <player> <#>");
                return;
            }

            int value;
            String playerName = args[1];
            if (args.length == 2){
                // Check login logs for first page
                LoginLogoutDB.checkLoginLogs(iCommandSender, playerName, 1);
            } else {
                // Check login logs for whatever page is passed
                value = CommandBase.parseInt(args[2], 1);
                LoginLogoutDB.checkLoginLogs(iCommandSender, playerName, value);
            }
            return;

        }

        if (subCommand.equalsIgnoreCase("delloginhistory")){
            if (args.length != 2){
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage! Usage: /cclogger delloginhistory <player>");
                return;
            }

            LoginLogoutDB.deleteLogs(iCommandSender, args[1]);
            return;
        }

        if (subCommand.equalsIgnoreCase("viewplayerstats")){
            if (args.length != 2){
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage! Usage: /cclogger viewplayerstats <player>");
                return;
            }

            String playerName = args[1];
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

            LoginLogoutDB.checkLoginStats(iCommandSender, args[1], uuid, convertTime(name.getFirstJoined()), convertTime(name.getLastSeen()));
            return;
        }

        if (subCommand.equalsIgnoreCase("lastseen")){
            if (args.length != 2){
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage! Usage: /cclogger lastseen <player>");
                return;
            }

            String playerName = args[1];

            UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerName);
            if (uuid == null){
                notOnServerError(playerName, iCommandSender);
                return;
            }
            getLastSeenFromUUID(uuid, iCommandSender, playerName);

            return;
        }

        if (subCommand.equalsIgnoreCase("stats")){
            if (args.length != 1){
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage! Usage: /cclogger stats");
                return;
            }

            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "=====================================================");
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "                           General Stats");
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "=====================================================");
            ChatHelper.sendMessage(iCommandSender, "Total Players Logged: " + TextFormatting.AQUA + CheesecakeServerCommands.lastKnownUsernames.size());
            ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "=====================================================");
            return;
        }

        sendHelp(iCommandSender);

    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "help", "viewlogins", "viewplayerstats", "delloginhistory", "lastknownusername", "lastseen", "stats");
        if (typedValue.length == 2 && (typedValue[0].equalsIgnoreCase("lastknownusername") || typedValue[0].equalsIgnoreCase("lastseen") ||
                typedValue[0].equalsIgnoreCase("viewplayerstats") || typedValue[0].equalsIgnoreCase("viewlogins") ||
                typedValue[0].equalsIgnoreCase("delloginhistory"))) {
            String[] names = new String[CheesecakeServerCommands.lastKnownUsernames.size()];
            int i = 0;
            for (LastKnownUsernames name : CheesecakeServerCommands.lastKnownUsernames) {
                names[i] = name.getLastKnownUsername();
                i++;
            }
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, names);
        }

        return null;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private void notOnServerError(String playerName, ICommandSender sender){
        ChatHelper.sendMessage(sender, TextFormatting.RED + playerName + " has never logged into the server");
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
                ChatHelper.sendMessage(sender, TextFormatting.GOLD + player.getDisplayNameString() + TextFormatting.WHITE + " is currently " + TextFormatting.GREEN + "Online");
                return;
            }
        }

        ChatHelper.sendMessage(sender, TextFormatting.GOLD + name.getLastKnownUsername() + TextFormatting.WHITE + " is last seen on " + TextFormatting.ITALIC + convertTime(name.getLastSeen()));
    }

    private void getListOfKnownUsernames(UUID uid, ICommandSender sender){
        for (LastKnownUsernames u : CheesecakeServerCommands.lastKnownUsernames){
            if (u.getUuid().equals(uid)){
                //Found, send list of usernames to sender
                foundAndTellRequesterAboutKnownUsernames(u, sender);
                return;
            }
        }

        ChatHelper.sendMessage(sender, TextFormatting.RED + "Unable to find any player with UUID of " + uid.toString());
    }

    private void getListOfKnownUsernames(String player, ICommandSender sender){
        for (LastKnownUsernames u : CheesecakeServerCommands.lastKnownUsernames){
            if (u.getLastKnownUsername().equalsIgnoreCase(player)){
                foundAndTellRequesterAboutKnownUsernames(u, sender);
                return;
            }
        }

        ChatHelper.sendMessage(sender, TextFormatting.RED + "Unable to find any player named " + player);
    }

    private void foundAndTellRequesterAboutKnownUsernames(LastKnownUsernames u, ICommandSender sender){
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "=====================================================");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + " Usernames for " + TextFormatting.WHITE + u.getUuid());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "=====================================================");
        ChatHelper.sendMessage(sender, TextFormatting.WHITE + "Size: " + TextFormatting.AQUA + u.getHistoryOfKnownUsernames().size());
        for (String names : u.getHistoryOfKnownUsernames()){
            ChatHelper.sendMessage(sender, TextFormatting.GOLD + names);
        }
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "=====================================================");
    }

    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z");
        return format.format(date);
    }
}
