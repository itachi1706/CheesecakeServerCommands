package com.itachi1706.cheesecakeservercommands.server.commands.admin.server;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO: Add to Main Command

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class ServerSettingsCommand implements ICommand {

    private List<String> aliases;
    private static List<String> options = Arrays.asList("allowFlight", "allowPVP", "buildLimit", "difficulty", "spawnProtection", "gamemode");

    public ServerSettingsCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("serversettings");
    }

    @Override
    public String getCommandName() {
        return "serversettings";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "serversettings <option> [value]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @SideOnly(Side.SERVER)
    public void doSetProperty(String id, Object value)
    {
        DedicatedServer server = (DedicatedServer) FMLCommonHandler.instance().getMinecraftServerInstance();
        server.setProperty(id, value);
        server.saveProperties();
    }

    public void setProperty(String id, Object value)
    {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER)
            doSetProperty(id, value);
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {
        if (!FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "You can only use this command on dedicated servers");
            return;
        }
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        if (astring.length == 0) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Usage: /serversettings <option> [value]");
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Options: " + EnumChatFormatting.RESET + StringUtils.join(options, ", "));
            return;
        }

        String subCmd = astring[0].toLowerCase();
        boolean setValue = false;
        String value = "";
        if (astring.length == 2) {
            setValue = true;
            value = astring[1];
        }
        if (subCmd.equalsIgnoreCase("allowflight")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Allow flight: %s", Boolean.toString(server.isFlightAllowed())));
            else {
                boolean allowFlight = Boolean.parseBoolean(value);
                server.setAllowFlight(allowFlight);
                setProperty("allow-flight", allowFlight);
                ChatHelper.sendMessage(iCommandSender, String.format("Allow flight set to %s", Boolean.toString(allowFlight)));
            }

        } else if (subCmd.equalsIgnoreCase("allowpvp")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Allow PvP: %s", Boolean.toString(server.isPVPEnabled())));
            else {
                boolean allowPvP = Boolean.parseBoolean(value);
                server.setAllowPvp(allowPvP);
                setProperty("pvp", allowPvP);
                ChatHelper.sendMessage(iCommandSender, String.format("PvP set to %s", Boolean.toString(allowPvP)));
            }

        } else if (subCmd.equalsIgnoreCase("buildlimit")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Build Limit: %d", server.getBuildLimit()));
            else {
                int buildlimit = 256;
                try {
                    buildlimit = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    buildlimit = 256;
                }
                server.setBuildLimit(buildlimit);
                setProperty("max-build-height", buildlimit);
                ChatHelper.sendMessage(iCommandSender, String.format("Build Limit set to %d", buildlimit));
            }

        } else if (subCmd.equalsIgnoreCase("spawnprotection")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Spawn protection size: %d", server.getSpawnProtectionSize()));
            else {
                int spawnprotection = 16;
                try {
                    spawnprotection = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    spawnprotection = 16;
                }
                setProperty("spawn-protection", spawnprotection);
                ChatHelper.sendMessage(iCommandSender, String.format("Set spawn-protection to %d", spawnprotection));
            }

        } else if (subCmd.equalsIgnoreCase("gamemode")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Default Gamemode: %s", server.getGameType().getName()));
            else {
                WorldSettings.GameType gamemode = WorldSettings.GameType.getByID(getGamemode(value));
                server.setGameType(gamemode);
                setProperty("gamemode", gamemode.ordinal());
                ChatHelper.sendMessage(iCommandSender, String.format("Default Gamemode set to %s", gamemode.getName()));
            }

        } else if (subCmd.equalsIgnoreCase("difficulty")) {
            if (!setValue)
                ChatHelper.sendMessage(iCommandSender, String.format("Difficulty: %s", server.func_147135_j()));
            else {
                EnumDifficulty difficulty = EnumDifficulty.getDifficultyEnum(getDifficulty(value));
                server.func_147139_a(difficulty);
                setProperty("difficulty", difficulty.ordinal());
                ChatHelper.sendMessage(iCommandSender, String.format("Difficulty set to %s", difficulty.name()));
            }

        } else {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Invalid Usage. Usage: /serversettings <option> [value]");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1) {
            String[] optionArray = new String[options.size()];
            optionArray = options.toArray(optionArray);
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, optionArray);
        }
        return null;
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
            return WorldSettings.GameType.SURVIVAL.getID();
        } else if (gamemode.equalsIgnoreCase("adventure") || gamemode.equalsIgnoreCase("a") || gamemode.equals("2")) {
            return WorldSettings.GameType.ADVENTURE.getID();
        } else if (gamemode.equalsIgnoreCase("creative") || gamemode.equalsIgnoreCase("c") || gamemode.equals("1")) {
            return WorldSettings.GameType.CREATIVE.getID();
        } else if (gamemode.equalsIgnoreCase("spectator") || gamemode.equalsIgnoreCase("sp") || gamemode.equals("3")) {
            // TODO: 1.9 game mode spectator
            return 0;
        }else {
            return 0;
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return PlayerMPUtil.isOperatorOrConsole(iCommandSender);
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
