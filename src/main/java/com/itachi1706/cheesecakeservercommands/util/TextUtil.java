package com.itachi1706.cheesecakeservercommands.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

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
}
