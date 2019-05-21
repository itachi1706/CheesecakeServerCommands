package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
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
        this.aliases = new ArrayList<>();
        this.aliases.add("serverproperties");
    }

    @Override
    @Nonnull
    public String getName() {
        return "serverproperties";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/serverproperties";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) {

        if(args.length != 0)
        {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + getUsage(iCommandSender));
            return;
        }
        
        getServerStats(iCommandSender);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
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

    private void getServerStats(ICommandSender sender){
        boolean serverSide = FMLCommonHandler.instance().getSide().isServer();
        MinecraftServer tmp = ServerUtil.getServerInstance();
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + ChatHelper.generateChatBreaks());
        ChatHelper.sendMessage(sender, TextFormatting.BLUE + ChatHelper.centerText("Server Status"));
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + ChatHelper.generateChatBreaks());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server: " + TextFormatting.AQUA + tmp.getServerModName());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Type: " + TextFormatting.AQUA + ((serverSide) ? "Dedicated Server (MP)" : "Single Player World (SP)"));
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Version: " + TextFormatting.AQUA + tmp.getMinecraftVersion());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Online Mode: " + TextFormatting.AQUA + tmp.isServerInOnlineMode());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Owner: " + TextFormatting.AQUA + tmp.getServerOwner());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server MOTD: " + TextFormatting.AQUA + tmp.getMOTD());
        if (serverSide) ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server IP: " + TextFormatting.AQUA + tmp.getServerHostname() + ":" + tmp.getServerPort());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Max Allowed Players: " + TextFormatting.AQUA + tmp.getMaxPlayers());
        if (serverSide) ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Spawn Protection Size: " + TextFormatting.AQUA + tmp.getSpawnProtectionSize());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server View Distance: " + TextFormatting.AQUA + tmp.getPlayerList().getViewDistance());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server World: " + TextFormatting.AQUA + tmp.getEntityWorld().getWorldInfo().getWorldName());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Allow Nether: " + TextFormatting.AQUA + tmp.getAllowNether());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Allow Flight: " + TextFormatting.AQUA + tmp.isFlightAllowed());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Default Gamemode: " + TextFormatting.AQUA + StringUtils.capitalize(tmp.getGameType().getName()));
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Generate Structure: " + TextFormatting.AQUA + tmp.canStructuresSpawn());
        if (serverSide) ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Server Whitelist: " + TextFormatting.AQUA + tmp.getPlayerList().isWhiteListEnabled());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "Hardcore Mode: " + TextFormatting.AQUA + tmp.isHardcore());
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + ChatHelper.generateChatBreaks());
    }

}
