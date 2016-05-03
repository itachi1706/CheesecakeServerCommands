package com.itachi1706.cheesecakeservercommands.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

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
        sendMessage(sender, getText(text));
    }

    public static void sendMessage(EntityPlayerMP sender, ChatComponentText text) {
        sender.addChatMessage(text);
    }

    public static void sendMessage(EntityPlayerMP sender, String text) {
        sendMessage(sender, getText(text));
    }

    public static void sendMessage(EntityPlayer sender, ChatComponentText text) {
        sender.addChatMessage(text);
    }

    public static void sendMessage(EntityPlayer sender, String text) {
        sendMessage(sender, getText(text));
    }

    public static void sendGlobalMessage(ChatComponentText text) {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(text);
    }

    public static void sendAdminMessage(ICommandSender sender, ChatComponentText text) {
        // Send to everyone except admin
        String texts = text.getUnformattedTextForChat();

        texts = "[" + sender.getCommandSenderName() + ": " + texts + "]";
        ChatComponentText newtext = new ChatComponentText(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + texts);
        List<EntityPlayerMP> players = PlayerMPUtil.getOnlinePlayers();
        LogHelper.info(texts);
        for (EntityPlayerMP playerMP : players) {
            if (PlayerMPUtil.isOperator(playerMP) && !playerMP.getCommandSenderName().equals(sender.getCommandSenderName())) {
                playerMP.addChatMessage(newtext);
            }
        }
    }

    public static void sendAdminMessage(ICommandSender sender, String text) {
        sendAdminMessage(sender, getText(text));
    }

    public static void sendGlobalMessage(String text) {
        sendGlobalMessage(getText(text));
    }

    public static String formatString(String unformattedString) {
        unformattedString = unformattedString.replace("&0", "\u00a70");
        unformattedString = unformattedString.replace("&1", "\u00a71");
        unformattedString = unformattedString.replace("&2", "\u00a72");
        unformattedString = unformattedString.replace("&3", "\u00a73");
        unformattedString = unformattedString.replace("&4", "\u00a74");
        unformattedString = unformattedString.replace("&5", "\u00a75");
        unformattedString = unformattedString.replace("&6", "\u00a76");
        unformattedString = unformattedString.replace("&7", "\u00a77");
        unformattedString = unformattedString.replace("&8", "\u00a78");
        unformattedString = unformattedString.replace("&9", "\u00a79");
        unformattedString = unformattedString.replace("&a", "\u00a7a");
        unformattedString = unformattedString.replace("&b", "\u00a7b");
        unformattedString = unformattedString.replace("&c", "\u00a7c");
        unformattedString = unformattedString.replace("&d", "\u00a7d");
        unformattedString = unformattedString.replace("&e", "\u00a7e");
        unformattedString = unformattedString.replace("&f", "\u00a7f");
        unformattedString = unformattedString.replace("&k", "\u00a7k");
        unformattedString = unformattedString.replace("&l", "\u00a7l");
        unformattedString = unformattedString.replace("&m", "\u00a7m");
        unformattedString = unformattedString.replace("&n", "\u00a7n");
        unformattedString = unformattedString.replace("&o", "\u00a7o");
        unformattedString = unformattedString.replace("&r", "\u00a7r");
        return unformattedString;
    }
}
