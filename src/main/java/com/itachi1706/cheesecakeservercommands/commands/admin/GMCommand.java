package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class GMCommand extends BaseCommand {

    private final String gameTypeName;
    private final GameType gameType;

    public GMCommand(String name, int permissionLevel, boolean enabled, GameType gameType, String gameTypeName) {
        super(name, permissionLevel, enabled);
        this.gameType = gameType;
        this.gameTypeName = gameTypeName;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> setOwnGamemode(context.getSource()))
                .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> setPlayerGamemode(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int setOwnGamemode(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot set CONSOLE as " + gameTypeName);
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot set " + sender.getTextName() + " as " + gameTypeName);
            return 0;
        }

        player.setGameMode(gameType);
        sendSuccessMessage(sender, "Set own gamemode to " + ChatFormatting.GOLD + gameTypeName);
        TextUtil.sendAdminChatMessage(sender, "Set own gamemode to " + gameTypeName);

        return Command.SINGLE_SUCCESS;
    }

    private int setPlayerGamemode(CommandSourceStack sender, ServerPlayer player) {
        player.setGameMode(gameType);
        sendSuccessMessage(sender, "Set " + ChatFormatting.AQUA + player.getDisplayName().getString() + ChatFormatting.RESET + " gamemode to " + ChatFormatting.GOLD + gameTypeName);
        TextUtil.sendAdminChatMessage(sender, "Set " + player.getDisplayName().getString() + " gamemode to " + gameTypeName);

        return Command.SINGLE_SUCCESS;
    }
}
