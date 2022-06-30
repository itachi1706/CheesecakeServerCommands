package com.itachi1706.cheesecakeservercommands.commands;

import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

public class PingCommand extends BaseCommand {

    public PingCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> pingUser(context.getSource()));
    }

    private int pingUser(CommandSourceStack sender) {

        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendSuccessMessage(sender, "Pong!");
        } else {
            ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
            if (player == null) {
                sendSuccessMessage(sender, ChatFormatting.GREEN + "Pong!");
            } else {
                sendMessage(player, ChatFormatting.GREEN + "Pong! (" + player.latency + "ms)");
            }
        }

        return Command.SINGLE_SUCCESS;
    }
}
