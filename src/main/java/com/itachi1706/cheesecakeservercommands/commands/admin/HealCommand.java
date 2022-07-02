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

public class HealCommand extends BaseCommand {

    public HealCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> healMe(context.getSource()))
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> healOthers(context.getSource(), EntityArgument.getPlayer(context, "players"))));
    }

    private int healMe(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot heal CONSOLE");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot heal " + sender.getTextName());
            return 0;
        }

        float toHeal = player.getMaxHealth() - player.getHealth();
        player.heal(toHeal);
        player.clearFire();
        player.getFoodData().eat(20, 1.0F);

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Your were healed");
        TextUtil.sendAdminChatMessage(sender, "Restored own health");

        return Command.SINGLE_SUCCESS;
    }

    private int healOthers(CommandSourceStack sender, ServerPlayer player) {
        float toHeal = player.getMaxHealth() - player.getHealth();
        player.heal(toHeal);
        player.clearFire();
        player.getFoodData().eat(20, 1.0F);

        sendSuccessMessage(sender, "Healed " + player.getName().getString());
        sendMessage(player, ChatFormatting.GOLD + "Your were healed");
        TextUtil.sendAdminChatMessage(sender, "Restored " + player.getName().getString() + "'s Health");

        return Command.SINGLE_SUCCESS;
    }
}
