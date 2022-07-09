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

public class EnderChestCommand extends BaseCommand {
    public EnderChestCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> openOwnEnderChest(context.getSource()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> openEnderChestForPlayer(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int openOwnEnderChest(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot open the ender chest of CONSOLE", "Cannot open the ender chest of " + sender.getTextName());
        if (player == null) {
            return 0;
        }

        ServerPlayerUtil.openEnderChest(player, player, "Ender Chest");

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Opened Ender Chest");
        TextUtil.sendAdminChatMessage(sender, "Opened Ender Chest");

        return Command.SINGLE_SUCCESS;
    }

    private int openEnderChestForPlayer(CommandSourceStack sender, ServerPlayer player) {
        ServerPlayerUtil.openEnderChest(player, player, "Ender Chest");

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Opened Ender Chest for " + player.getDisplayName().getString());
        TextUtil.sendAdminChatMessage(sender, "Opened Ender Chest for " + player.getDisplayName().getString());
        sendMessage(player, ChatFormatting.GOLD + "Opened Ender Chest");

        return Command.SINGLE_SUCCESS;
    }
}
