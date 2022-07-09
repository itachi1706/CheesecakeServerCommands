package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class GamemodeCommand extends BaseCommand {
    public GamemodeCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    private final String[] gametypeList = {"creative", "adventure", "survival", "spectator", "c", "a", "s", "sp", "0", "1", "2", "3"};

    private static final String ARG_GAMEMODE = "gamemode";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.argument(ARG_GAMEMODE, StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(gametypeList, builder))
                        .executes(context -> setOwnGamemode(context.getSource(), StringArgumentType.getString(context, ARG_GAMEMODE)))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> setPlayerGamemode(context.getSource(), EntityArgument.getPlayer(context, "player"), StringArgumentType.getString(context, ARG_GAMEMODE)))));
    }

    private int setOwnGamemode(CommandSourceStack sender, String gamemode) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot set gamemode on CONSOLE", "Cannot set gamemode on " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        GameType gameType = getGameMode(gamemode);
        if (gameType == null) {
            sendFailureMessage(sender, "Invalid gamemode");
            sendFailureMessage(sender, "Available gamemodes: creative, adventure, survival, spectator");
            return 0;
        }

        player.setGameMode(gameType);
        sendSuccessMessage(sender, "Set own gamemode to " + ChatFormatting.GOLD + gameType.getLongDisplayName().getString());
        TextUtil.sendAdminChatMessage(sender, "Set own gamemode to " + gameType.getLongDisplayName().getString());

        return Command.SINGLE_SUCCESS;
    }

    private int setPlayerGamemode(CommandSourceStack sender, ServerPlayer player, String gamemode) {
        GameType gameType = getGameMode(gamemode);
        if (gameType == null) {
            sendFailureMessage(sender, "Invalid gamemode");
            sendFailureMessage(sender, "Available gamemodes: creative, adventure, survival, spectator");
            return 0;
        }

        player.setGameMode(gameType);
        sendSuccessMessage(sender, "Set gamemode of " + ChatFormatting.AQUA + player.getDisplayName().getString() + ChatFormatting.RESET + " to " + ChatFormatting.GOLD + gameType.getLongDisplayName().getString());
        TextUtil.sendAdminChatMessage(sender, "Set gamemode of " + player.getDisplayName().getString() + " to " + gameType.getLongDisplayName().getString());

        return Command.SINGLE_SUCCESS;
    }

    private GameType getGameMode(String gamemode) {
        if (gamemode.equalsIgnoreCase("survival") || gamemode.equalsIgnoreCase("s") || gamemode.equals("0")) {
            return GameType.SURVIVAL;
        } else if (gamemode.equalsIgnoreCase("adventure") || gamemode.equalsIgnoreCase("a") || gamemode.equals("2")) {
            return GameType.ADVENTURE;
        } else if (gamemode.equalsIgnoreCase("creative") || gamemode.equalsIgnoreCase("c") || gamemode.equals("1")) {
            return GameType.CREATIVE;
        } else if (gamemode.equalsIgnoreCase("spectator") || gamemode.equalsIgnoreCase("sp") || gamemode.equals("3")) {
            return GameType.SPECTATOR;
        } else {
            return null;
        }
    }
}
