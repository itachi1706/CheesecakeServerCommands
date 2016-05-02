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
}
