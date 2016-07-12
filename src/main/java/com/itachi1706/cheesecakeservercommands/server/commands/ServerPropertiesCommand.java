package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
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
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        if(args.length != 0)
        {
            getCommandUsage(iCommandSender);
            return;
        }
        
        getServerStats(iCommandSender);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        return null;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private void getServerStats(ICommandSender sender){
        MinecraftServer tmp = ServerUtil.getServerInstance();
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "==================================================");
        ChatHelper.sendMessage(sender, ChatFormatting.BLUE + "                    Server Status");
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "==================================================");
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server: " + ChatFormatting.AQUA + tmp.getServerModName());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Version: " + ChatFormatting.AQUA + tmp.getMinecraftVersion());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Online Mode: " + ChatFormatting.AQUA + tmp.isServerInOnlineMode());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Owner: " + ChatFormatting.AQUA + tmp.getServerOwner());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server MOTD: " + ChatFormatting.AQUA + tmp.getMOTD());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server IP: " + ChatFormatting.AQUA + tmp.getServerHostname() + ":" + tmp.getServerPort());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Max Allowed Players: " + ChatFormatting.AQUA + tmp.getMaxPlayers());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Spawn Protection Size: " + ChatFormatting.AQUA + tmp.getSpawnProtectionSize());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server View Distance: " + ChatFormatting.AQUA + tmp.getPlayerList().getViewDistance());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server World: " + ChatFormatting.AQUA + tmp.getEntityWorld().getWorldInfo().getWorldName());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Allow Nether: " + ChatFormatting.AQUA + tmp.getAllowNether());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Allow Flight: " + ChatFormatting.AQUA + tmp.isFlightAllowed());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Default Gamemode: " + ChatFormatting.AQUA + tmp.getGameType().getName());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Generate Structure: " + ChatFormatting.AQUA + tmp.canStructuresSpawn());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Server Whitelist: " + ChatFormatting.AQUA + tmp.getPlayerList().isWhiteListEnabled());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "Hardcore Mode: " + ChatFormatting.AQUA + tmp.isHardcore());
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "==================================================");
    }

}
