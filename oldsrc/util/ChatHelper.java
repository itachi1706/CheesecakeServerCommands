package com.itachi1706.cheesecakeservercommands.util;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Kenneth on 1/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
 *
 * DEPRECATED. Replaced by {@link com.itachi1706.cheesecakeservercommands.util.TextUtil}
 */
public class ChatHelper {

    public static TextComponentString getText(String text) {
        return new TextComponentString(text);
    }

    public static void sendMessage(ICommandSender sender, TextComponentString text) {
        sender.sendMessage(text);
    }

    public static void sendMessage(ICommandSender sender, ITextComponent text) {
        sender.sendMessage(text);
    }

    public static void sendMessage(ICommandSender sender, String text) {
        sendMessage(sender, getText(text));
    }

    public static void sendMessage(EntityPlayerMP sender, TextComponentString text) {
        sender.sendMessage(text);
    }

    public static void sendMessage(EntityPlayerMP sender, String text) {
        sendMessage(sender, getText(text));
    }

    public static void sendMessage(EntityPlayer sender, TextComponentString text) {
        sender.sendMessage(text);
    }

    public static void sendMessage(EntityPlayer sender, ITextComponent text) {
        sender.sendMessage(text);
    }

    public static void sendMessage(EntityPlayer sender, String text) {
        sendMessage(sender, getText(text));
    }

    public static void sendMessage(ICommandSender sender, List<String> texts) {
        for (String text : texts)
            sendMessage(sender, getText(text));
    }

    public static void sendGlobalMessage(TextComponentString text) {
        ServerUtil.getServerInstance().getPlayerList().sendMessage(text);
    }

    public static void sendGlobalMessage(ITextComponent text) {
        ServerUtil.getServerInstance().getPlayerList().sendMessage(text);
    }

    public static void sendGlobalInfoMessage(ITextComponent text) {
        ServerUtil.getServerInstance().getPlayerList().sendPacketToAllPlayers(new SPacketChat(text, ChatType.GAME_INFO));
    }

    public static void sendGlobalMessage(String text) {
        sendGlobalMessage(getText(text));
    }

}
