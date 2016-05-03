package com.itachi1706.cheesecakeservercommands.server.commands.admin;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class GamemodeCommand implements ICommand {

    private List<String> aliases;

    public GamemodeCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("gm");
    }

    @Override
    public String getCommandName() {
        return "gm";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "gm <creative/adventure/survival> [player]";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if (astring.length == 0) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Usage: /gm <creative/adventure/survival> [player]");
            return;
        }

        String togm = astring[0];

        if(astring.length == 1)
        {
            if (!PlayerMPUtil.isPlayer(iCommandSender)) {
                ChatHelper.sendMessage(iCommandSender, "Cannot set CONSOLE's Game Mode");
                return;
            } else {
                // Gamemode yourself
                EntityPlayerMP player = (EntityPlayerMP) PlayerMPUtil.castToPlayer(iCommandSender);
                if (player == null) {
                    ChatHelper.sendMessage(iCommandSender, "Cannot set " + iCommandSender.getCommandSenderName() + "'s Game Mode");
                    return;
                }
                WorldSettings.GameType gamemode = getGameMode(togm);
                if (gamemode.equals(WorldSettings.GameType.NOT_SET)) {
                    ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Unknown Game Mode.");
                    ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Available modes: creative, adventure, survival");
                    return;
                }
                player.setGameType(gamemode);
                ChatHelper.sendMessage(iCommandSender, "Set own gamemode to " + EnumChatFormatting.GOLD + WordUtils.capitalize(gamemode.getName()));
                ChatHelper.sendAdminMessage(iCommandSender, "Set own gamemode to " + WordUtils.capitalize(gamemode.getName()));
                return;
            }
        }

        // Gamemode others
        String subname = astring[1];
        EntityPlayerMP player = PlayerMPUtil.getPlayer(subname);
        if (player == null) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Player not found");
            return;
        }

        WorldSettings.GameType gamemode = getGameMode(togm);
        if (gamemode.equals(WorldSettings.GameType.NOT_SET)) {
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Unknown Game Mode.");
            ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Available modes: creative, adventure, survival");
            return;
        }
        player.setGameType(WorldSettings.GameType.SURVIVAL);
        ChatHelper.sendMessage(iCommandSender, "Set " + player.getCommandSenderName() + " gamemode to " + EnumChatFormatting.GOLD + WordUtils.capitalize(gamemode.getName()));
        ChatHelper.sendAdminMessage(iCommandSender, "Set " + player.getCommandSenderName() + " gamemode to " + WordUtils.capitalize(gamemode.getName()));
    }

    private WorldSettings.GameType getGameMode(String gamemode) {
        if (gamemode.equalsIgnoreCase("survival") || gamemode.equalsIgnoreCase("s") || gamemode.equals("0")) {
            return WorldSettings.GameType.SURVIVAL;
        } else if (gamemode.equalsIgnoreCase("adventure") || gamemode.equalsIgnoreCase("a") || gamemode.equals("2")) {
            return WorldSettings.GameType.ADVENTURE;
        } else if (gamemode.equalsIgnoreCase("creative") || gamemode.equalsIgnoreCase("c") || gamemode.equals("1")) {
            return WorldSettings.GameType.CREATIVE;
        } else if (gamemode.equalsIgnoreCase("spectator") || gamemode.equalsIgnoreCase("sp") || gamemode.equals("3")) {
            // TODO: 1.9 game mode spectator
            return WorldSettings.GameType.NOT_SET;
        }else {
            return WorldSettings.GameType.NOT_SET;
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "creative", "survival", "adventure");
        }

        boolean showSecond = false;
        if (typedValue[0].equalsIgnoreCase("creative")) showSecond = true;
        if (typedValue[0].equalsIgnoreCase("survival")) showSecond = true;
        if (typedValue[0].equalsIgnoreCase("adventure")) showSecond = true;
        if (typedValue[0].equalsIgnoreCase("s")) showSecond = true;
        if (typedValue[0].equalsIgnoreCase("c")) showSecond = true;
        if (typedValue[0].equalsIgnoreCase("a")) showSecond = true;
        if (typedValue[0].equalsIgnoreCase("0")) showSecond = true;
        if (typedValue[0].equalsIgnoreCase("1")) showSecond = true;
        if (typedValue[0].equalsIgnoreCase("2")) showSecond = true;

        if (typedValue.length == 2 && showSecond)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
        return null;
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
