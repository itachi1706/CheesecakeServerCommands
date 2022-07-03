package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class BurnCommand extends BaseCommand {

    public BurnCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> burnMe(context.getSource()))
                .then(Commands.argument("players", EntityArgument.player())
                        .executes(context -> burnOthers(context.getSource(), EntityArgument.getPlayer(context, "players"))));
    }

    private int burnMe(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot burn CONSOLE");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot burn " + sender.getTextName());
            return 0;
        }

        player.setSecondsOnFire(15);

        sendSuccessMessage(sender, ChatFormatting.GOLD + "You were burned");
        TextUtil.sendAdminChatMessage(sender, "Set own self on fire");

        return Command.SINGLE_SUCCESS;
    }

    private int burnOthers(CommandSourceStack sender, ServerPlayer player) {
        player.setSecondsOnFire(15);

        sendSuccessMessage(sender, "Burned " + player.getName().getString());
        sendMessage(player, ChatFormatting.GOLD + "You were burned");
        TextUtil.sendAdminChatMessage(sender, "Set " + player.getName().getString() + " on fire");

        return Command.SINGLE_SUCCESS;
    }
}
