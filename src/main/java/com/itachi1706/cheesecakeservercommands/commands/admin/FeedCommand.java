package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class FeedCommand extends BaseCommand {

    public FeedCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> feedMe(context.getSource()))
                .then(Commands.argument("players", EntityArgument.player())
                        .executes(context -> feedOthers(context.getSource(), EntityArgument.getPlayer(context, "players"))));
    }

    private int feedMe(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot feed CONSOLE", "Cannot feed " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
        }

        player.getFoodData().eat(20, 1.0F);

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Your hunger is restored");
        TextUtil.sendAdminChatMessage(sender, "Restored own hunger");

        return Command.SINGLE_SUCCESS;
    }

    private int feedOthers(CommandSourceStack sender, ServerPlayer player) {
        player.getFoodData().eat(20, 1.0F);

        sendSuccessMessage(sender, "Fed " + player.getName().getString());
        sendMessage(player, ChatFormatting.GOLD + "Your hunger is restored");
        TextUtil.sendAdminChatMessage(sender, "Restored " + player.getName().getString() + "'s Hunger");

        return Command.SINGLE_SUCCESS;
    }
}
