package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.server.level.ServerPlayer;

public class SudoCommand extends BaseCommand {

    public SudoCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("command", MessageArgument.message())
                                .executes(context -> executeCommandAsPlayer(context.getSource(), EntityArgument.getPlayer(context, "player"),
                                        MessageArgument.getMessage(context, "command").getString()))));
    }

    private int executeCommandAsPlayer(CommandSourceStack sender, ServerPlayer player, String command) {
        ServerUtil.getServerInstance().getCommands().performCommand(player.createCommandSourceStack(), command);
        sendSuccessMessage(sender, "Executed \"" + command + "\" as " + player.getName().getString());
        TextUtil.sendAdminChatMessage(sender, "Executed \"" + command + "\" as " + player.getName().getString());

        return Command.SINGLE_SUCCESS;
    }
}
