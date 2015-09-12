package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.mojangcmd.MojangPremiumPlayer;
import com.itachi1706.cheesecakeservercommands.mojangcmd.MojangStatusChecker;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
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
    /mojang premium
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
        iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "/mojang premium"
                + EnumChatFormatting.AQUA + " Check if name is purchased"));
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
                return;
            }

            printMojangStatus(iCommandSender);
            return;
        }

        if (subCommand.equalsIgnoreCase("premium")){
            if (astring.length != 2){
                sendHelp(iCommandSender);
                return;
            }

            getPremium(iCommandSender, astring[1]);
            return;
        }

        sendHelp(iCommandSender);

    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] typedValue) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "status", "premium");
        if (typedValue.length == 2 && typedValue[0].equalsIgnoreCase("premium"))
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, MinecraftServer.getServer().getAllUsernames());
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

    private void printMojangStatus(ICommandSender sender){
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=================================================="));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.BLUE + "                Mojang Server Status"));
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=================================================="));
        for (MojangStatusChecker statusChecker : MojangStatusChecker.values()) {
            String service = statusChecker.getName();
            MojangStatusChecker.Status status = statusChecker.getStatus(true);

            sender.addChatMessage(new ChatComponentText(service + ": " + status.getColor() + status.getStatus() + " - " + status.getDescription()));
        }
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "=================================================="));
    }

    public void getPremium(ICommandSender sender, String name){
        int returnCode = MojangPremiumPlayer.isPremium(name);
        if (returnCode == 1){
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + name + EnumChatFormatting.DARK_PURPLE + " is a " + EnumChatFormatting.GREEN + "premium" + EnumChatFormatting.DARK_PURPLE + " status player!"));
        } else if (returnCode == 0){
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + name + EnumChatFormatting.DARK_PURPLE + " is a " + EnumChatFormatting.RED + "non-premium" + EnumChatFormatting.DARK_PURPLE + " status player!"));
        } else if (returnCode == 2){
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An error had occured. Check the console for details!"));
        }
    }

}
