package com.itachi1706.cheesecakeservercommands.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.util.List;
import java.util.stream.Stream;

public class TextUtil {

    private static void sendMessage(ServerPlayer player, Component textComponent, boolean actionBar) {
        player.displayClientMessage(textComponent, actionBar);
    }

    private static void sendGlobalMessage(PlayerList players, Component textComponent, boolean actionBar) {
        for(int i = 0; i < players.getPlayers().size(); ++i) {
            ServerPlayer player = players.getPlayers().get(i);
            sendMessage(player, textComponent, actionBar);
        }
    }

    // Chat msg

    public static void sendChatMessage(ServerPlayer player, Component textComponent) {
        sendMessage(player, textComponent, false);
    }

    public static void sendChatMessage(ServerPlayer player, String translationKey, Object... args) {
        sendMessage(player, new TranslatableComponent(translationKey, args), false);
    }

    // Action Bar

    public static void sendActionMessage(ServerPlayer player, Component textComponent) {
        sendMessage(player, textComponent, true);
    }

    public static void sendActionMessage(ServerPlayer player, String translationKey, Object... args) {
        sendMessage(player, new TranslatableComponent(translationKey, args), true);
    }

    // Global Action Bar

    public static void sendGlobalActionMessage(PlayerList players, String translationKey, Object... args) {
        sendGlobalMessage(players, new TranslatableComponent(translationKey, args), true);
    }

    public static void sendGlobalActionMessage(PlayerList players, Component textComponent) {
        sendGlobalMessage(players, textComponent, true);
    }

    // Global msg

    public static void sendGlobalChatMessage(PlayerList players, String translationKey, Object... args) {
        sendGlobalMessage(players, new TranslatableComponent(translationKey, args), false);
    }

    public static void sendGlobalChatMessage(PlayerList players, Component textComponent) {
        sendGlobalMessage(players, textComponent, false);
    }

    // From ChatHelper

    // Admin Messages

    public static void sendAdminChatMessage(ServerPlayer player, Component textComponent) {
//        if (ServerUtil.checkIfAdminSilenced(player)) return; // Don't send if admin silenced
        String text = textComponent.getString();

        text = "[" + player.getName() + ": " + text + "]";
        TextComponent newtext = new TextComponent(ChatFormatting.GRAY + "" + ChatFormatting.ITALIC + text);
        List<ServerPlayer> players = ServerPlayerUtil.getOnlinePlayers();
        LogHelper.info(text);

        for (ServerPlayer serverPlayer : players) {
            if (ServerPlayerUtil.isOperator(serverPlayer) && !serverPlayer.getName().equals(player.getName())) {
                sendMessage(serverPlayer, newtext, false);
            }
        }

    }
    public static void sendAdminChatMessage(ServerPlayer player, String message) {
        sendAdminChatMessage(player, new TextComponent(message));
    }

    // Utilities
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

    private static final int CHAT_LENGTH = 53;

    public static String generateChatBreaks() {
        return generateChatBreaks('=');
    }

    public static String generateChatBreaks(char breakWith) {
        StringBuilder sb = new StringBuilder();
        Stream.generate(() -> breakWith).limit(CHAT_LENGTH).forEach(sb::append);
        return sb.toString();
    }

    public static String centerText(String text) {
        return centerText(text, ' ');
    }

    public static String centerText(String text, char breakWith) {
        StringBuilder sb = new StringBuilder();
        int noOfBreaks = (text.length() > CHAT_LENGTH) ? 0 : ((CHAT_LENGTH - text.length()) / 2);
        if (text.length() < CHAT_LENGTH && breakWith == ' ') noOfBreaks *= 1.5;
        Stream.generate(() -> breakWith).limit(noOfBreaks).forEach(sb::append);
        sb.append(text);
        if (breakWith == ' ' || text.length() > CHAT_LENGTH) return sb.toString();
        int fillLeft = CHAT_LENGTH - noOfBreaks - text.length(); // Fill up the rest of the chat window
        Stream.generate(() -> breakWith).limit(fillLeft).forEach(sb::append);
        return sb.toString();
    }
}
