package com.itachi1706.cheesecakeservercommands.commands;

import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import java.util.List;

public abstract class BaseCommand {

    protected LiteralArgumentBuilder<CommandSourceStack> builder;
    boolean enabled;
    int permissionLevel;
    String name;

    public BaseCommand(String name, int permissionLevel, boolean enabled) {
        this.builder = Commands.literal(name).requires(source -> source.hasPermission(permissionLevel));
        this.enabled = enabled;
        this.permissionLevel = permissionLevel;
        this.name = name;
    }

    public LiteralArgumentBuilder<CommandSourceStack> getBuilder() {
        return builder;
    }

    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return null;
    }

    public String getName() {
        return name;
    }

    public boolean hasPermission(CommandSourceStack sender) {
        return sender.hasPermission(permissionLevel);
    }

    public void sendMessage(ServerPlayer player, String translationKey, Object... args) {
        TextUtil.sendChatMessage(player, translationKey, args);
    }

    public void sendMessage(ServerPlayer player, Component textComponent) {
        TextUtil.sendChatMessage(player, textComponent);
    }

    public void sendSuccessMessage(CommandSourceStack sender, List<String> textArr) {
        for (String text : textArr)
            sendSuccessMessage(sender, text, false);
    }

    public void sendSuccessMessage(CommandSourceStack sender, String text) {
        sendSuccessMessage(sender, text, false);
    }

    public void sendSuccessMessage(CommandSourceStack sender, String text, boolean notifyAdmin) {
        sender.sendSuccess(new TextComponent(text), notifyAdmin);
    }

    public void sendFailureMessage(CommandSourceStack sender, String text) {
        sender.sendFailure(new TextComponent(text));
    }

    public void sendGlobalMessage(PlayerList players, String translationKey, Object... args) {
        TextUtil.sendGlobalChatMessage(players, translationKey, args);
    }
}
