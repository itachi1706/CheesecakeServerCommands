package com.itachi1706.cheesecakeservercommands.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class ServerSettingsCommand extends BaseCommand {

    public ServerSettingsCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.literal("allowFlight").executes(context -> getAllowFlight(context.getSource()))
                        .then(Commands.argument("flightVal", BoolArgumentType.bool())
                                .executes(context -> setAllowFlight(context.getSource(), BoolArgumentType.getBool(context, "flightVal")))))
                .then(Commands.literal("allowPVP").executes(context -> getAllowPVP(context.getSource()))
                        .then(Commands.argument("pvpVal", BoolArgumentType.bool())
                                .executes(context -> setAllowPVP(context.getSource(), BoolArgumentType.getBool(context, "pvpVal")))))
                .then(Commands.literal("difficulty").executes(context -> getDifficulty(context.getSource()))
                        .then(Commands.argument("diffVal", StringArgumentType.string())
                                .executes(context -> setDifficulty(context.getSource(), StringArgumentType.getString(context, "diffVal")))))
                .then(Commands.literal("defaultGamemode").executes(context -> getGamemode(context.getSource()))
                        .then(Commands.argument("gmVal", StringArgumentType.string())
                                .executes(context -> setGamemode(context.getSource(), StringArgumentType.getString(context, "gmVal")))))
                ;
    }

    private boolean isNotDedicatedServer(CommandSourceStack sender) {
        if (!ServerUtil.getServerInstance().isDedicatedServer()) {
            sendFailureMessage(sender, DEDICATED_ONLY);
            return false;
        }
        return true;
    }

    private void sendLimitationMessage(CommandSourceStack sender, String parameter) {
        sendSuccessMessage(sender, "Due to limitations, this change is not saved on server restart");
        sendSuccessMessage(sender, "To persist this change, update the " + parameter + " value in server.properties instead");
    }

    private static final String DEDICATED_ONLY = "This command is only available on dedicated servers";

    // allowflight
    private int getAllowFlight(CommandSourceStack sender) {
        if (isNotDedicatedServer(sender)) return 0;

        sendSuccessMessage(sender, String.format("Allow flight: %b", ServerUtil.getDedicatedServerInstance().isFlightAllowed()));
        return Command.SINGLE_SUCCESS;
    }

    private int setAllowFlight(CommandSourceStack sender, boolean value) {
        if (isNotDedicatedServer(sender)) return 0;

        ServerUtil.getDedicatedServerInstance().setFlightAllowed(value);
        sendSuccessMessage(sender, String.format("Allow flight set to %b", value));
        sendLimitationMessage(sender, "allow-flight");

        return Command.SINGLE_SUCCESS;
    }

    // allowpvp
    private int getAllowPVP(CommandSourceStack sender) {
        if (isNotDedicatedServer(sender)) return 0;

        sendSuccessMessage(sender, String.format("Allow PvP: %b", ServerUtil.getDedicatedServerInstance().isPvpAllowed()));

        return Command.SINGLE_SUCCESS;
    }

    private int setAllowPVP(CommandSourceStack sender, boolean value) {
        if (isNotDedicatedServer(sender)) return 0;
        
        ServerUtil.getDedicatedServerInstance().setPvpAllowed(value);
        sendSuccessMessage(sender, String.format("PvP set to %b", value));
        sendLimitationMessage(sender, "pvp");

        return Command.SINGLE_SUCCESS;
    }

    // gamemode
    private int getGamemode(CommandSourceStack sender) {
        if (isNotDedicatedServer(sender)) return 0;

        sendSuccessMessage(sender, String.format("Default Gamemode: %s", ServerUtil.getDedicatedServerInstance().getDefaultGameType().getName()));

        return Command.SINGLE_SUCCESS;
    }

    private int setGamemode(CommandSourceStack sender, String value) {
        if (isNotDedicatedServer(sender)) return 0;

        GameType gamemode = GameType.byId(getGamemode(value));
        ServerUtil.getDedicatedServerInstance().setDefaultGameType(gamemode);
        sendSuccessMessage(sender, String.format("Default Gamemode set to %s", gamemode.getName()));
        sendLimitationMessage(sender, "gamemode");

        return Command.SINGLE_SUCCESS;
    }

    // difficulty
    private int getDifficulty(CommandSourceStack sender) {
        if (isNotDedicatedServer(sender)) return 0;

        sendSuccessMessage(sender, String.format("Difficulty: %s", ServerUtil.getDedicatedServerInstance().getWorldData().getDifficulty().getDisplayName().getString()));

        return Command.SINGLE_SUCCESS;
    }

    private int setDifficulty(CommandSourceStack sender, String value) {
        if (isNotDedicatedServer(sender)) return 0;

        Difficulty difficulty = Difficulty.byId(getDifficulty(value));
        ServerUtil.getDedicatedServerInstance().setDifficulty(difficulty, true);
        sendSuccessMessage(sender, String.format("Difficulty set to %s", difficulty.name()));
        sendLimitationMessage(sender, "difficulty");

        return Command.SINGLE_SUCCESS;
    }

    // Utility classes
    private int getDifficulty(String difficulty) {
        int id = 0;
        if (difficulty.equalsIgnoreCase("peaceful") || difficulty.equalsIgnoreCase("0")) {
            id = Difficulty.PEACEFUL.getId();
        } else if (difficulty.equalsIgnoreCase("easy") || difficulty.equalsIgnoreCase("1")) {
            id = Difficulty.EASY.getId();
        } else if (difficulty.equalsIgnoreCase("normal") || difficulty.equalsIgnoreCase("2")) {
            id = Difficulty.NORMAL.getId();
        } else if (difficulty.equalsIgnoreCase("hard") || difficulty.equalsIgnoreCase("3")) {
            id = Difficulty.HARD.getId();
        }

        return id;
    }

    private int getGamemode(String gamemode) {
        int id = 0;
        if (gamemode.equalsIgnoreCase("survival") || gamemode.equalsIgnoreCase("s") || gamemode.equals("0")) {
            id = GameType.SURVIVAL.getId();
        } else if (gamemode.equalsIgnoreCase("adventure") || gamemode.equalsIgnoreCase("a") || gamemode.equals("2")) {
            id = GameType.ADVENTURE.getId();
        } else if (gamemode.equalsIgnoreCase("creative") || gamemode.equalsIgnoreCase("c") || gamemode.equals("1")) {
            id = GameType.CREATIVE.getId();
        } else if (gamemode.equalsIgnoreCase("spectator") || gamemode.equalsIgnoreCase("sp") || gamemode.equals("3")) {
            id = GameType.SPECTATOR.getId();
        }

        return id;
    }
}
