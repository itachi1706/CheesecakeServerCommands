package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernames;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.storage.SaveHandler;

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
    /cheesecakelogger viewplayerstats <player> <#>
    /cheesecakelogger delloginhistory <player>
    /cheesecakelogger lastknownusername <player/uuid>
    /cheesecakelogger lastseen <player>
    /cheesecakelogger help
     */

    public CCLoggerCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("cheesecakelogger");
        this.aliases.add("cclogger");
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
        iCommandSender.addChatMessage(new ChatComponentText("Commands List:"));
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/cclogger viewlogins <player> <#>"
                + EnumChatFormatting.AQUA + " View Player Login Info"));
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/cclogger viewplayerstats <player> <#>"
                + EnumChatFormatting.AQUA + " View Player Stats"));
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/cclogger delloginhistory <player>"
                + EnumChatFormatting.AQUA + " Delete Player History"));
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/cclogger lastseen <player>"
                + EnumChatFormatting.AQUA + " Gets Last Seen of Player"));
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/cclogger lastknownusername <player/UUID>"
                + EnumChatFormatting.AQUA + " Get list of last " + EnumChatFormatting.AQUA +
                "known names of a player"));
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
                iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid Usage: /cclogger lastknownusername <player/UUID>"));
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
            //TODO: View Logins
        }

        if (subCommand.equalsIgnoreCase("delloginhistory")){
            //TODO: Delete login history
        }

        if (subCommand.equalsIgnoreCase("viewplayerstats")){
            //TODO: View Player Stats

        }

        if (subCommand.equalsIgnoreCase("lastseen")){
            if (astring.length != 2){
                iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid Usage! Usage: /lastseen <player>"));
                return;
            }

            String playerName = astring[1];

            UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(playerName);
            if (uuid == null){
                iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + playerName + " has never logged into the server"));
                return;
            }
            getLastSeenFromUUID(uuid, iCommandSender);

            return;
        }

        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_RED + "TODO"));

    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "help", "viewlogins", "viewplayerstats", "delloginhistory", "lastknownusername", "lastseen");
        if (typedValue.length == 2 && (typedValue[0].equalsIgnoreCase("lastknownusername") || typedValue[0].equalsIgnoreCase("lastseen")))
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

    private void getLastSeenFromUUID(UUID uuid, ICommandSender sender){
        LastKnownUsernames name = LastKnownUsernameJsonHelper.getLastKnownUsernameFromList(uuid);

        List<EntityPlayerMP> onlinePlayers = PlayerMPUtil.getOnlinePlayers();
        for (EntityPlayerMP player : onlinePlayers){
            if (player.getUniqueID().equals(uuid)){
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + player.getDisplayName() + EnumChatFormatting.WHITE + " is currently " + EnumChatFormatting.GREEN + "Online"));
                return;
            }
        }
        assert name != null;
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + name.getLastKnownUsername() + EnumChatFormatting.WHITE + " is last seen on " + EnumChatFormatting.ITALIC + convertTime(name.getLastSeen())));
    }

    private void getListOfKnownUsernames(UUID uid, ICommandSender sender){
        for (LastKnownUsernames u : CheesecakeServerCommands.lastKnownUsernames){
            if (u.getUuid().equals(uid)){
                //Found, send list of usernames to sender
                foundAndTellRequesterAboutKnownUsernames(u, sender);
                return;
            }
        }

        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unable to find any player with UUID of " + uid.toString()));
    }

    private void getListOfKnownUsernames(String player, ICommandSender sender){
        for (LastKnownUsernames u : CheesecakeServerCommands.lastKnownUsernames){
            if (u.getLastKnownUsername().equalsIgnoreCase(player)){
                foundAndTellRequesterAboutKnownUsernames(u, sender);
                return;
            }
        }

        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unable to find any player named " + player));
    }

    private void foundAndTellRequesterAboutKnownUsernames(LastKnownUsernames u, ICommandSender sender){
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "====================================================="));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + " Usernames for " + EnumChatFormatting.WHITE + u.getUuid()));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "====================================================="));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.WHITE + "Size: " + EnumChatFormatting.AQUA + u.getHistoryOfKnownUsernames().size()));
        for (String names : u.getHistoryOfKnownUsernames()){
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + names));
        }
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "====================================================="));
    }

    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z");
        return format.format(date);
    }
}
