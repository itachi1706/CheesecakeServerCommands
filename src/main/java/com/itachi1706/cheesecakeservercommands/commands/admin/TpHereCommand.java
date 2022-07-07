package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.objects.TeleportLocation;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TeleportHelper;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TpHereCommand extends BaseCommand {
    public TpHereCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> teleportToMe(context.getSource(), EntityArgument.getPlayers(context, "player"))));
    }

    private int teleportToMe(CommandSourceStack sender, Collection<ServerPlayer> players) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot teleport players to CONSOLE");
            return 0;
        }

        ServerPlayer source = ServerPlayerUtil.castToPlayer(sender);
        if (source == null) {
            sendFailureMessage(sender,  "Cannot teleport player to " + sender.getTextName());
            return 0;
        }

        TeleportLocation tp = TeleportHelper.getTeleportLocation(source);
        List<String> playersTeleported = new ArrayList<>();
        for (ServerPlayer player : players) {
            TeleportHelper.teleportPlayer(player, tp.level(), tp.position(), tp.chunkPos(), tp.yRot(), tp.xRot());

            playersTeleported.add(player.getName().getString());
            sendMessage(player, ChatFormatting.GOLD + "Teleporting...");
        }

        if (playersTeleported.isEmpty()) {
            sendFailureMessage(sender, "No players to teleport");
            return 0;
        }

        String message = "Teleported ";
        if (playersTeleported.size() > 1) {
            message += playersTeleported.size() + " players ";
        } else {
            message += playersTeleported.get(0) + " ";
        }

        sendSuccessMessage(sender, ChatFormatting.GOLD + message + "to you");
        TextUtil.sendAdminChatMessage(sender, message + " to him/her");
        return Command.SINGLE_SUCCESS;
    }
}
