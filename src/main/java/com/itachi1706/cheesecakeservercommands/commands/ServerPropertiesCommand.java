package com.itachi1706.cheesecakeservercommands.commands;

import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ServerPropertiesCommand extends BaseCommand {

    public ServerPropertiesCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> getServerProperties(context.getSource()));
    }

    private int getServerProperties(CommandSourceStack sender) {
        boolean serverSide = FMLEnvironment.dist.isDedicatedServer();
        DedicatedServer dedicatedServer = null;
        if (serverSide) {
            dedicatedServer = (DedicatedServer) ServerLifecycleHooks.getCurrentServer();
            dedicatedServer.getProperties()
            ;
        }

        MinecraftServer tmp = ServerUtil.getServerInstance();
        List<String> messagesToSend = new ArrayList<>();

        messagesToSend.add(ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        messagesToSend.add(ChatFormatting.BLUE + TextUtil.centerText("Server Status"));
        messagesToSend.add(ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        messagesToSend.add(ChatFormatting.GOLD + "Server: " + ChatFormatting.AQUA + tmp.getServerModName());
        messagesToSend.add(ChatFormatting.GOLD + "Server Type: " + ChatFormatting.AQUA + ( serverSide ? "Dedicated Server (MP)" : "Single Player World (SP)"));
        messagesToSend.add(ChatFormatting.GOLD + "Server Version: " + ChatFormatting.AQUA + tmp.getServerVersion());
        if (serverSide) messagesToSend.add(ChatFormatting.GOLD + "Server Online Mode: " + ChatFormatting.AQUA + dedicatedServer.getProperties().onlineMode);
        messagesToSend.add(ChatFormatting.GOLD + "Server Modded State: " + ChatFormatting.AQUA + tmp.getModdedStatus().confidence().name());
        messagesToSend.add(ChatFormatting.GOLD + "Server MOTD: " + ChatFormatting.AQUA + tmp.getMotd());
        if (serverSide) messagesToSend.add(ChatFormatting.GOLD + "Server IP: " + ChatFormatting.AQUA + dedicatedServer.getServerIp() + ":" + dedicatedServer.getServerPort());
        messagesToSend.add(ChatFormatting.GOLD + "Server Max Allowed Players: " + ChatFormatting.AQUA + tmp.getMaxPlayers());
        if (serverSide) messagesToSend.add(ChatFormatting.GOLD + "Server Spawn Protection Size: " + ChatFormatting.AQUA + tmp.getSpawnProtectionRadius());
        messagesToSend.add(ChatFormatting.GOLD + "Server View Distance: " + ChatFormatting.AQUA + tmp.getPlayerList().getViewDistance());
        messagesToSend.add(ChatFormatting.GOLD + "Server World: " + ChatFormatting.AQUA + tmp.overworld().dimension().location());
        messagesToSend.add(ChatFormatting.GOLD + "Server Allow Nether: " + ChatFormatting.AQUA + tmp.isNetherEnabled());
        messagesToSend.add(ChatFormatting.GOLD + "Server Allow Flight: " + ChatFormatting.AQUA + tmp.isFlightAllowed());
        messagesToSend.add(ChatFormatting.GOLD + "Server Default Gamemode: " + ChatFormatting.AQUA + StringUtils.capitalize(tmp.getDefaultGameType().getName()));
        if (serverSide) messagesToSend.add(ChatFormatting.GOLD + "Server Generate Structure: " + ChatFormatting.AQUA + dedicatedServer.getProperties().getWorldGenSettings(dedicatedServer.registryAccess()).generateFeatures());
        if (serverSide) messagesToSend.add(ChatFormatting.GOLD + "Server Whitelist: " + ChatFormatting.AQUA + tmp.getPlayerList().isUsingWhitelist());
        messagesToSend.add(ChatFormatting.GOLD + "Hardcore Mode: " + ChatFormatting.AQUA + tmp.isHardcore());
        messagesToSend.add(ChatFormatting.GOLD + TextUtil.generateChatBreaks());

        sendSuccessMessage(sender, messagesToSend);

        return Command.SINGLE_SUCCESS;
    }
}
