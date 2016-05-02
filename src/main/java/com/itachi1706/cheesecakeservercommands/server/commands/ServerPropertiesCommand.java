package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/12/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
public class ServerPropertiesCommand implements ICommand {

    private List<String> aliases;

    /*
    Commands List
    /serverproperties
     */

    public ServerPropertiesCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("serverproperties");
    }

    @Override
    public String getCommandName() {
        return "serverproperties";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "Usage: /serverproperties";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if(astring.length != 0)
        {
            getCommandUsage(iCommandSender);
            return;
        }
        
        getServerStats(iCommandSender);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
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

    private void getServerStats(ICommandSender sender){
        MinecraftServer tmp = MinecraftServer.getServer();
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "==================================================");
        ChatHelper.sendMessage(sender, EnumChatFormatting.BLUE + "                    Server Status");
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "==================================================");
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server: " + EnumChatFormatting.AQUA + tmp.getServerModName());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Version: " + EnumChatFormatting.AQUA + tmp.getMinecraftVersion());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Online Mode: " + EnumChatFormatting.AQUA + tmp.isServerInOnlineMode());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Owner: " + EnumChatFormatting.AQUA + tmp.getServerOwner());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server MOTD: " + EnumChatFormatting.AQUA + tmp.getMotd());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server IP: " + EnumChatFormatting.AQUA + tmp.getServerHostname() + ":" + tmp.getServerPort());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Max Allowed Players: " + EnumChatFormatting.AQUA + tmp.getMaxPlayers());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Spawn Protection Size: " + EnumChatFormatting.AQUA + tmp.getSpawnProtectionSize());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server View Distance: " + EnumChatFormatting.AQUA + tmp.getConfigurationManager().getViewDistance());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server World: " + EnumChatFormatting.AQUA + tmp.getEntityWorld().getWorldInfo().getWorldName());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Allow Nether: " + EnumChatFormatting.AQUA + tmp.getAllowNether());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Allow Flight: " + EnumChatFormatting.AQUA + tmp.isFlightAllowed());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Default Gamemode: " + EnumChatFormatting.AQUA + tmp.getGameType().getName());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Generate Structure: " + EnumChatFormatting.AQUA + tmp.canStructuresSpawn());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Server Whitelist: " + EnumChatFormatting.AQUA + tmp.getConfigurationManager().isWhiteListEnabled());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "Hardcore Mode: " + EnumChatFormatting.AQUA + tmp.isHardcore());
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "==================================================");
    }

}
