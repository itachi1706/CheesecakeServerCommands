package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.util.text.TextFormatting;
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
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "==================================================");
        ChatHelper.sendMessage(sender, TextFormatting.BLUE + "                    Server Status");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "==================================================");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server: " + TextFormatting.AQUA + tmp.getServerModName());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Version: " + TextFormatting.AQUA + tmp.getMinecraftVersion());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Online Mode: " + TextFormatting.AQUA + tmp.isServerInOnlineMode());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Owner: " + TextFormatting.AQUA + tmp.getServerOwner());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server MOTD: " + TextFormatting.AQUA + tmp.getMOTD());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server IP: " + TextFormatting.AQUA + tmp.getServerHostname() + ":" + tmp.getServerPort());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Max Allowed Players: " + TextFormatting.AQUA + tmp.getMaxPlayers());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Spawn Protection Size: " + TextFormatting.AQUA + tmp.getSpawnProtectionSize());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server View Distance: " + TextFormatting.AQUA + tmp.getPlayerList().getViewDistance());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server World: " + TextFormatting.AQUA + tmp.getEntityWorld().getWorldInfo().getWorldName());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Allow Nether: " + TextFormatting.AQUA + tmp.getAllowNether());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Allow Flight: " + TextFormatting.AQUA + tmp.isFlightAllowed());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Default Gamemode: " + TextFormatting.AQUA + tmp.getGameType().getName());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Generate Structure: " + TextFormatting.AQUA + tmp.canStructuresSpawn());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Whitelist: " + TextFormatting.AQUA + tmp.getPlayerList().isWhiteListEnabled());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Hardcore Mode: " + TextFormatting.AQUA + tmp.isHardcore());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "==================================================");
    }

}
