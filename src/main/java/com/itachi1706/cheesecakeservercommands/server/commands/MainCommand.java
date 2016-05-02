package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

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
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if(astring.length == 0)
        {
            ChatHelper.sendMessage(iCommandSender, "/cheesecakeservercommands list");
            ChatHelper.sendMessage(iCommandSender, "/cheesecakeservercommands modulehelp");
            return;
        }

        String subCommand = astring[0];
        if (subCommand.equalsIgnoreCase("list")) {
            listModules(iCommandSender);
        } else if (subCommand.equalsIgnoreCase("modulehelp")) {
            if (astring.length < 2)
                ChatHelper.sendMessage(iCommandSender, EnumChatFormatting.RED + "Please select a module. View modules with /csc list");
            else
                listCommands(astring[1], iCommandSender);
        }

    }

    public void listCommands(String modules, ICommandSender sender) {
        boolean isOp = PlayerMPUtil.isOperatorOrConsole(sender);
        if (modules.equals("cheesecakelogger") || modules.equals("ccl")) {
            if (!isOp) {
                ChatHelper.sendMessage(sender, EnumChatFormatting.RED + "You do not have permission to view help for this module!");
                return;
            }
            ChatHelper.sendMessage(sender, EnumChatFormatting.AQUA + "Cheesecake Logger Module Commands");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/cclogger stats"
                    + EnumChatFormatting.WHITE + " Gets General Statistics Logged.");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/cclogger viewlogins <player> <#>"
                    + EnumChatFormatting.WHITE + " View Player Login Info");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/cclogger viewplayerstats <player>"
                    + EnumChatFormatting.WHITE + " View Player Stats");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/cclogger delloginhistory <player>"
                    + EnumChatFormatting.WHITE + " Delete Player History");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/cclogger lastseen <player>"
                    + EnumChatFormatting.WHITE + " Gets Last Seen of Player");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/cclogger lastknownusername <player/UUID>"
                    + EnumChatFormatting.WHITE + " Get list of last known names of a player");
        } else if (modules.equals("serverproperties")) {
            if (!isOp) {
                ChatHelper.sendMessage(sender, EnumChatFormatting.RED + "You do not have permission to view help for this module!");
                return;
            }
            ChatHelper.sendMessage(sender, EnumChatFormatting.AQUA + "Server Properties Module Commands");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/serverproperties"
                    + EnumChatFormatting.WHITE + " View Server Properties");
        } else if (modules.equals("mojang")) {
            ChatHelper.sendMessage(sender, EnumChatFormatting.AQUA + "Mojang Module Commands");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/mojang status"
                    + EnumChatFormatting.WHITE + " View Mojang Server Status");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/mojang premium"
                    + EnumChatFormatting.WHITE + " Check if name is purchased");
        } else if (modules.equals("admin")) {
            if (!isOp) {
                ChatHelper.sendMessage(sender, EnumChatFormatting.RED + "You do not have permission to view help for this module!");
                return;
            }
            ChatHelper.sendMessage(sender, EnumChatFormatting.AQUA + "Admin Module Commands");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/gmc [player]"
                    + EnumChatFormatting.WHITE + " Set Gamemode to Creative");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/gms [player]"
                    + EnumChatFormatting.WHITE + " Set Gamemode to Survival");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/gma [player]"
                    + EnumChatFormatting.WHITE + " Set Gamemode to Adventure");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/speed <fly/walk/all> <speed/reset> [player]"
                    + EnumChatFormatting.WHITE + " Set Fly/Walk speed of player");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/heal [player]"
                    + EnumChatFormatting.WHITE + " Heals a player");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/smite [player/me/x] [y] [z]"
                    + EnumChatFormatting.WHITE + " Smites a player or location");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/kill [player]"
                    + EnumChatFormatting.WHITE + " Kills yourself or another player");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/invsee [player]"
                    + EnumChatFormatting.WHITE + " Views player inventory");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "/burn [player] [duration]"
                    + EnumChatFormatting.WHITE + " Burns a player");
        } else {
            ChatHelper.sendMessage(sender, EnumChatFormatting.RED + "Invalid Module. View modules with /csc list");
        }
    }

    public void listModules(ICommandSender sender) {
        boolean isOp = PlayerMPUtil.isOperatorOrConsole(sender);
        ChatHelper.sendMessage(sender, "Modules List");
        if (isOp) {
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "serverproperties");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "cheesecakelogger");
            ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "admin");
        }
        ChatHelper.sendMessage(sender, EnumChatFormatting.GOLD + "mojang");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
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
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return true;
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
