package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.enums.MojangStatusChecker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kenneth on 9/12/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
public class MojangServerCommand implements ICommand {

    private List<String> aliases;

    /*
    Commands List
    /mojang status
     */

    public MojangServerCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("mojang");
    }

    @Override
    public String getCommandName() {
        return "mojang";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "View Command Help: " + EnumChatFormatting.GOLD + "/mojang help";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    private void sendHelp(ICommandSender iCommandSender){
        iCommandSender.addChatMessage(new ChatComponentText("Commands List:"));
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/mojang status"
                + EnumChatFormatting.AQUA + " View Mojang Server Status"));
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] astring) {

        if(astring.length == 0)
        {
            sendHelp(iCommandSender);
            return;
        }

        String subCommand = astring[0];

        if (subCommand.equalsIgnoreCase("status")){
            if (astring.length != 1){
                sendHelp(iCommandSender);
            }

            iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=================================================="));
            iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.BLUE + "                Mojang Server Status"));
            iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=================================================="));
            for (MojangStatusChecker statusChecker : MojangStatusChecker.values()) {
                String service = statusChecker.getName();
                MojangStatusChecker.Status status = statusChecker.getStatus(true);

                iCommandSender.addChatMessage(new ChatComponentText(service + ": " + status.getColor() + status.getStatus() + " - " + status.getDescription()));
            }
            iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=================================================="));
            return;
        }

        sendHelp(iCommandSender);

    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "status");
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
