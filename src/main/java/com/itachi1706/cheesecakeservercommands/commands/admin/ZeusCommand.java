package com.itachi1706.cheesecakeservercommands.commands.admin;

import com.itachi1706.cheesecakeservercommands.commands.BaseCommand;
import com.itachi1706.cheesecakeservercommands.damagesource.ZeusDamage;
import com.itachi1706.cheesecakeservercommands.util.ServerPlayerUtil;
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
                .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> strikeOtherPlayer(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int strikeYourself(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "CONSOLE cannot feel the wrath of Zeus");
        } else {
            ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
            kaboom(player);
            TextUtil.sendAdminChatMessage(sender, "Made own self suffer the wrath of Zeus");
        }

        return Command.SINGLE_SUCCESS;
    }

    private int strikeOtherPlayer(CommandSourceStack sender, ServerPlayer player) {
        if (player == null) {
            sendFailureMessage(sender, "Player not found");
        } else {
            kaboom(player);
            sendSuccessMessage(sender, ChatFormatting.GOLD + "Made " + player.getName().getString() + " suffer the wrath of Zeus!");
            TextUtil.sendAdminChatMessage(sender, "Made " + player.getName().getString() + " suffer the wrath of Zeus");
        }

        return Command.SINGLE_SUCCESS;
    }

    private void kaboom(ServerPlayer player) {
        LightningBolt kaboom = EntityType.LIGHTNING_BOLT.create(player.getLevel());
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
