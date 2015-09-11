package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
public class CCLoggerCommand implements ICommand {

    private List<String> aliases;

    /*
    Commands List
    /cheesecakelogger viewlogins <player> <#>
    /cheesecakelogger viewplayerstats <player> <#>
    /cheesecakelogger delloginhistory <player>
    /cheesecakelogger help
     */

    public CCLoggerCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("cheesecakelogger");
        this.aliases.add("cclogger");
    }

    @Override
    public String getCommandName() {
        return "cheesecakelogger";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "View Command Help: " + EnumChatFormatting.GOLD + "/cheesecakelogger help";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    private void sendHelp(ICommandSender iCommandSender){
        iCommandSender.addChatMessage(new ChatComponentText("Commands List:"));
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/cclogger viewlogins <player> <#>"
                + EnumChatFormatting.AQUA + " View Player Login Info"));
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/cclogger viewplayerstats <player> <#>"
                + EnumChatFormatting.AQUA + " View Player Stats"));
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/cclogger delloginhistory <player>"
                + EnumChatFormatting.AQUA + " Delete Player History"));
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if(astring.length == 0)
        {
            sendHelp(iCommandSender);
            return;
        }

        String subCommand = astring[0];

        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "TODO"));

    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] astring) {
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

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
