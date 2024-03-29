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

public class HealCommand extends BaseCommand {

    public HealCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> healMe(context.getSource()))
                .then(Commands.argument("players", EntityArgument.player())
                        .executes(context -> healOthers(context.getSource(), EntityArgument.getPlayer(context, "players"))));
    }

    private int healMe(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot heal CONSOLE", "Cannot heal " + sender.getTextName());
        if (player == null) {
            return 0; // Already sent message
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
