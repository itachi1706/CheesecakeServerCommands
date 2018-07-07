package com.itachi1706.cheesecakeservercommands.util;

import com.itachi1706.cheesecakeservercommands.jsonstorage.LastKnownUsernameJsonHelper;
import com.itachi1706.cheesecakeservercommands.nbtstorage.AdminSilenced;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 1/5/2016.
 * for com.itachi1706.cheesecakeservercommands.util in CheesecakeServerCommands
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

    public static void sendAdminMessage(ICommandSender sender, TextComponentString text) {
        // Send to everyone except admin
        if (sender instanceof EntityPlayer && AdminSilenced.getState()) {
            UUID uuid = LastKnownUsernameJsonHelper.getLastKnownUUIDFromPlayerName(sender.getName());
            if (uuid != null && AdminSilenced.contains(uuid)) return; // Don't send admin message
            if (UUID.fromString(AdminSilenced.MY_UUID).equals(uuid)) return; // Don't send from me as well
        }
        String texts = text.getUnformattedText();

        texts = "[" + sender.getName() + ": " + texts + "]";
        TextComponentString newtext = new TextComponentString(TextFormatting.GRAY + "" + TextFormatting.ITALIC + texts);
        List<EntityPlayerMP> players = PlayerMPUtil.getOnlinePlayers();
        LogHelper.info(texts);
        for (EntityPlayerMP playerMP : players) {
            if (PlayerMPUtil.isOperator(playerMP) && !playerMP.getName().equals(sender.getName())) {
                playerMP.sendMessage(newtext);
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
