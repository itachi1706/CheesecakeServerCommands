package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.libs.selections.WorldPoint;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class LocateCommand extends BaseCommand {
    public LocateCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> findYourself(context.getSource()))
                .then(Commands.argument("players", EntityArgument.player()).executes(context -> findOthers(context.getSource(), EntityArgument.getPlayer(context, "players"))));
    }

    private int findYourself(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot locate CONSOLE");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot locate " + sender.getTextName());
            return 0;
        }

        WorldPoint point = new WorldPoint(player);

        sendSuccessMessage(sender, String.format("%sYou are at %d, %d, %d in dimension %s with a gamemode of %s", ChatFormatting.GOLD,
                point.getX(), point.getY(), point.getZ(), point.getDimension(), player.gameMode.getGameModeForPlayer().getLongDisplayName().getString()));
        TextUtil.sendAdminChatMessage(sender, "Located Own location");

        return Command.SINGLE_SUCCESS;
    }

    private int findOthers(CommandSourceStack sender, ServerPlayer player) {
        WorldPoint point = new WorldPoint(player);

        sendSuccessMessage(sender, String.format("%s%s is at %d, %d, %d in dimension %s with a gamemode of %s", ChatFormatting.GOLD, player.getName().getString(),
                point.getX(), point.getY(), point.getZ(), point.getDimension(), player.gameMode.getGameModeForPlayer().getLongDisplayName().getString()));
        TextUtil.sendAdminChatMessage(sender, "Located " + player.getName().getString() + "'s location");

        return Command.SINGLE_SUCCESS;
    }
}
