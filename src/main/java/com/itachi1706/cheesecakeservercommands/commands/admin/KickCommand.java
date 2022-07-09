package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class KickCommand extends BaseCommand {

    private static final String ARG_PLAYER = "player";
    private static final String DEFAULT_KICK_REASON = "Kicked by an operator.";

    public KickCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.argument(ARG_PLAYER, EntityArgument.player())
                        .executes(context -> kickOthers(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER)))
                        .then(Commands.argument("reason", MessageArgument.message())
                                .executes(context -> kickOthersWithReason(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER), MessageArgument.getMessage(context, "reason")))));
    }

    private int kick(CommandSourceStack sender, ServerPlayer player, TextComponent reason) {
        player.connection.disconnect(reason);
        sendSuccessMessage(sender, "Kicked " + player.getName().getString() + " with reason " + DEFAULT_KICK_REASON);
        TextUtil.sendAdminChatMessage(sender, "Kicked " + player.getName().getString() + " with reason " + DEFAULT_KICK_REASON);

        return Command.SINGLE_SUCCESS;
    }

    private int kickOthers(CommandSourceStack sender, ServerPlayer player) {
        return kick(sender, player, new TextComponent(DEFAULT_KICK_REASON));
    }

    private int kickOthersWithReason(CommandSourceStack sender, ServerPlayer player, Component reason) {
        String formattedReason = TextUtil.formatString(reason.getString());
        return kick(sender, player, new TextComponent(formattedReason));
    }
}
