package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.mojangcmd.MojangPremiumPlayer;
import com.itachi1706.cheesecakeservercommands.mojangcmd.MojangStatusChecker;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
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
    @Nonnull
    public String getName() {
        return "mojang";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/mojang help";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    private void sendHelp(ICommandSender iCommandSender){
        ChatHelper.sendMessage(iCommandSender, "Commands List:");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/mojang status"
                + TextFormatting.AQUA + " View Mojang Server Status");
        ChatHelper.sendMessage(iCommandSender, TextFormatting.GOLD + "/mojang premium"
                + TextFormatting.AQUA + " Check if name is purchased" + TextFormatting.RED + " (BROKEN)");
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) throws CommandException {

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
            iCommandSender.sendMessage(new TextComponentString(TextFormatting.RED + "Command has been broken by Mojang"));
            // TODO: Broken by Mojang
            /*if (args.length != 2){
                sendHelp(iCommandSender);
                return;
            }

            getPremium(iCommandSender, args[1]);*/
            return;
        }

        sendHelp(iCommandSender);

    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, "status", "premium");
        if (args.length == 2 && args[0].equalsIgnoreCase("premium"))
            return CommandBase.getListOfStringsMatchingLastWord(args, ServerUtil.getServerInstance().getOnlinePlayerNames());
        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender) {
        return true;
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int p_82358_2_) {
        return false;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private void printMojangStatus(ICommandSender sender){
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "==================================================");
        ChatHelper.sendMessage(sender, TextFormatting.BLUE + "              Mojang Server Checker Status");
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "==================================================");
        for (MojangStatusChecker statusChecker : MojangStatusChecker.values()) {
            String service = statusChecker.getName();
            MojangStatusChecker.Status status = statusChecker.getStatus(true);

            ChatHelper.sendMessage(sender, service + ": " + status.getColor() + status.getStatus() + " - " + status.getDescription());
        }
        ChatHelper.sendMessage(sender, TextFormatting.GOLD + "==================================================");
    }

    public void getPremium(ICommandSender sender, String name){
        int returnCode = MojangPremiumPlayer.isPremium(name);
        if (returnCode == 1){
            ChatHelper.sendMessage(sender, TextFormatting.GOLD + name + TextFormatting.DARK_PURPLE + " is a " + TextFormatting.GREEN + "premium" + TextFormatting.DARK_PURPLE + " status player!");
        } else if (returnCode == 0){
            ChatHelper.sendMessage(sender, TextFormatting.GOLD + name + TextFormatting.DARK_PURPLE + " is a " + TextFormatting.RED + "non-premium" + TextFormatting.DARK_PURPLE + " status player!");
        } else if (returnCode == 2){
            ChatHelper.sendMessage(sender, TextFormatting.RED + "An error had occured. Check the console for details!");
        }
    }

}
