package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.mojangcmd.MojangPremiumPlayer;
import com.itachi1706.cheesecakeservercommands.mojangcmd.MojangStatusChecker;
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
        this.aliases = new ArrayList<String>();
        this.aliases.add("mojang");
    }

    @Override
    public String getCommandName() {
        return "mojang";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "View Command Help: " + ChatFormatting.GOLD + "/mojang help";
    }

    @Override
    public List getCommandAliases() {
        return this.aliases;
    }

    private void sendHelp(ICommandSender iCommandSender){
        ChatHelper.sendMessage(iCommandSender, "Commands List:");
        ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "/mojang status"
                + ChatFormatting.AQUA + " View Mojang Server Status");
        ChatHelper.sendMessage(iCommandSender, ChatFormatting.GOLD + "/mojang premium"
                + ChatFormatting.AQUA + " Check if name is purchased");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender iCommandSender, String[] args) throws CommandException {

        if(args.length == 0)
        {
            sendHelp(iCommandSender);
            return;
        }

        String subCommand = args[0];

        if (subCommand.equalsIgnoreCase("status")){
            if (args.length != 1){
                sendHelp(iCommandSender);
                return;
            }

            printMojangStatus(iCommandSender);
            return;
        }

        if (subCommand.equalsIgnoreCase("premium")){
            if (args.length != 2){
                sendHelp(iCommandSender);
                return;
            }

            getPremium(iCommandSender, args[1]);
            return;
        }

        sendHelp(iCommandSender);

    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender iCommandSender, String[] typedValue, @Nullable BlockPos pos) {
        if (typedValue.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, "status", "premium");
        if (typedValue.length == 2 && typedValue[0].equalsIgnoreCase("premium"))
            return CommandBase.getListOfStringsMatchingLastWord(typedValue, PlayerMPUtil.getServerInstance().getAllUsernames());
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

    private void printMojangStatus(ICommandSender sender){
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "==================================================");
        ChatHelper.sendMessage(sender, ChatFormatting.BLUE + "              Mojang Server Checker Status");
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "==================================================");
        for (MojangStatusChecker statusChecker : MojangStatusChecker.values()) {
            String service = statusChecker.getName();
            MojangStatusChecker.Status status = statusChecker.getStatus(true);

            ChatHelper.sendMessage(sender, service + ": " + status.getColor() + status.getStatus() + " - " + status.getDescription());
        }
        ChatHelper.sendMessage(sender, ChatFormatting.GOLD + "==================================================");
    }

    public void getPremium(ICommandSender sender, String name){
        int returnCode = MojangPremiumPlayer.isPremium(name);
        if (returnCode == 1){
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + name + ChatFormatting.DARK_PURPLE + " is a " + ChatFormatting.GREEN + "premium" + ChatFormatting.DARK_PURPLE + " status player!");
        } else if (returnCode == 0){
            ChatHelper.sendMessage(sender, ChatFormatting.GOLD + name + ChatFormatting.DARK_PURPLE + " is a " + ChatFormatting.RED + "non-premium" + ChatFormatting.DARK_PURPLE + " status player!");
        } else if (returnCode == 2){
            ChatHelper.sendMessage(sender, ChatFormatting.RED + "An error had occured. Check the console for details!");
        }
    }

}
