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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class FlingCommand extends BaseCommand {

    public FlingCommand(String name, int permissionLevel, boolean enabled) {
        super(name, permissionLevel, enabled);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> setExecution() {
        return builder.executes(context -> flingMyself(context.getSource()))
                .then(Commands.argument("players", EntityArgument.player())
                        .executes(context -> flingOthers(context.getSource(), EntityArgument.getPlayer(context, "players"))));
    }

    private int flingMyself(CommandSourceStack sender) {
        if (!ServerPlayerUtil.isPlayer(sender)) {
            sendFailureMessage(sender, "Cannot fling CONSOLE");
            return 0;
        }

        ServerPlayer player = ServerPlayerUtil.castToPlayer(sender);
        if (player == null) {
            sendFailureMessage(sender, "Cannot fling " + sender.getTextName());
            return 0;
        }

        flingPlayer(player);

        sendSuccessMessage(sender, ChatFormatting.DARK_PURPLE + "Your were flung into the air");
        TextUtil.sendAdminChatMessage(sender, "Flung own self into the air!");

        return Command.SINGLE_SUCCESS;
    }

    private int flingOthers(CommandSourceStack sender, ServerPlayer player) {
        flingPlayer(player);

        sendSuccessMessage(sender, "Flung " + player.getName().getString() + " into the air");
        sendMessage(player, ChatFormatting.DARK_PURPLE + "You were flung into the air");
        TextUtil.sendAdminChatMessage(sender, "Flung " + player.getName().getString() + " into the air");

        return Command.SINGLE_SUCCESS;
    }

    private void flingPlayer(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 100, true, false));
        ServerLevel level = player.getLevel();
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, player.getX(), player.getY(), player.getZ(), 1, 0.4D, 0.4D, 0.4D, 0.4D);
        level.explode(player, player.getX(), player.getY(), player.getZ(), 0, true, Explosion.BlockInteraction.NONE);

        // Trying to send motion to player object is annoying sigh
        player.setDeltaMovement(new Vec3(0, 10, 0));
        player.connection.send(new ClientboundSetEntityMotionPacket(player));
    }
}
