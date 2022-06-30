package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.reference.CommandPermissionsLevel;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class WowCommand extends BaseCommand {

    public WowCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> wowYourself(context.getSource()))
                .then(Commands.argument("player", EntityArgument.players())
                        // Just for this command requires OP for others but not yourself
                        .requires(source -> source.hasPermission(CommandPermissionsLevel.OPS))
                        .executes(context -> wowOthers(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int wowYourself(CommandSourceStack sender) {
        sendSuccessMessage(sender, ChatFormatting.GREEN + "Such command, Very Nothing");
        TextUtil.sendAdminChatMessage(sender, "Doged Own Self");
        sendToEveryone(sender, null);

        return Command.SINGLE_SUCCESS;
    }

    private int wowOthers(CommandSourceStack sender, ServerPlayer player) {
        if (player == null) {
            sendFailureMessage(sender, "Player not found");
            return 0;
        }

        if (!sender.hasPermission(CommandPermissionsLevel.OPS)) {
            sendFailureMessage(sender, ChatFormatting.RED + "You do not have permission to do this to other players");
            TextUtil.sendAdminChatMessage(sender, "Tried to Doge " + player.getName().getString() + " while he is not an OP");
            return 0;
        }

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Doged " + player.getName().getString());
        TextUtil.sendAdminChatMessage(sender, "Doged " + player.getName().getString());
        sendMessage(player, ChatFormatting.GREEN + "Such randomness, Very Nothing");
        sendToEveryone(sender, player);

        return Command.SINGLE_SUCCESS;
    }

    private void sendToEveryone(CommandSourceStack starter, ServerPlayer recepient) {
        List<ServerPlayer> players = ServerPlayerUtil.getOnlinePlayers();
        for (ServerPlayer player : players) {
            if (!(player.getName().equals(starter.getTextName()))) {
                if (recepient != null && !player.getName().equals(recepient.getName())) {
                    sendMessage(player, ChatFormatting.GOLD + recepient.getName().getString() + ChatFormatting.GRAY + " just got doged :D");
                } else if (recepient == null) {
                    sendMessage(player, ChatFormatting.GOLD + starter.getTextName() + ChatFormatting.GRAY + " just got doged :D");
                }
            }
        }
    }
}
