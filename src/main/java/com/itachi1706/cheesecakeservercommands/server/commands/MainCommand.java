package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class MainCommand implements ICommand {

    private List<String> aliases;

    public MainCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("cheesecakeservercommands");
        this.aliases.add("csc");
    }

    @Override
    public String getCommandName() {
        return "cheesecakeservercommands";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "cheesecakeservercommands list";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        if(args.length == 0)
        {
            ChatHelper.sendMessage(iCommandSender, "/cheesecakeservercommands list");
            ChatHelper.sendMessage(iCommandSender, "/cheesecakeservercommands modulehelp");
            return;
        }

        String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("list")) {
            listModules(iCommandSender);
        } else if (subCommand.equalsIgnoreCase("modulehelp")) {
            if (args.length < 2)
                ChatHelper.sendMessage(iCommandSender, ChatFormatting.RED + "Please select a module. View modules with /csc list");
            else
                listCommands(args[1], iCommandSender);
        }

    }

    public void listCommands(String modules, ICommandSender sender) {
        boolean isOp = PlayerMPUtil.isOperatorOrConsole(sender);
        if (modules.equals("cheesecakelogger") || modules.equals("ccl")) {
            if (!isOp) {
                ChatHelper.sendMessage(sender, ChatFormatting.RED + "You do not have permission to view help for this module!");
                return;
            }
            ChatHelper.sendMessage(sender, ChatFormatting.AQUA + "Cheesecake Logger Module Commands");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/cclogger stats"
                    + ChatFormatting.WHITE + " Gets General Statistics Logged.");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/cclogger viewlogins <player> <#>"
                    + ChatFormatting.WHITE + " View Player Login Info");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/cclogger viewplayerstats <player>"
                    + ChatFormatting.WHITE + " View Player Stats");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/cclogger delloginhistory <player>"
                    + ChatFormatting.WHITE + " Delete Player History");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/cclogger lastseen <player>"
                    + ChatFormatting.WHITE + " Gets Last Seen of Player");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/cclogger lastknownusername <player/UUID>"
                    + ChatFormatting.WHITE + " Get list of last known names of a player");
        } else if (modules.equals("serverproperties")) {
            if (!isOp) {
                ChatHelper.sendMessage(sender, ChatFormatting.RED + "You do not have permission to view help for this module!");
                return;
            }
            ChatHelper.sendMessage(sender, ChatFormatting.AQUA + "Server Properties Module Commands");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/serverproperties"
                    + ChatFormatting.WHITE + " View Server Properties");
        } else if (modules.equals("mojang")) {
            ChatHelper.sendMessage(sender, ChatFormatting.AQUA + "Mojang Module Commands");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/mojang status"
                    + ChatFormatting.WHITE + " View Mojang Server Status");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/mojang premium"
                    + ChatFormatting.WHITE + " Check if name is purchased");
        } else if (modules.equals("admin")) {
            if (!isOp) {
                ChatHelper.sendMessage(sender, ChatFormatting.RED + "You do not have permission to view help for this module!");
                return;
            }
            ChatHelper.sendMessage(sender, ChatFormatting.AQUA + "Admin Module Commands");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/gmc [player]"
                    + ChatFormatting.WHITE + " Set Gamemode to Creative");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/gms [player]"
                    + ChatFormatting.WHITE + " Set Gamemode to Survival");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/gma [player]"
                    + ChatFormatting.WHITE + " Set Gamemode to Adventure");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/gm <creative/adventure/survival> [player]"
                    + ChatFormatting.WHITE + " Set Gamemode of Player");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/speed <fly/walk/all> <speed/reset> [player]"
                    + ChatFormatting.WHITE + " Set Fly/Walk speed of player");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/heal [player]"
                    + ChatFormatting.WHITE + " Heals a player");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/zeus [player]"
                    + ChatFormatting.WHITE + " Let a player suffer the Wrath of Zeus");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/smite [player/me/x] [y] [z]"
                    + ChatFormatting.WHITE + " Smites a player or location");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/kill [player]"
                    + ChatFormatting.WHITE + " Kills yourself or another player");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/kick <player> [reason]"
                    + ChatFormatting.WHITE + " Kicks a player from the server with an optional color coded reason");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/wow [player]"
                    + ChatFormatting.WHITE + " Trolls yourself or another player");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/tpto <player>"
                    + ChatFormatting.WHITE + " Teleports to a player");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/tphere <player>"
                    + ChatFormatting.WHITE + " Teleports another player to you");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/fling [player]"
                    + ChatFormatting.WHITE + " Flings yourself or another player into the air");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/invsee [player]"
                    + ChatFormatting.WHITE + " Views player inventory");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/burn [player] [duration]"
                    + ChatFormatting.WHITE + " Burns a player");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/locate [player]"
                    + ChatFormatting.WHITE + " Locates a player's location");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "/sudo <player> <command>"
                    + ChatFormatting.WHITE + " Runs a command as a user" + ChatFormatting.DARK_RED + " (VERY DANGEROUS)");
        } else {
            ChatHelper.sendMessage(sender, ChatFormatting.RED + "Invalid Module. View modules with /csc list");
        }
    }

    public void listModules(ICommandSender sender) {
        boolean isOp = PlayerMPUtil.isOperatorOrConsole(sender);
        ChatHelper.sendMessage(sender, "Modules List");
        if (isOp) {
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "serverproperties");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "cheesecakelogger");
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "admin");
        }
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "mojang");
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "list", "modulehelp");
        if (typedValue.length == 2 && typedValue[0].equalsIgnoreCase("modulehelp")) {
            boolean isOp = PlayerMPUtil.isOperatorOrConsole(iCommandSender);
            if (isOp) {
                return CommandBase.getListOfStringsMatchingLastWord(typedValue, "mojang", "serverproperties", "cheesecakelogger", "admin");
            }
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "serverproperties");
        }
        return null;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender iCommandSender) {
        return true;
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
