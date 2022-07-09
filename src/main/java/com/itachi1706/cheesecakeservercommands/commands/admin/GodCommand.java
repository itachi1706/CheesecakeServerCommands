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

public class GodCommand extends BaseCommand {

    private static final String STATUS_ENABLED = "Enabled";
    private static final String STATUS_DISABLED = "Disabled";
    private static final String STATUS_INVULNERABLE = "Invulnerable";
    private static final String STATUS_VULNERABLE = "Vulnerable";

    public GodCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> toggleGodModeToMe(context.getSource()))
                .then(Commands.argument("players", EntityArgument.player())
                        .executes(context -> toggleGodModeToOthers(context.getSource(), EntityArgument.getPlayer(context, "players"))));
    }

    private int toggleGodModeToMe(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "Cannot set invulnerability status of CONSOLE", "Cannot set " + sender.getTextName() + "'s invulnerability status");
        if (player == null) {
            return 0; // Already sent message
        }

        player.getAbilities().invulnerable = !player.getAbilities().invulnerable;
        player.onUpdateAbilities();

        sendSuccessMessage(sender, "God Mode " + (player.getAbilities().invulnerable ? ChatFormatting.GREEN + STATUS_ENABLED : ChatFormatting.RED + STATUS_DISABLED));
        TextUtil.sendAdminChatMessage(sender, "Set own invulnerability to " + (player.getAbilities().invulnerable ? STATUS_INVULNERABLE : STATUS_VULNERABLE));

        return Command.SINGLE_SUCCESS;
    }

    private int toggleGodModeToOthers(CommandSourceStack sender, ServerPlayer player) {
        player.getAbilities().invulnerable = !player.getAbilities().invulnerable;
        player.onUpdateAbilities();

        sendSuccessMessage(sender, "Set " + player.getName().getString() + " invulnerability to " + (player.getAbilities().invulnerable ? ChatFormatting.GREEN + STATUS_INVULNERABLE : ChatFormatting.RED + STATUS_VULNERABLE));
        sendMessage(player, "God Mode " + (player.getAbilities().invulnerable ? ChatFormatting.GREEN + STATUS_ENABLED : ChatFormatting.RED + STATUS_DISABLED));
        TextUtil.sendAdminChatMessage(sender, "Set " + player.getName().getString() + " invulnerability to " + (player.getAbilities().invulnerable ? STATUS_INVULNERABLE : STATUS_VULNERABLE));

        return Command.SINGLE_SUCCESS;
    }
}
