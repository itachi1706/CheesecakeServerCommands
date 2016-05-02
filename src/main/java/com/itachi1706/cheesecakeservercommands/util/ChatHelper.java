package com.itachi1706.cheesecakeservercommands.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

/**
 * Created by Kenneth on 1/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 */
public class ChatHelper {

    public static ChatComponentText getText(String text) {
        return new ChatComponentText(text);
    }

    public static void sendMessage(ICommandSender sender, ChatComponentText text) {
        sender.addChatMessage(text);
    }

    public static void sendMessage(ICommandSender sender, String text) {
        sender.addChatMessage(getText(text));
    }

    public static void sendGlobalMessage(ChatComponentText text) {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(text);
    }

    public static void sendGlobalMessage(String text) {
        sendGlobalMessage(getText(text));
    }
}
