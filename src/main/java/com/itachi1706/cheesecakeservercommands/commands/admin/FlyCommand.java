package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.itachi1706.cheesecakeservercommands.util.WorldUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class FlyCommand extends BaseCommand {

    private static final String STATUS_ENABLED = "Enabled";
    private static final String STATUS_DISABLED = "Disabled";

    public FlyCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> toggleMyFlight(context.getSource()))
                .then(Commands.argument("players", EntityArgument.player())
                        .executes(context -> toggleOthersFlight(context.getSource(), EntityArgument.getPlayer(context, "players"))));
    }

    private int toggleMyFlight(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot set flight status of CONSOLE");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot set " + sender.getTextName() + "'s flight status");
            return 0;
        }

        player.getAbilities().mayfly = !player.getAbilities().mayfly;
        if (!player.isOnGround())
            player.getAbilities().flying = player.getAbilities().mayfly;
        if (!player.getAbilities().mayfly)
            WorldUtil.placeInWorld(player);
        player.onUpdateAbilities();

        sendSuccessMessage(sender, "Flight Mode " + (player.getAbilities().mayfly ? ChatFormatting.GREEN + STATUS_ENABLED : ChatFormatting.RED + STATUS_DISABLED));
        TextUtil.sendAdminChatMessage(sender, "Set own flight mode to " + (player.getAbilities().mayfly ? STATUS_ENABLED : STATUS_DISABLED));

        return Command.SINGLE_SUCCESS;
    }

    private int toggleOthersFlight(CommandSourceStack sender, ServerPlayer player) {
        player.getAbilities().mayfly = !player.getAbilities().mayfly;
        if (!player.isOnGround())
            player.getAbilities().flying = player.getAbilities().mayfly;
        if (!player.getAbilities().mayfly)
            WorldUtil.placeInWorld(player);
        player.onUpdateAbilities();

        sendSuccessMessage(sender, "Set " + player.getName().getString() + " flight mode to " + (player.getAbilities().mayfly ? ChatFormatting.GREEN + STATUS_ENABLED : ChatFormatting.RED + STATUS_DISABLED));
        sendMessage(player, "Flight Mode has been set to " + (player.getAbilities().mayfly ? ChatFormatting.GREEN + STATUS_ENABLED : ChatFormatting.RED + STATUS_DISABLED));
        TextUtil.sendAdminChatMessage(sender, "Set " + player.getName().getString() + " flight mode to " + (player.getAbilities().mayfly ? STATUS_ENABLED : STATUS_DISABLED));

        return Command.SINGLE_SUCCESS;
    }
}
