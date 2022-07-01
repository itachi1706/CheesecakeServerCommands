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
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, ChatFormatting.BLUE + TextUtil.centerText("Server Status"));
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server: " + ChatFormatting.AQUA + tmp.getServerModName());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Type: " + ChatFormatting.AQUA + ((serverSide) ? "Dedicated Server (MP)" : "Single Player World (SP)"));
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Version: " + ChatFormatting.AQUA + tmp.getServerVersion());
        if (serverSide) sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Online Mode: " + ChatFormatting.AQUA + dedicatedServer.getProperties().onlineMode);
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Modded State: " + ChatFormatting.AQUA + tmp.getModdedStatus().confidence().name());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server MOTD: " + ChatFormatting.AQUA + tmp.getMotd());
        if (serverSide) sendSuccessMessage(sender, ChatFormatting.GOLD + "Server IP: " + ChatFormatting.AQUA + dedicatedServer.getServerIp() + ":" + dedicatedServer.getServerPort());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Max Allowed Players: " + ChatFormatting.AQUA + tmp.getMaxPlayers());
        if (serverSide) sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Spawn Protection Size: " + ChatFormatting.AQUA + tmp.getSpawnProtectionRadius());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server View Distance: " + ChatFormatting.AQUA + tmp.getPlayerList().getViewDistance());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server World: " + ChatFormatting.AQUA + tmp.overworld().dimension().location());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Allow Nether: " + ChatFormatting.AQUA + tmp.isNetherEnabled());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Allow Flight: " + ChatFormatting.AQUA + tmp.isFlightAllowed());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Default Gamemode: " + ChatFormatting.AQUA + StringUtils.capitalize(tmp.getDefaultGameType().getName()));
        if (serverSide) sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Generate Structure: " + ChatFormatting.AQUA + dedicatedServer.getProperties().getWorldGenSettings(dedicatedServer.registryAccess()).generateFeatures());
        if (serverSide) sendSuccessMessage(sender, ChatFormatting.GOLD + "Server Whitelist: " + ChatFormatting.AQUA + tmp.getPlayerList().isUsingWhitelist());
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Hardcore Mode: " + ChatFormatting.AQUA + tmp.isHardcore());
        sendSuccessMessage(sender, ChatFormatting.GOLD + TextUtil.generateChatBreaks());

        return Command.SINGLE_SUCCESS;
    }
}
