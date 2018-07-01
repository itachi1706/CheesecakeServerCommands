package com.itachi1706.cheesecakeservercommands.server.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class ServerSettingsCommand implements ICommand {

    private List<String> aliases;
    private static List<String> options = Arrays.asList("allowFlight", "allowPVP", "buildLimit", "difficulty", "spawnProtection", "gamemode");

    public ServerSettingsCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("serversettings");
    }

    @Override
    public String getName() {
        return "serversettings";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/serversettings <option> [value]";
    }

    @Override
    public List getAliases() {
        return this.aliases;
    }

    @SideOnly(Side.SERVER)
    public void doSetProperty(String id, Object value)
    {
        DedicatedServer server = (DedicatedServer) ServerUtil.getServerInstance();
        server.setProperty(id, value);
        server.saveProperties();
    }

    public void setProperty(String id, Object value)
    {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            doSetProperty(id, value);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {
        if (!ServerUtil.getServerInstance().isDedicatedServer()) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "You can only use this command on dedicated servers");
            return;
        }

        if (args.length == 0) {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Usage: /serversettings <option> [value]");
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Options: " + TextFormatting.RESET + StringUtils.join(options, ", "));
            return;
        }

        String subCmd = args[0].toLowerCase();
        boolean setValue = false;
        String value = "";
        if (args.length == 2) {
            setValue = true;
            value = args[1];
        }
        if (subCmd.equalsIgnoreCase("allowflight")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Allow flight: %s", Boolean.toString(server.isFlightAllowed())));
            else {
                boolean allowFlight = CommandBase.parseBoolean(value);
                server.setAllowFlight(allowFlight);
                setProperty("allow-flight", allowFlight);
                ChatHelper.sendMessage(iCommandSender, String.format("Allow flight set to %s", Boolean.toString(allowFlight)));
            }

        } else if (subCmd.equalsIgnoreCase("allowpvp")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Allow PvP: %s", Boolean.toString(server.isPVPEnabled())));
            else {
                boolean allowPvP = CommandBase.parseBoolean(value);
                server.setAllowPvp(allowPvP);
                setProperty("pvp", allowPvP);
                ChatHelper.sendMessage(iCommandSender, String.format("PvP set to %s", Boolean.toString(allowPvP)));
            }

        } else if (subCmd.equalsIgnoreCase("buildlimit")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Build Limit: %d", server.getBuildLimit()));
            else {
                int buildlimit = CommandBase.parseInt(value, 1, 256);
                server.setBuildLimit(buildlimit);
                setProperty("max-build-height", buildlimit);
                ChatHelper.sendMessage(iCommandSender, String.format("Build Limit set to %d", buildlimit));
            }

        } else if (subCmd.equalsIgnoreCase("spawnprotection")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Spawn protection size: %d", server.getSpawnProtectionSize()));
            else {
                int spawnprotection = CommandBase.parseInt(value, 0);
                setProperty("spawn-protection", spawnprotection);
                ChatHelper.sendMessage(iCommandSender, String.format("Set spawn-protection to %d", spawnprotection));
            }

        } else if (subCmd.equalsIgnoreCase("gamemode")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Default Gamemode: %s", server.getGameType().getName()));
            else {
                GameType gamemode = GameType.getByID(getGamemode(value));
                server.setGameType(gamemode);
                setProperty("gamemode", gamemode.ordinal());
                ChatHelper.sendMessage(iCommandSender, String.format("Default Gamemode set to %s", gamemode.getName()));
            }

        } else if (subCmd.equalsIgnoreCase("difficulty")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Difficulty: %s", server.getDifficulty()));
            else {
                EnumDifficulty difficulty = EnumDifficulty.getDifficultyEnum(getDifficulty(value));
                server.setDifficultyForAllWorlds(difficulty);
                setProperty("difficulty", difficulty.ordinal());
                ChatHelper.sendMessage(iCommandSender, String.format("Difficulty set to %s", difficulty.name()));
            }

        } else {
            ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Invalid Usage. Usage: /serversettings <option> [value]");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            String[] optionArray = new String[options.size()];
            optionArray = options.toArray(optionArray);
            return CommandBase.getListOfStringsMatchingLastWord(args, optionArray);
        }
        return Collections.emptyList();
    }

    private int getDifficulty(String difficulty) {
        if (difficulty.equalsIgnoreCase("peaceful") || difficulty.equalsIgnoreCase("0")) {
            return EnumDifficulty.PEACEFUL.getDifficultyId();
        } else if (difficulty.equalsIgnoreCase("easy") || difficulty.equalsIgnoreCase("1")) {
            return EnumDifficulty.EASY.getDifficultyId();
        } else if (difficulty.equalsIgnoreCase("normal") || difficulty.equalsIgnoreCase("2")) {
            return EnumDifficulty.NORMAL.getDifficultyId();
        } else if (difficulty.equalsIgnoreCase("hard") || difficulty.equalsIgnoreCase("3")) {
            return EnumDifficulty.HARD.getDifficultyId();
        } else {
            return 0;
        }
    }

    private int getGamemode(String gamemode) {
        if (gamemode.equalsIgnoreCase("survival") || gamemode.equalsIgnoreCase("s") || gamemode.equals("0")) {
            return GameType.SURVIVAL.getID();
        } else if (gamemode.equalsIgnoreCase("adventure") || gamemode.equalsIgnoreCase("a") || gamemode.equals("2")) {
            return GameType.ADVENTURE.getID();
        } else if (gamemode.equalsIgnoreCase("creative") || gamemode.equalsIgnoreCase("c") || gamemode.equals("1")) {
            return GameType.CREATIVE.getID();
        } else if (gamemode.equalsIgnoreCase("spectator") || gamemode.equalsIgnoreCase("sp") || gamemode.equals("3")) {
            return GameType.SPECTATOR.getID();
        }else {
            return 0;
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
