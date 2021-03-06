package com.itachi1706.cheesecakeservercommands.server.commands;

import com.itachi1706.cheesecakeservercommands.server.commands.admin.server.AdminSilenceCommand;
import com.itachi1706.cheesecakeservercommands.server.objects.HelpInitializer;
import com.itachi1706.cheesecakeservercommands.server.objects.HelpMain;
import com.itachi1706.cheesecakeservercommands.server.objects.HelpSub;
import com.itachi1706.cheesecakeservercommands.util.ChatHelper;
import com.itachi1706.cheesecakeservercommands.util.PlayerMPUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kenneth on 9/11/2015.
 * for CheesecakeServerCommands in package com.itachi1706.cheesecakeservercommands.server.commands
 */
@SuppressWarnings("unused")
public class MainCommand implements ICommand {

    private List<String> aliases;
    private HelpMain[] mainHelp;

    public MainCommand(){
        this.aliases = new ArrayList<>();
        this.aliases.add("cheesecakeservercommands");
        this.aliases.add("csc");
        this.mainHelp = HelpInitializer.initialize();
    }

    @Override
    @Nonnull
    public String getName() {
        return "cheesecakeservercommands";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/cheesecakeservercommands list";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender iCommandSender, @Nonnull String[] args) {

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
                ChatHelper.sendMessage(iCommandSender, TextFormatting.RED + "Please select a module. View modules with /csc list");
            else
                listCommands(args[1], iCommandSender);
        } else if (subCommand.equalsIgnoreCase("adminsilence")) {
            AdminSilenceCommand.execute(server, iCommandSender, args);
        }

    }

    private void listCommands(String modules, ICommandSender sender) {
        boolean isOp = PlayerMPUtil.isOperatorOrConsole(sender);
        HelpMain found = null;
        for (HelpMain main : mainHelp) {
            // Try to find
            if (main.getKey().equalsIgnoreCase(modules)) {
                if (main.isAdminOnly() && !isOp) {
                    ChatHelper.sendMessage(sender, TextFormatting.RED + "You do not have permission to view help for this module!");
                    return;
                }
                found = main;
                break;
            }
        }
        if (found == null) {
            ChatHelper.sendMessage(sender, TextFormatting.RED + "Invalid Module. View modules with /csc list");
            return;
        }
        HelpSub[] subHelps = found.getCommands();
        ChatHelper.sendMessage(sender, TextFormatting.AQUA + found.getName());
        for (HelpSub sub : subHelps) {
            ChatHelper.sendMessage(sender, TextFormatting.GOLD + sub.getCommand() + TextFormatting.WHITE + " " + sub.getUsage());
        }
    }

    private void listModules(ICommandSender sender) {
        boolean isOp = PlayerMPUtil.isOperatorOrConsole(sender);
        ChatHelper.sendMessage(sender, "Modules List");
        // Retrieve Help
        for (HelpMain main : mainHelp) {
            if (main.isAdminOnly() && !isOp) continue;
            ChatHelper.sendMessage(sender, TextFormatting.GOLD + main.getKey());
        }
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
            return CommandBase.getListOfStringsMatchingLastWord(args, "list", "modulehelp");
        if (args.length == 2 && args[0].equalsIgnoreCase("modulehelp")) {
            boolean isOp = PlayerMPUtil.isOperatorOrConsole(sender);
            List<String> viewable = new ArrayList<>();
            for (HelpMain main : mainHelp) {
                if (main.isAdminOnly() && !isOp) continue;
                viewable.add(main.getKey());
            }

            return CommandBase.getListOfStringsMatchingLastWord(args, viewable);
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("adminsilence")) {
            return AdminSilenceCommand.getTabCompletions(server, sender, args, targetPos);
        }
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
}
