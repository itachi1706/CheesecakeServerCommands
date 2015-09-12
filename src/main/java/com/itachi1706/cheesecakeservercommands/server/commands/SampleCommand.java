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
@SuppressWarnings("unused")
public class SampleCommand implements ICommand {

    private List<String> aliases;

    public SampleCommand(){
        this.aliases = new ArrayList<String>();
        this.aliases.add("sample");
        this.aliases.add("sam");
    }

    @Override
    public String getCommandName() {
        return "sample";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "sample <text>";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if(astring.length == 0)
        {
            iCommandSender.addChatMessage(new ChatComponentText("Invalid arguments"));
            return;
        }

        iCommandSender.addChatMessage(new ChatComponentText("Sample: " + EnumChatFormatting.AQUA + "[" + astring[0] + "]"));

    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] p_71516_2_) {
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
