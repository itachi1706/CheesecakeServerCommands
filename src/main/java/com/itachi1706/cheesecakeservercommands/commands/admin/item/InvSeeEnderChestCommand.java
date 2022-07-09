package com.itachi1706.cheesecakeservercommands.commands.admin.item;

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

public class InvSeeEnderChestCommand extends BaseCommand {
    public InvSeeEnderChestCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> openOwnEnderChest(context.getSource()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> openEnderChestForPlayer(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int openOwnEnderChest(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot invsee ender chest for CONSOLE", "Cannot invsee ender chests for " + sender.getTextName());
        if (player == null) {
            return 0;
        }

        ServerPlayerUtil.openEnderChest(player, player, "Ender Chest");

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Viewing your own Ender Chest");
        TextUtil.sendAdminChatMessage(sender, "Viewing own Ender Chest");

        return Command.SINGLE_SUCCESS;
    }

    private int openEnderChestForPlayer(CommandSourceStack sender, ServerPlayer player) {
        ServerPlayer source = ensureIsPlayer(sender, "Cannot invsee other's ender chest for CONSOLE", "Cannot invsee other's ender chests for " + sender.getTextName());
        if (source == null) {
            return 0;
        }
        ServerPlayerUtil.openEnderChest(source, player, player.getName().getString() + "'s Ender Chest");

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Viewing " + player.getDisplayName().getString() + "'s Ender Chest");
        TextUtil.sendAdminChatMessage(sender, "Viewing Ender Chest of " + player.getDisplayName().getString());

        return Command.SINGLE_SUCCESS;
    }
}
