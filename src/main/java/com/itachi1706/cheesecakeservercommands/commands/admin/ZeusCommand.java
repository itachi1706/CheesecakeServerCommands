package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.damagesource.ZeusDamage;
import com.itachi1706.cheesecakeservercommands.util.LogHelper;
import com.itachi1706.cheesecakeservercommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public class ZeusCommand extends BaseCommand {

    public ZeusCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> strikeYourself(context.getSource()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> strikeOtherPlayer(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int strikeYourself(CommandSourceStack sender) {
        ServerPlayer player = ensureIsPlayer(sender, "CONSOLE cannot feel the wrath of Zeus", null);
        if (player == null) {
            return 0; // Already sent message
        }

        kaboom(player);
        TextUtil.sendAdminChatMessage(sender, "Made own self suffer the wrath of Zeus");

        return Command.SINGLE_SUCCESS;
    }

    private int strikeOtherPlayer(CommandSourceStack sender, ServerPlayer player) {
        if (player == null) {
            sendFailureMessage(sender, "Player not found");
            return 0;
        }

        kaboom(player);
        sendSuccessMessage(sender, ChatFormatting.GOLD + "Made " + player.getName().getString() + " suffer the wrath of Zeus!");
        TextUtil.sendAdminChatMessage(sender, "Made " + player.getName().getString() + " suffer the wrath of Zeus");

        return Command.SINGLE_SUCCESS;
    }

    private void kaboom(ServerPlayer player) {
        LightningBolt kaboom = EntityType.LIGHTNING_BOLT.create(player.getLevel());
        if (kaboom == null) {
            LogHelper.error("An error occurred spawning Zeus's lightning bolt on " + player.getName().getString());
            return; // Don't do anything
        }
        kaboom.moveTo(Vec3.atBottomCenterOf(player.blockPosition()));
        kaboom.setVisualOnly(true);
        player.getLevel().addFreshEntity(kaboom);
        player.hurt(getDamageSource(), Float.MAX_VALUE);

        sendMessage(player, ChatFormatting.GOLD + "You angered Zeus and hence suffered the Wrath of Zeus!");
    }

    public static DamageSource getDamageSource() {
        return new ZeusDamage("csczeuswrath")
                .bypassArmor().bypassInvul();
    }
}
