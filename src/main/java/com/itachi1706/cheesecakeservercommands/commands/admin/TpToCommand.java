package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.objects.TeleportLocation;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
import com.itachi1706.cheesecakeservercommands.util.TeleportHelper;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class TpToCommand extends BaseCommand {
    public TpToCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> teleportToPlayer(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int teleportToPlayer(CommandSourceStack sender, ServerPlayer player) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "CONSOLE cannot teleport");
            return 0;
        }

        ServerPlayer source = ServerPlayerUtil.castToPlayer(sender);
        if (source == null) {
            sendFailureMessage(sender,  sender.getTextName() + " cannot teleport");
            return 0;
        }

        TeleportLocation tp = TeleportHelper.getTeleportLocation(player);
        TeleportHelper.teleportPlayer(source, tp.level(), tp.position(), tp.chunkPos(), tp.yRot(), tp.xRot());

        sendSuccessMessage(sender, ChatFormatting.GOLD + "Teleporting to " + player.getName().getString());
        TextUtil.sendAdminChatMessage(sender, "Teleported to " + player.getName().getString());
        return Command.SINGLE_SUCCESS;
    }
}
