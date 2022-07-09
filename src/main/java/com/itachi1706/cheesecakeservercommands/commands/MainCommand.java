package com.itachi1706.cheesecakeservercommands.commands;

import com.itachi1706.cheesecakeservercommands.objects.HelpInitializer;
import com.itachi1706.cheesecakeservercommands.objects.HelpMain;
import com.itachi1706.cheesecakeservercommands.objects.HelpSub;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Arrays;

public class MainCommand extends BaseCommand {

    private final HelpMain[] mainHelp;
    
    private static final String ARG_MODULE_HELP = "modulehelp";
    private static final String ARG_ADMIN_SILENCE = "adminsilence";

    public MainCommand(String command, int permissionLevel, boolean enabled) {
        super(command, permissionLevel, enabled);
        this.mainHelp = HelpInitializer.initialize();
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> listMain(context.getSource()))
                .then(Commands.literal("list").executes(context -> subCmd(context.getSource(), "list")))
                .then(Commands.literal(ARG_MODULE_HELP).executes(context -> subCmd(context.getSource(), ARG_MODULE_HELP))
                        .then(Commands.argument("module", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(Arrays.stream(this.mainHelp).map(HelpMain::getKey).toList(), builder))
                                .executes(context -> listCommands(context.getSource(), StringArgumentType.getString(context, "module")))))
                .then(Commands.literal(ARG_ADMIN_SILENCE).executes(context -> subCmd(context.getSource(), ARG_ADMIN_SILENCE)));
    }

    private int listMain(CommandSourceStack sender) {
        sendSuccessMessage(sender, "/cheesecakeservercommands list");
        sendSuccessMessage(sender, "/cheesecakeservercommands modulehelp");
        return Command.SINGLE_SUCCESS;
    }

    private int subCmd(CommandSourceStack sender, String arg) {
        switch (arg) {
            case "list" -> listModules(sender);
            case ARG_MODULE_HELP -> sendFailureMessage(sender, ChatFormatting.RED + "Please select a module. View modules with /csc list");
            case ARG_ADMIN_SILENCE -> sendFailureMessage(sender, "Please use /adminsilence instead now");
            default -> sendFailureMessage(sender, "Invalid command");
        }
        return Command.SINGLE_SUCCESS;
    }

    private void listModules(CommandSourceStack sender) {
        boolean isOp = hasPermission(sender);
        sendSuccessMessage(sender, "Modules List");
        // Retrieve Help
        for (HelpMain main : mainHelp) {
            if (main.isAdminOnly() && !isOp) continue;
            sendSuccessMessage(sender, ChatFormatting.GOLD + main.getKey());
        }
    }

    private int listCommands(CommandSourceStack sender, String modules) {
        boolean isOp = hasPermission(sender);
        
        HelpMain found = null;
        for (HelpMain main : mainHelp) {
            // Try to find
            if (main.getKey().equalsIgnoreCase(modules)) {
                if (main.isAdminOnly() && !isOp) {
                    sendFailureMessage(sender, ChatFormatting.RED + "You do not have permission to view help for this module!");
                    return 0;
                }
                found = main;
                break;
            }
        }
        if (found == null) {
            sendFailureMessage(sender, ChatFormatting.RED + "Invalid Module. View modules with /csc list");
            return 0;
        }
        HelpSub[] subHelps = found.getCommands();
        sendSuccessMessage(sender, ChatFormatting.AQUA + found.getName());
        for (HelpSub sub : subHelps) {
            sendSuccessMessage(sender, ChatFormatting.GOLD + sub.getCommand() + ChatFormatting.WHITE + " " + sub.getUsage());
        }
        return Command.SINGLE_SUCCESS;
    }
}
