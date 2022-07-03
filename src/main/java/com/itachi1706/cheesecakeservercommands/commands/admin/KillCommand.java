package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.CheesecakeServerCommands;
import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class KillCommand extends BaseCommand {

    private static final String ARG_PLAYER = "player";
    private static final String MSG_KILLED = "Killed ";

    public KillCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> killYourself(context.getSource()))
                .then(Commands.argument(ARG_PLAYER, EntityArgument.players())
                        .executes(context -> killOthers(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER)))
                        .then(Commands.argument("cause", StringArgumentType.string())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(CheesecakeServerCommands.getKnownDamageSources().keySet(), builder))
                                .executes(context -> killOthersWithCause(context.getSource(), EntityArgument.getPlayer(context, ARG_PLAYER), StringArgumentType.getString(context, "cause")))));
    }

    private int killYourself(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot kill CONSOLE");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot kill" + sender.getTextName());
            return 0;
        }

        boolean wasInvulnerable = player.getAbilities().invulnerable;
        player.getAbilities().invulnerable = false;
        player.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
        player.kill();
        player.getAbilities().invulnerable = wasInvulnerable;
        sendSuccessMessage(sender, ChatFormatting.GOLD + "You were slain");
        TextUtil.sendAdminChatMessage(sender, "Took their own life");

        return Command.SINGLE_SUCCESS;
    }

    private int killOthers(CommandSourceStack sender, ServerPlayer player) {
        boolean wasInvulnerable = player.getAbilities().invulnerable;
        player.getAbilities().invulnerable = false;
        player.hurt(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
        player.getAbilities().invulnerable = wasInvulnerable;
        sendSuccessMessage(sender, ChatFormatting.GOLD + MSG_KILLED + player.getName().getString());
        TextUtil.sendAdminChatMessage(sender, MSG_KILLED + player.getName().getString());

        return Command.SINGLE_SUCCESS;
    }

    private int killOthersWithCause(CommandSourceStack sender, ServerPlayer player, String cause) {
        if (CheesecakeServerCommands.getKnownDamageSources().containsKey(cause)) {
            boolean wasInvulnerable = player.getAbilities().invulnerable;
            player.getAbilities().invulnerable = false;
            player.hurt(CheesecakeServerCommands.getKnownDamageSources().get(cause), Float.MAX_VALUE);
            player.getAbilities().invulnerable = wasInvulnerable;
            sendSuccessMessage(sender, ChatFormatting.GOLD + MSG_KILLED + player.getName().getString() + " with " + cause);
            TextUtil.sendAdminChatMessage(sender, MSG_KILLED + player.getName().getString() + " with " + cause);

            return Command.SINGLE_SUCCESS;
        } else {
            return killOthers(sender, player);
        }
    }
}
